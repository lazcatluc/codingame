import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.IntUnaryOperator;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
	
	private static final Map<Integer, Room> ROOMS_BY_TYPE = new HashMap<>();
	static {
		ROOMS_BY_TYPE.put(1, new Room(new Path.Builder().build(),
				  new Path.Builder().from(Direction.LEFT).build(),
				  new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(2, new Room(
				  new Path.Builder().from(Direction.LEFT).to(Direction.RIGHT).build(),
				  new Path.Builder().from(Direction.RIGHT).to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(3, new Room(new Path.Builder().build()));
		ROOMS_BY_TYPE.put(4, new Room(
				  new Path.Builder().from(Direction.RIGHT).build(),
				  new Path.Builder().to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(5, new Room(
				  new Path.Builder().from(Direction.LEFT).build(),
				  new Path.Builder().to(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(6, new Room(
				  new Path.Builder().from(Direction.LEFT).to(Direction.RIGHT).build(),
				  new Path.Builder().from(Direction.RIGHT).to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(7, new Room(new Path.Builder().build(),
				  new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(8, new Room(
				  new Path.Builder().from(Direction.LEFT).build(),
				  new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(9, new Room(new Path.Builder().build(),
				  new Path.Builder().from(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(10, new Room(
				  new Path.Builder().to(Direction.LEFT).build()));
		ROOMS_BY_TYPE.put(11, new Room(
				  new Path.Builder().to(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(12, new Room(
				  new Path.Builder().from(Direction.RIGHT).build()));
		ROOMS_BY_TYPE.put(13, new Room(
				  new Path.Builder().from(Direction.LEFT).build()));
	}
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // number of columns.
        int height = in.nextInt(); // number of rows.
        int[][] roomTypes = new int[width][height];
        in.nextLine();
        for (int i = 0; i < height; i++) {
            String[] line = in.nextLine().split(" "); // represents a line in the grid and contains W integers. Each integer represents one room of a given type.
            for (int j = 0; j < width; j++) {
            	roomTypes[j][i] = Integer.parseInt(line[j]);
            }
        }
        int exitX = in.nextInt(); // the coordinate along the X axis of the exit (not useful for this first mission, but must be read).

        // game loop
        while (true) {
            int xi = in.nextInt();
            int yi = in.nextInt();
            Direction direction = Direction.valueOf(in.next());
            String result = ROOMS_BY_TYPE.get(roomTypes[xi][yi]).getOut(direction).transform(xi, yi);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // One line containing the X Y coordinates of the room in which you believe Indy will be on the next turn.
            System.out.println(result);
        }
    }
	
	enum Direction {
		TOP(x -> x, y -> y - 1), 
		RIGHT(x -> x + 1, y -> y), 
		BOTTOM(x -> x, y -> y + 1), 
		LEFT(x -> x - 1, y -> y);
		
		private final IntUnaryOperator transformX;
		private final IntUnaryOperator transformY;
		
		private Direction(IntUnaryOperator transformX, IntUnaryOperator transformY) {
			this.transformX = transformX;
			this.transformY = transformY;
		}
		
		public String transform(int x, int y) {
			return transformX.applyAsInt(x) + " " + transformY.applyAsInt(y);
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
		
		public Room(Path...paths) {
			this.paths = Arrays.asList(paths);
		}
		
		public Direction getOut(Direction in) {
			return paths.stream().filter(path -> path.startsWith(in)).map(Path::getOut).findFirst().get();
		}
	}
	
	
}
