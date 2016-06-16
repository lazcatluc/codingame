import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        // game loop
        while (true) {

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            out.println("WAIT");
            if (round.rounds==1) {
            	return;
            }
            round = new GameRound(in, height);
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
		private final Set<Node> nodes;
		private final Set<Location> obstacles;
		private Map<Location, Integer> bombsWithExpiration = new HashMap<>();
		
		public Game(Set<Node> nodes, Set<Location> obstacles) {
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
			return expiredBombs;
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

