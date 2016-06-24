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
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) throws InterruptedException {
		run(new MyScanner(new Scanner(System.in)), System.out);
    }

	protected static void run(MyCustomScanner in, PrintStream out) throws InterruptedException {
		int initialWaits = 5;
		Thread t = new Thread(() -> {
        	try {
        		for (int i = 0; i < initialWaits; i++) {
        			Thread.sleep(95);	
        			out.println("WAIT");
        		}
			} catch (InterruptedException e) {
				return;
			}
        });
		t.start();
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        GameRound initialPosition = new GameRound(in, height);
        GameRound secondPosition = new GameRound(in, height);
        GameRound round = new GameRound(in, height);
        Set<Node> nodes = Node.getNodes(round.map, initialPosition.getNodes(), secondPosition.getNodes(), round.getNodes());
        Game game = new Game(nodes, round.getObstacles(), width, height, initialWaits);
        Queue<Location> bombsToBePlaced = new LinkedList<>();
        BombStrategy solver = new SubsetCoverage(game, initialPosition.rounds, round.bombs);
        if (!solver.isSolved()) {
        	solver = new GreedyStrategyWaitingForSignificantFractionOfNodes(game, round.bombs);
        }
        
        if (solver.isSolved()) {
        	t.join();
        	System.err.println("Solved: "+solver.getBombLocations());
        	bombsToBePlaced.addAll(solver.getBombLocations());
        }
        // game loop
        while (true) {
        	if (bombsToBePlaced.isEmpty()) {
        		bombsToBePlaced.addAll(new BombWithHighestDamageAndPatience(game, 50).getBombLocations());
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
		boolean isSolved();
	}
	
	static class GreedyStrategyWaitingForSignificantFractionOfNodes implements BombStrategy {
		private final Game game;
		private final int availableBombs;
		private final int maxWaits;
		private final boolean solved;
		private final List<Location> bombLocations = new ArrayList<>();
		
		public GreedyStrategyWaitingForSignificantFractionOfNodes(Game game, int availableBombs) {
			this.maxWaits = availableBombs == 1 ? 50 : 10;
			this.game = new Game(game);
			this.availableBombs = availableBombs;
			this.solved = trySolve();
		}

		private boolean trySolve() {
			if (game.nodes.isEmpty()) {
				return true;
			}
			if (availableBombs == 0) {
				return false;
			}
			List<Location> bombLocations = new BombWithHighestDamageAndPatience(game, maxWaits).getBombLocations();
			Iterator<Location> bombIt = bombLocations.iterator();
			while (bombIt.hasNext()) {
				Location bombLocation = bombIt.next();
				if (bombLocation == null) {
					game.nextRound();
					continue;
				}
				int totalNodes = game.nodes.size();
				Game simulateBombAt = game.simulateBombAt(bombLocation);
				int destroyedNodes = totalNodes - simulateBombAt.nodes.size();
				if (totalNodes <= destroyedNodes * availableBombs) {
					GreedyStrategyWaitingForSignificantFractionOfNodes solver = new GreedyStrategyWaitingForSignificantFractionOfNodes(
							simulateBombAt, availableBombs - 1);
					this.bombLocations.addAll(bombLocations);
					this.bombLocations.addAll(solver.getBombLocations());
					return solver.isSolved();
				}
				return false;
			}
			
			return false;
		}
		
		public boolean isSolved() {
			return solved;
		}

		@Override
		public List<Location> getBombLocations() {
			return Collections.unmodifiableList(bombLocations);
		}
		
	}
	
	static class GameSolverWithStrategyAndWait implements BombStrategy {
		private final Game game;
		private final int availableBombs;
		private final int maxWaits;
		private final boolean solved;
		private final List<Location> bombLocations = new ArrayList<>();

		public GameSolverWithStrategyAndWait(Game game, int availableBombs) {
			this.maxWaits = availableBombs == 1 ? 50 : 5;
			this.game = new Game(game);
			this.availableBombs = availableBombs;
			this.solved = trySolve();
		}
		
		private boolean trySolve() {
			if (game.nodes.isEmpty()) {
				return true;
			}
			if (availableBombs == 0) {
				return false;
			}
			for (int i = 0; i < maxWaits; i++) {
				Location bombLocation = new BombWithHighestDamage(game).getBombLocations().get(0);
				GameSolverWithStrategyAndWait solver = new GameSolverWithStrategyAndWait(game.simulateBombAt(bombLocation),
						availableBombs - 1);
				if (solver.solved) {
					bombLocations.addAll(Arrays.asList(bombLocation, null, null));
					bombLocations.addAll(solver.getBombLocations());
					return true;
				}
				if (i < maxWaits - 1) {
					game.nextRound();
					bombLocations.add(null);
				}
			}
			return false;
		}
		
		public boolean isSolved() {
			return solved;
		}
		
		@Override
		public List<Location> getBombLocations() {
			return Collections.unmodifiableList(bombLocations);
		}
		
		
	}
	
	static class BombWithHighestDamageAndPatience implements BombStrategy {
		private final Game game;
		private final int maxWait;
		
		public BombWithHighestDamageAndPatience(Game game, int maxWait) {
			this.game = game;
			this.maxWait = maxWait;
		}

		@Override
		public List<Location> getBombLocations() {
			int waits = 0;
			Location maxLocation = null;
			int maxDamage = 0;
			Game game = new Game(this.game);
			for (int i = 0; i < maxWait; i++) {
				BombWithHighestDamage bombWithHighestDamage = new BombWithHighestDamage(game);
				Location location = bombWithHighestDamage.getBombLocations().get(0);
				int damage = bombWithHighestDamage.getBombValue(location);
				if (damage > maxDamage) {
					waits = i;
					maxDamage = damage;
					maxLocation = location;
				}
				game.nextRound();
			}
			List<Location> ret = new ArrayList<>();
			for (int i = 0; i < waits; i++) {
				ret.add(null);
			}
			ret.add(maxLocation);
			ret.add(null);
			ret.add(null);
			return ret;
		}

		@Override
		public boolean isSolved() {
			return false;
		}
		
		
	}
	
	static class BombWithHighestDamage implements BombStrategy {
		private final Map<Location, Integer> bombableLocations;

		public BombWithHighestDamage(Game game) {
			bombableLocations = game.getBombableLocations().stream()
					.collect(Collectors.toMap(loc -> loc, loc -> game.newBombDamage(loc)));
		}

		@Override
		public List<Location> getBombLocations() {
			return Arrays.asList(bombableLocations.entrySet().stream()
					.max((entry1, entry2) -> entry1.getValue() - entry2.getValue()).get().getKey(), null, null);
		}
		
		Integer getBombValue(Location location) {
			return bombableLocations.getOrDefault(location, 0);
		}

		@Override
		public boolean isSolved() {
			return false;
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
			if (direction == null) {
				return this;
			}
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
		
		public Location moveToIfOnTheMapAndNotBlocked(Direction direction, List<String> map) {
			
			Location newLocation = moveTo(direction);
			if (newLocation.isOnTheMapAndNotBlocked(map)) {
				return newLocation;
			}
			newLocation = moveTo(Direction.reverse(direction));
			if (newLocation.isOnTheMap(map)) {
				return newLocation;
			}
			return null;
		}
		
		public Direction directionTo(Location neighbor) {
			if (this.equals(neighbor)) {
				return null;
			}
			return Arrays.stream(Direction.values()).filter(direction -> moveTo(direction).equals(neighbor)).findAny().get();
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
		if (trajectory.get(trajectory.size() - 1).equals(first)) {
			trajectory.remove(trajectory.size() - 1);
			return trajectory;
		}
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
		private final List<Location> firstTwoLocations;

		public Node(List<String> map, Location first, Location second) {
			this.trajectory = buildTrajectory(map, first, second);
			if (trajectory.size() == 1) {
				this.firstTwoLocations = Arrays.asList(trajectory.get(0), trajectory.get(0));
			}
			else {
				this.firstTwoLocations = trajectory.subList(0, 2);
			}
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
			result = prime * result + ((firstTwoLocations == null) ? 0 : firstTwoLocations.hashCode());
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
			if (firstTwoLocations == null) {
				if (other.firstTwoLocations != null)
					return false;
			} else if (!firstTwoLocations.equals(other.firstTwoLocations))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Node [firstTwoLocations=" + firstTwoLocations + "]";
		}

		@SafeVarargs
		public static Set<Node> getNodes(List<String> map, Set<Location> locations1, Set<Location> locations2,
				Set<Location>... subsequentLocations) {
			Set<Node> ret = new HashSet<>();
			locations1.forEach(location -> 
				location.getNeighborsAndSelf().stream()
						.filter(locations2::contains)
						.forEach(neighbor -> {
							Location previousNeighbor = location;
							Location currentNeighbor = neighbor;
							
							for (int i = 0; i < subsequentLocations.length; i++) {
								Direction currentDirection = previousNeighbor.directionTo(currentNeighbor);
								Location nextNeighbor = currentNeighbor.moveToIfOnTheMapAndNotBlocked(currentDirection, map);
								if (!subsequentLocations[i].contains(nextNeighbor)) {
									return;
								}
								previousNeighbor = currentNeighbor;
								currentNeighbor = nextNeighbor;
							}
							ret.add(new Node(map, location, neighbor));
						})); 			
			return ret;
		}
		
		
	}
	
	static class Game {
		private static final int BOMB_EXPIRATION = 3;
		private static final int BOMB_POWER = 3;
		private int round;
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
			this(nodes, obstacles, width, height, 2);
		}
		public Game(Set<Node> nodes, Set<Location> obstacles, int width, int height, int round) {
			this.width = width;
			this.height = height;
			this.nodes = nodes;
			this.obstacles = obstacles;
			this.round = round;
		}
		
		private Location getNodeLocation(Node node) {
			return getNodeLocation(node, 0);
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
			return getNodeLocations(0);
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
		
		public Map<Location, Set<Node>> getBombableLocationsWithDamage() {
			Map<Location, Set<Node>> locations = new HashMap<>();
			Set<Location> allNodesLocations = getNodeLocations();
			Set<Location> allNodesLocationsWhenBombBlows = getNodeLocations(BOMB_EXPIRATION);
			for (int i = 0; i < width; i++) {
				locationFor:
				for (int j = 0; j < height ; j++) {
					Location location = new Location(i, j);
					if (!obstacles.contains(location) && !allNodesLocations.contains(location) && 
							!bombsWithExpiration.containsKey(location)) {
						Set<Node> damage = nodesAtLocation(getAccessibleLocationsFrom(location).stream()
								.filter(allNodesLocationsWhenBombBlows::contains).collect(Collectors.toSet()), BOMB_EXPIRATION);
						Iterator<Map.Entry<Location, Set<Node>>> currentLocationsWithDamage = locations.entrySet().iterator();
						while (currentLocationsWithDamage.hasNext()) {
							Map.Entry<Location, Set<Node>> entry = currentLocationsWithDamage.next();
							if (entry.getValue().containsAll(damage)) {
								continue locationFor;
							}
							if (damage.containsAll(entry.getValue())) {
								currentLocationsWithDamage.remove();
							}
						}
						locations.put(location, damage);
					}
				}
			}
			return locations;
		}
		
		private Set<Node> nodesAtLocation(Set<Location> loc, int bombExpiration) {
			return nodes.stream().filter(node -> loc.contains(node.trajectory.get((round + bombExpiration) % node.trajectory.size())))
					.collect(Collectors.toSet());
		}

		private Set<Location> getNodeLocations(int bombExpiration) {
			return nodes.stream().map(node -> getNodeLocation(node, bombExpiration)).collect(Collectors.toSet());
		}

		private Location getNodeLocation(Node node, int bombExpiration) {
			return node.trajectory.get((round + bombExpiration) % node.trajectory.size());
		}

		public int newBombDamage(Location location) {
			return simulateRounds(BOMB_EXPIRATION).nodes.size() - 
					simulateBombAt(location).nodes.size();
		}
		
		public Game simulateRounds(int rounds) {
			return simulateRounds(rounds, Optional.empty());
		}
		
		public Game simulateRounds(int rounds, Location location) {
			return simulateRounds(rounds, Optional.of(location));
		}
		
		public Game simulateBombAt(Location location) {
			return simulateRounds(BOMB_EXPIRATION, Optional.of(location));
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

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			Set<Location> allNodes = getNodeLocations();
			Set<Location> bombs = bombsWithExpiration.keySet();
			
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					Location location = new Location(i, j);
					if (obstacles.contains(location)) {
						sb.append('#');
						continue;
					}
					if (bombs.contains(location)) {
						sb.append(bombsWithExpiration.get(location));
						continue;
					}
					if (allNodes.contains(location)) {
						sb.append('@');
						continue;
					}
					sb.append('.');
				}
				sb.append('\n');
			}
			return sb.toString();
		}

		public Set<Node> getNodes() {
			return Collections.unmodifiableSet(nodes);
		}
		
		
	}
	
	static class MaximalBombLocationDamage implements Comparable<MaximalBombLocationDamage> {
		private final Location bombLocation;
		private final Map<Node, Integer> killedNodesScores;
		private final Set<Integer> killerRounds;
		private Integer[] sortedScores;
		
		public MaximalBombLocationDamage(MaximalBombLocationDamage other) {
			this.bombLocation = other.bombLocation;
			this.killedNodesScores = new HashMap<>(other.killedNodesScores);
			this.killerRounds = new HashSet<>(other.killerRounds);
		}
		
		public MaximalBombLocationDamage(Location bombLocation, Set<Integer> killerRounds) {
			this.bombLocation = bombLocation;
			this.killedNodesScores = new HashMap<>();
			this.killerRounds = new HashSet<>(killerRounds);
		}
		
		public MaximalBombLocationDamage(Location bombLocation) {
			this.bombLocation = bombLocation;
			this.killedNodesScores = new HashMap<>();
			this.killerRounds = new HashSet<>();
		}
		
		public List<Integer> getKillerRounds() {
			return new ArrayList<>(killerRounds);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((bombLocation == null) ? 0 : bombLocation.hashCode());
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
			MaximalBombLocationDamage other = (MaximalBombLocationDamage) obj;
			if (bombLocation == null) {
				if (other.bombLocation != null)
					return false;
			} else if (!bombLocation.equals(other.bombLocation))
				return false;
			return true;
		}
		
		public void addKilledNode(Node node, Integer score) {
			killedNodesScores.put(node, score);
			sortedScores = null;
		}

		public void removeAll(Set<Node> nodes) {
			killedNodesScores.keySet().removeAll(nodes);
			sortedScores = null;
		}
		
		public void removeAll(MaximalBombLocationDamage o) {
			removeAll(o.killedNodesScores.keySet());
		}
		
		public void retainAll(Set<Integer> rounds) {
			killerRounds.retainAll(rounds);
		}

		@Override
		public String toString() {
			return "MaximalBombLocationDamage [bombLocation=" + bombLocation + ", killedNodes=" + killedNodesScores
					+ ", killerRounds=" + killerRounds + "]";
		}
		
		private Integer[] getScore() {
			if (sortedScores == null) {
				sortedScores = killedNodesScores.values().toArray(new Integer[killedNodesScores.values().size()]);
				Arrays.sort(sortedScores);
			}
			return sortedScores;
		}

		@Override
		public int compareTo(MaximalBombLocationDamage o) {
			int i = 0;
			while (i < this.getScore().length && i < o.getScore().length) {
				int diff = this.getScore()[i] - o.getScore()[i];
				if (diff != 0) {
					return diff;
				}
				i++;
			}
			if (i < this.getScore().length) {
				return -1;
			}
			if (i < o.getScore().length) {
				return 1;
			}
			return 0;
		}
		
		
		
	}
	
	static class SubsetCoverage implements BombStrategy {
		
		private final Game game;
		private final int maxRounds;
		private final int maxBombs;
		private final boolean solved;
		private final Map<Integer, Location> bombLocationsForRound;
		
		public SubsetCoverage(Game game, int maxRounds, int maxBombs) {
			this.game = new Game(game);
			this.maxRounds = maxRounds;
			this.maxBombs = maxBombs;
			this.bombLocationsForRound = bombLocationsForRound();
			this.solved = bombLocationsForRound != null;
		}
		
		public Map<Integer, Location> bombLocationsForRound() {
			Set<MaximalBombLocationDamage> findSubsetCoveringAllNodes = findSubsetCoveringAllNodes();
			if (findSubsetCoveringAllNodes == null) {
				return null;
			}
			List<MaximalBombLocationDamage> coveringSubset = new ArrayList<>(findSubsetCoveringAllNodes);
			Collections.sort(coveringSubset, (m1, m2) -> m1.killerRounds.size() - m2.killerRounds.size());
			List<List<Integer>> killerRounds = coveringSubset.stream().map(MaximalBombLocationDamage::getKillerRounds).collect(Collectors.toList());
			killerRounds.forEach(Collections::sort);
			int[] roundsIndices = new int[coveringSubset.size()];
			int i = 0;
			rounds:
			while (i > -1 && i < roundsIndices.length) {
				if (roundsIndices[i] == killerRounds.get(i).size()) {
					i--;
					continue;
				}
				Integer round = killerRounds.get(i).get(roundsIndices[i]);
				roundsIndices[i]++;
				for (int j = 0; j < i; j++) {
					if (Math.abs(round - killerRounds.get(j).get(roundsIndices[j] - 1)) < 3) {
						continue rounds;
					}
				}
				i++;
			}
			if (i == -1) {
				return null;
			}
			Map<Integer, Location> ret = new TreeMap<>();
			for (i = 0; i< roundsIndices.length; i++) {
				ret.put(killerRounds.get(i).get(roundsIndices[i] - 1), coveringSubset.get(i).bombLocation);
			}
			return ret;
		}
		
		public Set<MaximalBombLocationDamage> findSubsetCoveringAllNodes() {
			return findSubsetCoveringAllNodes(game.getNodes(), new ArrayList<>(findMaximalSubset()), maxBombs);
		}
		
		private Set<MaximalBombLocationDamage> findSubsetCoveringAllNodes(Set<Node> allNodes, 
				List<MaximalBombLocationDamage> maximalSubsets, int remainingBombs) {
			if (allNodes.isEmpty()) {
				return new HashSet<>();
			}
			if (maximalSubsets.isEmpty()) {
				return null;
			}
			if (remainingBombs == 0) {
				return null;
			}
			Collections.sort(maximalSubsets);
			for (int i = 0; i < maximalSubsets.size(); i++) {
				MaximalBombLocationDamage maximalBombLocationDamage = maximalSubsets.get(i);			
				Set<Node> newlyNeedToCoverNodes = new HashSet<>(allNodes);
				newlyNeedToCoverNodes.removeAll(maximalBombLocationDamage.killedNodesScores.keySet());
				List<MaximalBombLocationDamage> newMaximalSubset = maximalSubsets.subList(i + 1, maximalSubsets.size())
						.stream().map(MaximalBombLocationDamage::new).collect(Collectors.toList());
				newMaximalSubset.forEach(maximalSubset -> maximalSubset.removeAll(maximalBombLocationDamage));
				Set<MaximalBombLocationDamage> subsetCoveringNodes = 
						findSubsetCoveringAllNodes(newlyNeedToCoverNodes, newMaximalSubset, remainingBombs - 1);
				if (subsetCoveringNodes != null) {
					subsetCoveringNodes.add(maximalBombLocationDamage);
					return subsetCoveringNodes;
				}
			}
			return null;
		}
		
		public Set<MaximalBombLocationDamage> findMaximalSubset() {
			Map<Node, Map<Location, Set<Integer>>> original = findNodeCoverage();
			Map<Location, MaximalBombLocationDamage> map = new HashMap<>();
			original.entrySet().forEach(myMapEntry -> myMapEntry.getValue().entrySet().forEach(entry -> {
				MaximalBombLocationDamage currentMaximalRounds = map.get(entry.getKey());
				if (currentMaximalRounds == null) {
					currentMaximalRounds = new MaximalBombLocationDamage(entry.getKey(), entry.getValue());
					map.put(entry.getKey(), currentMaximalRounds);
				}
				currentMaximalRounds.retainAll(entry.getValue());
				currentMaximalRounds.addKilledNode(myMapEntry.getKey(), myMapEntry.getValue().size());
			}));
			return map.values().stream().filter(m -> !m.killerRounds.isEmpty()).collect(Collectors.toSet());
		}
		
		public Map<Player.Node, Map<Location, Set<Integer>>> findNodeCoverage() {
			Game game = new Player.Game(this.game);
			Map<Node, Map<Location, Set<Integer>>> map = new HashMap<>();
			Map<Integer, Map<Player.Location, Set<Player.Node>>> bombableLocationsInTime = new HashMap<>();
			for (int i = game.round; i < maxRounds - 2; i++) {
				bombableLocationsInTime.put(i, game.getBombableLocationsWithDamage());
				game.nextRound();
			}
			game.getNodes().forEach(node -> map.put(node, reverseMap(findNodeCoverage(node, bombableLocationsInTime))));
			return map;
		}
		
		public Map<Location, Set<Integer>> reverseMap(Map<Integer, Set<Location>> mapWithSingleSolution) {
			Map<Location, Set<Integer>> map = new HashMap<>();
			mapWithSingleSolution.entrySet().forEach(entry ->
				entry.getValue().stream().forEach(location -> {
					Set<Integer> solutions = map.get(location);
					if (solutions == null) {
						solutions = new HashSet<>();
						map.put(location, solutions);
					}
					solutions.add(entry.getKey());
				})
			);
			return map;
		}
	
		private Map<Integer, Set<Player.Location>> findNodeCoverage(Node node, 
				Map<Integer, Map<Player.Location, Set<Node>>> bombableLocationsInTime) {
			Map<Integer, Set<Player.Location>> map = new HashMap<>();
			bombableLocationsInTime.entrySet().stream().forEach(entry -> {
				entry.getValue().entrySet().stream().filter(entry1 -> entry1.getValue()
						.contains(node)).forEach(entry1 -> {
							Set<Player.Location> set = map.get(entry.getKey());
							if (set == null) {
								set = new HashSet<>();
								map.put(entry.getKey(), set);
							}
							set.add(entry1.getKey());
						});
			});
			return map;
		}

		@Override
		public List<Location> getBombLocations() {
			List<Location> bombs = new ArrayList<>();
			for (int i = game.round; i < maxRounds; i++) {
				bombs.add(bombLocationsForRound.get(i));
			}
			return bombs;
		}

		@Override
		public boolean isSolved() {
			return solved;
		}
		
	}
	
	static class GameRound {
		private final int rounds;
		final int bombs;
		final List<String> map;
		
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
			if (direction == null) {
				return null;
			}
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

