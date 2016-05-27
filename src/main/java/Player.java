import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int lines = in.nextInt();
        int columns = in.nextInt();
        List<String> rows = new ArrayList<>(lines);
        in.nextLine();
        for (int i = 0; i < lines; i++) {
            rows.add(in.nextLine());
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(new BenderPath(rows).getPath());
    }

	interface FuturamaStreetItem {
		Direction exitFrom(Direction in);
		Location getLocation();
		Location newLocation();
	}
	
	interface Obstacle {
	}
	
	static class BenderPath {
		private List<Direction> preferedDirections = Arrays.asList(Direction.SOUTH, Direction.EAST,
				Direction.NORTH, Direction.WEST);
		private final Map<Location, FuturamaStreetItem> futurama = new HashMap<>();
		private final TeleporterProxy teleporterProxy = new TeleporterProxy();
		private BenderMode benderMode = BenderMode.REGULAR;
		private Location currentLocation;
		private Direction currentDirection = Direction.SOUTH;
		private Result result;
		private int brokenObstacles;
		
		public BenderPath(List<String> rows) {
			for (int i = 0; i < rows.size(); i++) {
				char[] currentRow = rows.get(i).toCharArray();
				for (int j = 0; j < currentRow.length; j++) {
					Location location = new Location(j, i);
					futurama.put(location, get(currentRow[j], location));
				}
			}
			Objects.requireNonNull(currentLocation);
			Objects.requireNonNull(result);
		}
		
		public String getPath() {
			while (!result.isComplete()) {
				Mover mover = new Mover();
				mover.move();
				result.add(new Trajectory(currentDirection, currentLocation, benderMode, brokenObstacles));
				currentLocation = mover.getNewLocation();
				currentDirection = mover.getNewDirection();
			}
			return result.toString();
		}

		public FuturamaStreetItem get(char ch, Location location) {
			switch(ch) {
			case '@': currentLocation = location; return new EmptySpace(location);
			case '$': result = new Result(location); return new SuicideBooth(location);
			case '#': return new Unbreakable(location);
			case 'X': return new Breakable(location);
			case 'E': return new PathModifier(location, Direction.EAST);
			case 'W': return new PathModifier(location, Direction.WEST);
			case 'N': return new PathModifier(location, Direction.NORTH);
			case 'S': return new PathModifier(location, Direction.SOUTH);
			case 'I': return new Inverter(location);
			case 'B': return new Beer(location);
			case 'T': return teleporterProxy.getTeleporter(location);
			case ' ': return new EmptySpace(location);
			default: throw new IllegalArgumentException("Unknown code: '" + ch + "'");
			}
		}
		
		class Beer extends FixedStreetItem {

			public Beer(Location location) {
				super(location);
			}

			@Override
			public Direction exitFrom(Direction in) {
				benderMode = BenderMode.switchMode(benderMode);
				return in;
			}
			
		}
		
		class Breakable extends FixedStreetItem implements Obstacle {
			
			public Breakable(Location location) {
				super(location);
			}

			@Override
			public Direction exitFrom(Direction in) {
				if (BenderMode.REGULAR.equals(benderMode)) {
					return Direction.IMPASSABLE;
				}
				futurama.remove(getLocation());
				brokenObstacles++;
				return in;
			}
			
		}
		
		class Result {
			private final Location finalDestination;
			private Set<Trajectory> trajectories = new LinkedHashSet<>();
			private String result = "";
			
			public Result(Location finalDestination) {
				this.finalDestination = finalDestination;
			}

			public boolean isComplete() {
				return !result.isEmpty();
			}
			
			public void add(Trajectory trajectory) {
				if (finalDestination.equals(trajectory.getLocation())) {
					buildPath();
					return;
				}
				boolean added = trajectories.add(trajectory);
				if (!added) {
					result = "LOOP";
				}
			}
			
			private void buildPath() {
				StringBuilder sb = new StringBuilder();
				trajectories.forEach(trajectory -> sb.append(trajectory.getDirection()).append("\n"));
				result = sb.toString();
			}

			public void markBrokenObstacle() {
				trajectories.clear();
			}

			@Override
			public String toString() {
				return result;
			}
			
		}
		
		class Mover {
			private Location newLocation;
			private Direction newDirection;

			public Mover() {
				newLocation = currentLocation;
				newDirection = currentDirection;
			}
			
			public void move() {
				List<Direction> directionsInOrder = new ArrayList<>();
				directionsInOrder.add(currentDirection);
				directionsInOrder.addAll(preferedDirections);
				for (Direction direction : directionsInOrder) {
					newLocation = currentLocation.to(direction);
					FuturamaStreetItem item = futurama.getOrDefault(newLocation, new EmptySpace(newLocation));
					newDirection = item.exitFrom(direction);
					newLocation = item.newLocation();
					if (newDirection != Direction.IMPASSABLE) {
						currentDirection = direction;
						return;
					}
				}
			}
			
			public Location getNewLocation() {
				return newLocation;
			}
			
			public Direction getNewDirection() {
				return newDirection;
			}
			
		}
		
		class Inverter extends FixedStreetItem {

			public Inverter(Location location) {
				super(location);
			}

			@Override
			public Direction exitFrom(Direction in) {
				Collections.reverse(preferedDirections);
				return in;
			}
			
		}
	}
	
	abstract static class FixedStreetItem implements FuturamaStreetItem {
		private final Location location;

		public FixedStreetItem(Location location) {
			this.location = location;
		}
		
		@Override
		public Location newLocation() {
			return location;
		}
		
		@Override
		public Location getLocation() {
			return location;
		}
	}
	
	static class Trajectory {
		private final Direction direction;
		private final Location location;
		private final BenderMode benderMode;
		private final int brokenObstacles;
		
		public Trajectory(Direction direction, Location location, BenderMode benderMode, int brokenObstacles) {
			this.direction = direction;
			this.location = location;
			this.benderMode = benderMode;
			this.brokenObstacles = brokenObstacles;
		}

		public Location getLocation() {
			return location;
		}

		public Direction getDirection() {
			return direction;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((benderMode == null) ? 0 : benderMode.hashCode());
			result = prime * result + brokenObstacles;
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
			result = prime * result + ((location == null) ? 0 : location.hashCode());
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
			Trajectory other = (Trajectory) obj;
			if (benderMode != other.benderMode)
				return false;
			if (brokenObstacles != other.brokenObstacles)
				return false;
			if (direction != other.direction)
				return false;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			return true;
		}
		
	}
	
	static class EmptySpace extends FixedStreetItem {

		public EmptySpace(Location location) {
			super(location);
		}

		@Override
		public Direction exitFrom(Direction in) {
			return in;
		}
		
	}
	
	static class Teleporter extends FixedStreetItem {

		private final Supplier<Location> teleportToLocation;

		public Teleporter(Location location, Supplier<Location> teleportToLocation) {
			super(location);
			this.teleportToLocation = teleportToLocation;
		}

		@Override
		public Direction exitFrom(Direction in) {
			return in;
		}
		
		@Override
		public Location newLocation() {
			return teleportToLocation.get();
		}
	}
	
	static class TeleporterProxy {

		private List<Location> locations = new ArrayList<>();
		
		public Teleporter getTeleporter(Location location) {
			locations.add(location);
			int locationSize = locations.size();
			return new Teleporter(location, () -> locations.get(2 - locationSize));
		}
		
	}
	
	static class PathModifier extends FixedStreetItem {
		
		private final Direction direction;

		public PathModifier(Location location, Direction direction) {
			super(location);
			this.direction = direction;
		}

		@Override
		public Direction exitFrom(Direction in) {
			return direction;
		}
		
	}
	
	
	
	static class SuicideBooth extends FixedStreetItem {

		public SuicideBooth(Location location) {
			super(location);
		}

		@Override
		public Direction exitFrom(Direction in) {
			return Direction.DIE;
		}
		
	}
	
	
	static class Unbreakable extends FixedStreetItem implements Obstacle {

		public Unbreakable(Location location) {
			super(location);
		}

		@Override
		public Direction exitFrom(Direction in) {
			return Direction.IMPASSABLE;
		}
		
	}
	
	enum Direction {
		NORTH, SOUTH, EAST, WEST, IMPASSABLE, DIE;
	}
	
	enum BenderMode {
		REGULAR, BREAKER;
		
		static BenderMode switchMode(BenderMode benderMode) {
			if (REGULAR.equals(benderMode)) {
				return BREAKER;
			}
			return REGULAR;
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
		
		public Location to(Direction direction) {
			switch(direction) {
				case NORTH: return new Location(x, y - 1);
				case EAST: return new Location(x + 1, y);
				case SOUTH: return new Location(x, y + 1);
				case WEST: return new Location(x - 1, y);
				default: return this;		
			}
		}

		@Override
		public String toString() {
			return "Location [x=" + x + ", y=" + y + "]";
		}
		
		
	}
}
