import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	private static final Map<Integer, Room> ROOMS_BY_TYPE = new HashMap<>();
	static {
		ROOMS_BY_TYPE.put(0, new Room());
		ROOMS_BY_TYPE.put(1, new Room(new Path.Builder().build(), new Path.Builder().from(Direction.LEFT).build(),
				new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(2, new Room(new Path.Builder().from(Direction.LEFT).to(Direction.RIGHT).build(),
				new Path.Builder().from(Direction.RIGHT).to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(3, new Room(new Path.Builder().build()));
		ROOMS_BY_TYPE.put(4, new Room(new Path.Builder().from(Direction.RIGHT).build(),
				new Path.Builder().to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(5, new Room(new Path.Builder().from(Direction.LEFT).build(),
				new Path.Builder().to(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(6, new Room(new Path.Builder().from(Direction.LEFT).to(Direction.RIGHT).build(),
				new Path.Builder().from(Direction.RIGHT).to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(7, new Room(new Path.Builder().build(), new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(8, new Room(new Path.Builder().from(Direction.LEFT).build(),
				new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(9, new Room(new Path.Builder().build(), new Path.Builder().from(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(10, new Room(new Path.Builder().to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(11, new Room(new Path.Builder().to(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(12, new Room(new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(13, new Room(new Path.Builder().from(Direction.LEFT).build()));
	}

	private static final int[] ROTATION_PERMUTATION = {0, 1, 3, 2, 5, 4, 7, 8, 9, 6, 11, 12, 13, 10}; 
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int width = in.nextInt(); // number of columns.
		int height = in.nextInt(); // number of rows.
		Map<Location, Rotation> rotations = new HashMap<>();
		in.nextLine();
		for (int i = 0; i < height; i++) {
			String nextLine = in.nextLine();
			System.err.println(nextLine);
			String[] line = nextLine.split(" "); // represents a line in
														// the grid and contains
														// W integers. Each
														// integer represents
														// one room of a given
														// type.
			for (int j = 0; j < width; j++) {
				Rotation rotation = new Rotation.Builder().atX(j).atY(i).withRoomType(Integer.parseInt(line[j]))
						.build();
				rotations.put(rotation.location, rotation);
			}
		}
		int exitX = in.nextInt(); // the coordinate along the X axis of the exit
									// (not useful for this first mission, but
									// must be read).
		Location exit = new Location(exitX, height - 1);
		int xi = in.nextInt();
		int yi = in.nextInt();
		Direction initialDirection = Direction.valueOf(in.next());
		Location initialLocation = new Location(xi, yi);
		IndyState initialState = new IndyState.Builder().withRooms(rotations).startingAt(initialLocation).going(initialDirection).exitAt(exit).build();
		while (initialState.score() > 0) {
			initialState.expand();
			//System.err.println(initialState);
		}
		// game loop
		while (true) {
			System.out.println(initialState.nextAction());
			initialState = initialState.nextState();
			xi = in.nextInt();
			yi = in.nextInt();
			initialLocation = new Location(xi, yi);
			initialDirection = Direction.valueOf(in.next());
		}
	}
	
	static class IndyState {
		private final Direction currentDirection;
		private final Location currentLocation;
		private final Location exit;
		private final Location finalLocation;
		private final Map<Location, Direction> path;
		private final Map<Location, Rotation> rooms;
		private final boolean solved;
		private Map<IndyState, Action> expandedStates;
		
		private IndyState(Builder builder) {
			this.currentDirection = builder.currentDirection;
			this.currentLocation = builder.currentLocation;
			this.exit = builder.exit;
			PathBuilder pathBuilder = new PathBuilder(exit, builder.rooms, currentLocation, currentDirection);
			this.path = pathBuilder.getPath();
			this.solved = pathBuilder.isSolved();
			Iterator<Location> locations = this.path.keySet().iterator();
			Location finalLocation = currentLocation;
			while(locations.hasNext()) {
				finalLocation = locations.next();
			}
			this.finalLocation = finalLocation;
			this.rooms = new HashMap<>(builder.rooms);
		}
		
		public Action nextAction() {
			if (expandedStates == null || expandedStates.isEmpty()) {
				return Action.WAIT;
			}
			return expandedStates.get(nextState());
		}

		public boolean isSolved() {
			return solved;
		}
		
		public int score() {
			if (isSolved()) {
				return 0;
			}
			if (expandedStates != null) {
				if (expandedStates.isEmpty()) {
					return Integer.MAX_VALUE;
				}
				int nextStateScore = nextState().score();
				return nextStateScore == 0 ? 0 : nextStateScore - 1;
			}
			return directScore();
		}

		private IndyState nextState() {
			if (expandedStates == null || expandedStates.isEmpty()) {
				return this;
			}
			return expandedStates.keySet().iterator().next();
		}

		public void expand() {
			if (score() == 0) {
				return;
			}
			if (expandedStates == null) {
				expandedStates = new HashMap<>();
				Optional<Direction> nextDirection = rooms.get(currentLocation).getRoom().getOut(currentDirection);
				if (nextDirection.isPresent()) {
					Set<Action> availableActions = getAvailableActions();
					Location nextLocation = nextDirection.get().transform(currentLocation);
					Builder nextStatesBuilder = new Builder().exitAt(exit).startingAt(nextLocation).going(nextDirection.get().reverse());
					availableActions.forEach(action -> {
						Map<Location, Rotation> newRooms = action.act(rooms);
						expandedStates.put(nextStatesBuilder.withRooms(newRooms).build(), action);
					});
				}
			}
			else {
				if (expandedStates.isEmpty()) {
					return;
				}
				nextState().expand();
			}
			List<IndyState> nextStates = new ArrayList<>(expandedStates.keySet());
			Collections.sort(nextStates, (s1, s2) -> s1.score() - s2.score());
			Map<IndyState, Action> newExpandedStates = new LinkedHashMap<>();
			nextStates.forEach(state -> newExpandedStates.put(state, expandedStates.get(state)));
			expandedStates = newExpandedStates;
		}
		
		private Set<Action> getAvailableActions() {
			Set<Action> actions = new HashSet<>();
			actions.add(Action.WAIT);
			rooms.entrySet().stream().filter(entry -> !exit.equals(entry.getKey())).filter(entry -> !currentLocation.equals(entry.getKey()))
					.filter(entry -> !entry.getValue().isFixed()).filter(entry -> !entry.getValue().isConnectedTo(exit))
					.filter(entry -> isCloseToCurrentLocation(entry.getKey())).forEach(entry -> {
						actions.add(new Action(entry.getKey(), Direction.LEFT));
						actions.add(new Action(entry.getKey(), Direction.RIGHT));
					});
			
			return actions;
		}

		private boolean isCloseToCurrentLocation(Location other) {
			return Math.abs(other.x - currentLocation.x) + other.y - currentLocation.y == 1;
		}
		
		protected int directScore() {
			return Integer.MAX_VALUE - path.size();
		}
		
		public static class Builder {
			private Map<Location, Rotation> rooms;
			private Location exit;
			private Location currentLocation;
			private Direction currentDirection;
			
			public Builder startingAt(Location currentLocation) {
				this.currentLocation = currentLocation;
				return this;
			}
			
			public Builder going(Direction currentDirection) {
				this.currentDirection = currentDirection;
				return this;
			}
			
			public Builder exitAt(Location exit) {
				this.exit = exit;
				return this;
			}
			
			public Builder withRooms(Map<Location, Rotation> rooms) {
				this.rooms = rooms;
				return this;
			}
			
			public IndyState build() {
				return new IndyState(this);
			}
		}

		@Override
		public String toString() {
			return "IndyState [score=" + score() + ", currentDirection=" + currentDirection + ", currentLocation=" + currentLocation
					+ ", exit=" + exit + ", finalLocation=" + finalLocation + ", path=" + path + ", rooms=" + rooms
					+ ", solved=" + solved + ", expandedStates=" + expandedStates + "]";
		}
		
	}
	
	static class Action {
		public static final Action WAIT = new Action(new Location(-1, -1), Direction.TOP) {
			@Override
			public String toString() {
				return "WAIT";
			}
		};
		
		private final Location location;
		private final Direction directionOfRotation;
		
		public Action(Location location, Direction directionOfRotation) {
			this.location = location;
			this.directionOfRotation = directionOfRotation;
		}

		@Override
		public String toString() {
			return location.x + " " + location.y + " " + directionOfRotation;
		}
		
		public Map<Location, Rotation> act(Map<Location, Rotation> original) {
			Map<Location, Rotation> ret = new HashMap<>(original);
			Rotation rotation = ret.get(location);
			if (rotation != null) {
				ret.put(location, rotation.rotate(directionOfRotation));
			}
			return ret;
		}
	}

	static class PathBuilder {
		private final Location exit;
		private final Map<Location, Rotation> rotations;
		private final Map<Location, Direction> path;

		public PathBuilder(Location exit, Map<Location, Rotation> rotations, Location currentLocation, Direction currentDirection) {
			this.exit = exit;
			this.rotations = rotations;
			this.path = new LinkedHashMap<>();
			buildPath(currentLocation, currentDirection, this.path);
		}
		
		public boolean isSolved() {
			return path.containsKey(exit);
		}
		
		public Map<Location, Direction> getPath() {
			return Collections.unmodifiableMap(path);
		}

		private void buildPath(Location currentLocation, Direction currentDirection, Map<Location, Direction> path) {
			Rotation rotation = rotations.get(currentLocation);
			if (rotation == null) {
				return;
			}
			Room room = rotation.getRoom();
			Optional<Direction> nextDirectionReversed = room.getOut(currentDirection);
			if (!nextDirectionReversed.isPresent()) {
				return;
			}
			path.put(currentLocation, currentDirection);
			if (exit.equals(currentLocation)) {
				return;
			}			
			Location nextLocation = nextDirectionReversed.get().transform(currentLocation);
			buildPath(nextLocation, nextDirectionReversed.get().reverse(), path);
		}
	}

	enum Direction {
		TOP(x -> x, y -> y - 1), 
		RIGHT(x -> x + 1, y -> y), 
		BOTTOM(x -> x, y -> y + 1), 
		LEFT(x -> x - 1, y -> y);

		private final IntUnaryOperator transformX;
		private final IntUnaryOperator transformY;
		private final UnaryOperator<Location> transformLocation;

		private Direction(IntUnaryOperator transformX, IntUnaryOperator transformY) {
			this.transformX = transformX;
			this.transformY = transformY;
			this.transformLocation = location -> new Location(transformX.applyAsInt(location.x),
					transformY.applyAsInt(location.y));
		}

		public Direction reverse() {
			switch(this) {
			case TOP: return BOTTOM;
			case RIGHT: return LEFT;
			case LEFT: return RIGHT;
			case BOTTOM: return TOP;
			default: throw new IllegalStateException();
			}
		}

		public String transform(int x, int y) {
			return transformX.applyAsInt(x) + " " + transformY.applyAsInt(y);
		}

		public Location transform(Location other) {
			return transformLocation.apply(other);
		}
		
		public Direction rotate(int rotation) {
			int realRotation = (rotation + ordinal()) % Direction.values().length;
			return Direction.values()[realRotation];
		}
	}

	static class Path {
		private final Direction in;
		private final Direction out;

		public Path(Builder builder) {
			this.in = builder.in;
			this.out = builder.out;
		}

		static class Builder {
			private Direction in = Direction.TOP;
			private Direction out = Direction.BOTTOM;

			public Builder from(Direction in) {
				this.in = in;
				return this;
			}

			public Builder to(Direction out) {
				this.out = out;
				return this;
			}

			public Path build() {
				return new Path(this);
			}
		}

		public boolean startsWith(Direction direction) {
			return in.equals(direction);
		}

		public Direction getOut() {
			return out;
		}
	}

	static class Room {
		private final Collection<Path> paths;

		public Room(Path... paths) {
			this.paths = Arrays.asList(paths);
		}

		public Optional<Direction> getOut(Direction in) {
			return paths.stream().filter(path -> path.startsWith(in)).map(Path::getOut).findFirst();
		}

	}

	static class Location {
		private final int x;
		private final int y;

		public Location(int x, int y) {
			this.x = x;
			this.y = y;
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

	}

	static class Rotation {
		private static final int MAX_ROTATIONS = Direction.values().length;
		private final Location location;
		private final int roomType;
		private final boolean fixed;
		private final int rotation;

		public Rotation(Builder builder) {
			this.location = new Location(builder.x, builder.y);
			this.fixed = builder.fixed;
			this.roomType = builder.roomType;
			this.rotation = builder.rotation;
		}

		public boolean isFixed() {
			return fixed || ROTATION_PERMUTATION[roomType] == roomType;
		}

		public boolean isConnectedTo(Location exit) {
			Room room = getRoom();
			
			return Arrays.stream(Direction.values()).map(room::getOut)
					.filter(Optional::isPresent).map(Optional::get).anyMatch(exit::equals);
		}

		public Room getRoom() {
			int type = roomType;
			for (int i = 0; i < rotation; i++) {
				type = ROTATION_PERMUTATION[type];
			}
			if (type != roomType) {
				System.err.println("Original type: "+roomType+"; finalType: "+type);
			}
			return ROOMS_BY_TYPE.get(type);
		}

		public Rotation rotate(Direction direction) {
			if (fixed) {
				throw new IllegalStateException();
			}
			Builder fromThis = new Rotation.Builder().from(this);
			switch(direction) {
				case LEFT: return fromThis.withRotation(rotation(-1)).build();
				case RIGHT: return fromThis.withRotation(rotation(1)).build();
				default: throw new IllegalArgumentException(direction.toString());
			}
		}
		
		private int rotation(int i) {
			return (rotation + i + MAX_ROTATIONS) % MAX_ROTATIONS;
		}

		public static class Builder {
			private int x;
			private int y;
			private int roomType;
			private int rotation;
			private boolean fixed;
			
			public Builder from(Rotation original) {
				this.x = original.location.x;
				this.y = original.location.y;
				this.roomType = original.roomType;
				this.fixed = original.fixed;
				this.rotation = original.rotation;
				return this;
			}

			public Builder atX(int x) {
				this.x = x;
				return this;
			}

			public Builder atY(int y) {
				this.y = y;
				return this;
			}

			public Builder withRoomType(int type) {
				this.roomType = Math.abs(type);
				return type < 0 ? fixed() : rotating();
			}
			
			public Builder withRotation(int rotation) {
				this.rotation = rotation;
				return this;
			}

			public Builder fixed() {
				this.fixed = true;
				return this;
			}

			public Builder rotating() {
				this.fixed = false;
				return this;
			}

			public Rotation build() {
				return new Rotation(this);
			}
		}

		@Override
		public String toString() {
			return "Rotation [location=" + location + ", roomType=" + roomType + ", fixed=" + fixed + ", rotation="
					+ rotation + "]";
		}
		
		
	}

}
