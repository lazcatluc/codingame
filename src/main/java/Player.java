import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int width = in.nextInt(); // width of the building.
		int height = in.nextInt(); // height of the building.
		int turns = in.nextInt(); // maximum number of turns before game over.
		int x = in.nextInt();
		int y = in.nextInt();
		Player.Location min = new Player.Location(0, 0);
		Player.Location max = new Player.Location(width - 1, height - 1);
		Player.Location current = new Player.Location(x, y);
		Solver solver = new Solver(min, max, current);
		//UNKNOWN
		in.next();
		// game loop
		while (true) {
			System.out.println(solver.getNext());

			String bombDir = in.next(); // Current distance to the bomb compared to previous distance (COLDER, WARMER, SAME or UNKNOWN)
			switch(bombDir) {
				case "COLDER": /*System.out.println(solver.getCurrent());*/ solver = solver.colder(); break;
				case "WARMER": solver = solver.warmer(); break;
				case "SAME": solver = solver.same(); break;
				default: throw new IllegalArgumentException(bombDir);
			}

		}
	}

	public static class Solver {
		private final Location min;
		private final Location max;
		private final Location current;
		private final Location next;
		private final boolean splitByX;
		private final boolean leftOriented;
		private final boolean topOriented;

		public Solver(Location min, Location max, Location current) {
			this.min = min;
			this.max = max;
			this.current = current;
			leftOriented = current.x <= (max.x + min.x) / 2;
			topOriented = current.y <= (max.y + min.y) / 2;
			this.next = next();
			splitByX = next.y == current.y;
		}

		public Location getNext() {
			return next;
		}

		private Location next() {
			if (min.x < max.x) {
				int newX = (leftOriented ? (2 * max.x + min.x) : (max.x + 2 * min.x)) / 3;
				if (newX == current.x) {
					newX++;
				}
				return new Location(newX, current.y);
			}
			if (min.y < max.y) {
				int newY = (topOriented ? (2 * max.y + min.y) : (max.y + 2 * min.y)) / 3;
				if (newY == current.y) {
					newY++;
				}
				return new Location(current.x, newY);
			}
			return current;
		}

		public Solver same() {
			if (splitByX) {
				int x = halfX();
				Location newMin = new Location(x, min.y);
				Location newMax = new Location(x, max.y);
				Location newCurrent = new Location(x, current.y);
				return new Solver(newMin, newMax, newCurrent);
			}
			int y = (current.y + next.y) / 2;
			Location newMin = new Location(min.x, y);
			Location newMax = new Location(max.x, y);
			Location newCurrent = new Location(current.x, y);
			return new Solver(newMin, newMax, newCurrent);
		}

		public Solver warmer() {
			if (splitByX) {
				int half = halfX();
				if (half == current.x) {
					half = next.x;
				}
				if (leftOriented) {
					return new Solver(new Location(half, min.y), max, next);
				}
				return new Solver(min, new Location(half, max.y), next);
			}
			int half = halfY();
			if (half == current.y) {
				half = next.y;
			}
			if (topOriented) {
				return new Solver(new Location(min.x, half), max, next);
			}
			return new Solver(min, new Location(max.x, half), next);
		}

		public Solver colder() {
			if (splitByX) {
				int half = halfX();
				if (half == next.x) {
					half = current.x;
				}
				if (leftOriented) {
					return new Solver(min, new Location(half, max.y), current);
				}
				return new Solver(new Location(half, min.y), max, current);
			}
			int half = halfY();
			if (half == next.y) {
				half = current.y;
			}
			if (topOriented) {
				return new Solver(min, new Location(max.x, half), current);
			}
			return new Solver(new Location(min.x, half), max, current);
		}


		private int halfX() {
			return (current.x + next.x) / 2;
		}

		private int halfY() {
			return (current.y + next.y) / 2;
		}

		@Override
		public String toString() {
			return "Solver{" +
					"min=" + min +
					", max=" + max +
					", current=" + current +
					", next=" + next +
					", splitByX=" + splitByX +
					", leftOriented=" + leftOriented +
					", topOriented=" + topOriented +
					", halfX=" + halfX() +
					", halfY=" + halfY() +
					'}';
		}

		public Location getCurrent() {
			return current;
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
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Location location = (Location) o;
			return x == location.x &&
					y == location.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

		@Override
		public String toString() {
			return  x + " " + y;
		}
	}
}
