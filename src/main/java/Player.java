import java.	io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
		run(new MyScanner(new Scanner(System.in)), System.out);
    }

	protected static void run(MyCustomScanner in, PrintStream out) {
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        GameRound initialPosition = new GameRound(in, height);
        out.println("WAIT");
        GameRound round = new GameRound(in, height);
        Set<Node> nodes = Node.getNodes(round.map, initialPosition.getNodes(), round.getNodes());
        Game game = new Game(nodes, round.getObstacles(), width, height);
        Queue<Location> bombsToBePlaced = new LinkedList<>();
        
        // game loop
        while (true) {
        	if (bombsToBePlaced.isEmpty()) {
        		bombsToBePlaced.addAll(new BombWithHighestDamage(game).getBombLocations());
        	}
        	Location nextBomb = bombsToBePlaced.poll();
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
        	if (nextBomb == null) {
        		out.println("WAIT");
        	}
        	else {
        		game.placeBombAt(nextBomb);
        		out.println(nextBomb.x+" "+nextBomb.y);
        	}
            if (round.rounds==1) {
            	return;
            }
            round = new GameRound(in, height);
            game.nextRound();
        }
	}
	
	interface BombStrategy {
		List<Location> getBombLocations();
	}
	
	static class BombWithHighestDamage implements BombStrategy {
		private final Game game;
		
		public BombWithHighestDamage(Game game) {
			this.game = game;
		}

		@Override
		public List<Location> getBombLocations() {
			return Arrays.asList(game.getBombableLocations().stream().collect(
					Collectors.toMap(loc -> loc, loc -> game.newBombDamage(loc)))
					.entrySet().stream().max((entry1, entry2) -> entry1.getValue() - entry2.getValue()).get().getKey(), null, null);
		}
		
	}
	
	static class Location {
		private final int x;
		private final int y;

		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Location moveTo(Direction direction) {
			switch (direction) {
			case UP:
				return new Location(x, y - 1);
			case DOWN:
				return new Location(x, y + 1);
			case LEFT:
				return new Location(x - 1, y);
			case RIGHT:
				return new Location(x + 1, y);
			default:
				throw new IllegalArgumentException(direction.toString());
			}
		}

		public boolean isOnTheMapAndNotBlocked(List<String> map) {
			return isOnTheMap(map) && !isBlocked(map);
		}

		private boolean isBlocked(List<String> map) {
			return map.get(y).charAt(x) == '#';
		}

		protected boolean isOnTheMap(List<String> map) {
			return y >= 0 && x >= 0 && y < map.size() && x < map.get(0).length();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Location other = (Location) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "[x=" + x + ", y=" + y + "]";
		}

		public Set<Location> getNeighbors() {
			return Arrays.stream(Direction.values()).map(this::moveTo).collect(Collectors.toSet());
		}

		public Set<Location> getNeighborsAndSelf() {
			Set<Location> ret = new HashSet<>();
			ret.add(this);
			ret.addAll(getNeighbors());
			return ret;
		}

	}
	
	static Direction getDirectionFromTo(Location first, Location second) {
		if (first.y > second.y) {
			return Direction.UP;
		}
		if (first.y < second.y) {
			return Direction.DOWN;
		}
		if (first.x < second.x) {
			return Direction.RIGHT;
		}
		if (first.x > second.x) {
			return Direction.LEFT;
		}
		throw new IllegalArgumentException(second.toString());
	}
	
	static List<Location> buildTrajectory(List<String> map, Location first, Location second) {
		if (first.equals(second)) {
			return Collections.singletonList(first);
		}
		Direction direction = getDirectionFromTo(first, second);
		ArrayList<Location> trajectory = new ArrayList<>();
		trajectory.add(first);
		Location location = keepMovingUntilMarginIsReached(map, direction, trajectory, second);
		direction = Direction.reverse(direction);
		location = keepMovingUntilMarginIsReached(map, direction, trajectory, location.moveTo(direction).moveTo(direction));
		direction = Direction.reverse(direction);
		keepMovingUntilFirstIsReached(map, direction, trajectory, location.moveTo(direction).moveTo(direction), first);
		return trajectory;
	}

	private static void keepMovingUntilFirstIsReached(List<String> map, Direction direction,
			ArrayList<Location> trajectory, Location location, Location first) {
		while (location.isOnTheMapAndNotBlocked(map) && !first.equals(location)) {
			trajectory.add(location);
			location = location.moveTo(direction);
		}		
	}

	protected static Location keepMovingUntilMarginIsReached(List<String> map, Direction direction,
			ArrayList<Location> trajectory, Location location) {
		while (location.isOnTheMapAndNotBlocked(map)) {
			trajectory.add(location);
			location = location.moveTo(direction);
		}
		return location;
	}
	
	static class Node {
		private final List<Location> trajectory;

		public Node(List<String> map, Location first, Location second) {
			this.trajectory = buildTrajectory(map, first, second);
		}
		
		public static Set<Node> getNodes(List<String> map, Set<Location> firstLocations, Set<Location> secondLocations) {
			Set<Node> ret = new HashSet<>();
			firstLocations.forEach(location -> 
				location.getNeighborsAndSelf().stream().filter(secondLocations::contains)
						.forEach(neighbor -> ret.add(new Node(map, location, neighbor))));			
			return ret;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((trajectory == null) ? 0 : trajectory.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (trajectory == null) {
				if (other.trajectory != null)
					return false;
			} else if (!trajectory.equals(other.trajectory))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Node [trajectory=" + trajectory + "]";
		}
		
		
	}
	
	static class Game {
		private static final int BOMB_EXPIRATION = 3;
		private static final int BOMB_POWER = 3;
		private int round = 1;
		private final int width;
		private final int height;
		private final Set<Node> nodes;
		private final Set<Location> obstacles;
		private Map<Location, Integer> bombsWithExpiration = new HashMap<>();
		
		public Game(Game other) {
			this.round = other.round;
			this.height = other.height;
			this.width = other.width;
			this.nodes = new HashSet<>(other.nodes);
			this.obstacles = new HashSet<>(other.obstacles);
			this.bombsWithExpiration = new HashMap<>(other.bombsWithExpiration);
		}
		
		public Game(Set<Node> nodes, Set<Location> obstacles, int width, int height) {
			this.width = width;
			this.height = height;
			this.nodes = nodes;
			this.obstacles = obstacles;
		}
		
		private Location getNodeLocation(Node node) {
			return node.trajectory.get(round % node.trajectory.size());
		}

		public void nextRound() {
			round++;
			triggerBombs();
		}

		private void triggerBombs() {
			Set<Location> bombsToBeTriggered = bombsToBeTriggered();
			bombsWithExpiration.keySet().removeAll(bombsToBeTriggered);
			Map<Location, Integer> newBombsWithExpriration = new HashMap<>();
			bombsWithExpiration.forEach((key, value) -> newBombsWithExpriration.put(key, value - 1));
			bombsWithExpiration = newBombsWithExpriration;
			bombsToBeTriggered.forEach(this::triggerBomb);
		}

		private Set<Location> bombsToBeTriggered() {
			Set<Location> expiredBombs = bombsWithExpiration.entrySet().stream()
					.filter(entry -> entry.getValue().equals(1)).map(Map.Entry::getKey).collect(Collectors.toSet());
			Set<Location> enrichedBombs;
			Set<Location> newEnrichedBombs = new HashSet<>(expiredBombs);
			do {
				enrichedBombs = new HashSet<>(newEnrichedBombs);
				enrichedBombs.stream().map(this::getAccessibleLocationsFrom).flatMap(Set::stream)
						.filter(bombsWithExpiration.keySet()::contains).forEach(newEnrichedBombs::add);
			}
			while (!enrichedBombs.equals(newEnrichedBombs));
			return enrichedBombs;
		}
		
		private void triggerBomb(Location location) {
			Set<Location> accessibleLocations = new HashSet<>(getAccessibleLocationsFrom(location));
			accessibleLocations.retainAll(getNodeLocations());
			Iterator<Node> nodeIt = nodes.iterator();
			while (nodeIt.hasNext()) {
				if (accessibleLocations.contains(getNodeLocation(nodeIt.next()))) {
					nodeIt.remove();
				}
			}
		}

		private Set<Location> getAccessibleLocationsFrom(Location location) {
			Set<Location> accessibleLocations = new HashSet<>();
			accessibleLocations.add(location);
			Arrays.stream(Direction.values()).forEach(direction -> {
				Location myLocation = location;
				for (int i = 0; i < BOMB_POWER; i++) {
					myLocation = myLocation.moveTo(direction);
					if (obstacles.contains(myLocation)) {
						break;
					}
					accessibleLocations.add(myLocation);
				}
			});
			return accessibleLocations;
		}

		public Set<Location> getNodeLocations() {
			return nodes.stream().map(this::getNodeLocation).collect(Collectors.toSet());
		}

		public void placeBombAt(Location location) {
			bombsWithExpiration.put(location, BOMB_EXPIRATION);
		}

		public Set<Location> getBombableLocations() {
			Set<Location> locations = new HashSet<>();
			Set<Location> allNodesLocations = getNodeLocations();
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height ; j++) {
					Location location = new Location(i, j);
					if (!obstacles.contains(location) && !allNodesLocations.contains(location) && 
							!bombsWithExpiration.containsKey(location)) {
						locations.add(location);
					}
				}
			}
			return locations;
		}
		
		public int newBombDamage(Location location) {
			return simulateRounds(BOMB_EXPIRATION).nodes.size() - 
					simulateRounds(BOMB_EXPIRATION, location).nodes.size();
		}
		
		public Game simulateRounds(int rounds) {
			return simulateRounds(rounds, Optional.empty());
		}
		
		public Game simulateRounds(int rounds, Location location) {
			return simulateRounds(rounds, Optional.of(location));
		}

		private Game simulateRounds(int rounds, Optional<Location> bomb) {
			Game simulated = new Game(this);
			if (bomb.isPresent()) {
				simulated.placeBombAt(bomb.get());
			}
			for (int i = 0; i < rounds; i++) {
				simulated.nextRound();
			}
			return simulated;
		}
	}
	
	static class GameRound {
		private final int rounds;
		private final int bombs;
		private final List<String> map;
		
		GameRound(MyCustomScanner in, int height) {
			rounds = in.nextInt();
			bombs = in.nextInt();
			in.nextLine();
			map = new ArrayList<>();
			for (int i = 0; i < height; i++) {
				map.add(in.nextLine());
			}
		}
		
		private Set<Location> getLocationsOf(char c) {
			Set<Location> locations = new HashSet<>();
			for (int y = 0; y < map.size(); y++) {
				for (int x = 0; x < map.get(y).length(); x++) {
					if (map.get(y).charAt(x) == c) {
						locations.add(new Location(x, y));
					}
				}
			}
			return locations;
		}
		
		public Set<Location> getNodes() {
			return getLocationsOf('@');
		}
		
		public Set<Location> getObstacles() {
			return getLocationsOf('#');
		}
	}
	
	interface MyCustomScanner {
		String nextLine();
		int nextInt();
	}
	
	static class MyScanner implements MyCustomScanner {
		private final Scanner scanner;
		private final List<String> lines = new ArrayList<>();

		public MyScanner(Scanner scanner) {
			this.scanner = scanner;
		}

		public String nextLine() {
			String nextLine = scanner.nextLine();
			lines.add(nextLine);
			return nextLine;
		}

		public int nextInt() {
			int i = scanner.nextInt();
			lines.add(String.valueOf(i));
			return i;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			appendLine(0, sb);
			for(int i = 1; i < lines.size(); i++) {
				sb.append(",\n");
				appendLine(i, sb);
			}
			return sb.toString();
		}
		
		private void appendLine(int line, StringBuilder sb) {
			sb.append("\"").append(lines.get(line).replaceAll("\"", "\\\"")).append("\"");
		}
	}
	
	enum Direction {
		UP, DOWN, LEFT, RIGHT;

		public static Direction reverse(Direction direction) {
			switch (direction) {
			case LEFT: return RIGHT;
			case RIGHT: return LEFT;
			case UP: return DOWN;
			case DOWN: return UP;
			default: throw new IllegalArgumentException(direction.toString());
			}
		}
	}
}

