import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	private static final int MAX_JET_FUEL = 1200;
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int rows = in.nextInt(); // number of rows.
        int columns = in.nextInt(); // number of columns.
        int alarmRounds = in.nextInt(); // number of rounds between the time the alarm countdown is activated and the time the alarm goes off.
        int jetPackFuel = MAX_JET_FUEL;
        char goal = 'C';
        
        
        gameLoop:
        while (true) {
        	StringBuilder sb = new StringBuilder();
        	jetPackFuel--;
        	if (jetPackFuel == 1) {
        		System.err.println(sb);
        	}
            int kirkRow = in.nextInt(); // row where Kirk is located.
            int kirkColumn = in.nextInt(); // column where Kirk is located.
            Location kirkLocation = new Location(kirkRow, kirkColumn);
            
            Map<Location, Character> map = readMap(in, rows);
            
            print(map, rows, columns, kirkLocation, sb);
            System.err.println(sb);
			if (map.get(kirkLocation).equals(goal)) {
            	goal = 'T';
            }
			List<MazeSolver> solvers = Arrays.asList(
					new DiscoverMapWhenEnoughFuel(map, kirkLocation, jetPackFuel),
					new DirectSolver(map, kirkLocation, goal),
					new DirectSolver(map, kirkLocation, '?'));
			
			for (MazeSolver solver : solvers) {
				solver.solve();
	            if (solver.solutionFound()) {
	            	System.out.println(solver.getDirection());
	            	continue gameLoop;
	            	
	            }
			}
			System.err.println(sb);
        	throw new IllegalStateException("No solution found");
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            
        }
    }

	protected static Map<Location, Character> readMap(Scanner in, int rows) {
		Map<Location, Character> map = new HashMap<>();
		for (int i = 0; i < rows; i++) {
		    char[] row = in.next().toCharArray(); // C of the characters in '#.TC?' (i.e. one line of the ASCII maze).
		    for (int j = 0; j < row.length; j++) {
		    	map.put(new Location(i, j), row[j]);
		    }
		    
		}
		return map;
	}
	
	private static void print(Map<Location, Character> map, int rows, int columns, Location kirkLocation, StringBuilder sb) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				Location location = new Location(i, j);
				if (location.equals(kirkLocation)) {
					sb.append('@');
				}
				else {
					sb.append(map.get(location));
				}
			}
			sb.append("\n");
		}
		sb.append("\n");
	}

	interface MazeSolver {
		char getGoal();
		void solve();
		boolean solutionFound();
		Direction getDirection();
	}
	
	static class Location {
		private final int row;
		private final int column;
		
		public Location(int row, int column) {
			this.row = row;
			this.column = column;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + column;
			result = prime * result + row;
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
			if (column != other.column)
				return false;
			if (row != other.row)
				return false;
			return true;
		}
		
		public Location getNeighbor(Direction direction) {
			switch(direction) {
			case UP: return new Location(row - 1, column);
			case DOWN: return new Location(row + 1, column);
			case LEFT: return new Location(row, column - 1);
			case RIGHT: return new Location(row, column + 1);
			default: throw new IllegalArgumentException(direction.toString());
			}
		}
	}
	
	static class DiscoverMapWhenEnoughFuel implements MazeSolver {
		private final DirectSolver questionMarkSolver;
		private final boolean shouldAttemptToSolve;
		
		public DiscoverMapWhenEnoughFuel(Map<Location, Character> map, Location location, int jetPackFuel) {
			this.questionMarkSolver = new DirectSolver(map, location, '?');
			shouldAttemptToSolve = jetPackFuel > MAX_JET_FUEL / 2;
		}

		@Override
		public char getGoal() {
			return '?';
		}

		@Override
		public void solve() {
			if (shouldAttemptToSolve) {
				questionMarkSolver.solve();
			}
		}

		@Override
		public boolean solutionFound() {
			return questionMarkSolver.solutionFound();
		}

		@Override
		public Direction getDirection() {
			return questionMarkSolver.getDirection();
		}
		
		
	}
	
	static class DirectSolver implements MazeSolver {
		private final Map<Location, Character> map;
		private final char goal;
		private final Map<Location, List<Direction>> directions = new HashMap<>();
		private final Location startingLocation;
		private Location solutionLocation;
		
		public DirectSolver(Map<Location, Character> map, Location location, char goal) {
			this.map = new HashMap<>(map);
			this.startingLocation = location;
			directions.put(startingLocation, Collections.emptyList());
			this.goal = goal;
		}
		
		@Override
		public void solve() {
			Queue<Location> locations = new LinkedList<>();
			locations.offer(startingLocation);
			while (!locations.isEmpty()) {
				Location currentLocation = locations.poll();
				for (Direction direction : Direction.values()) { 
					Location neighbor = currentLocation.getNeighbor(direction);
					Character neighborMapCharacter = map.get(neighbor);
					if (neighborMapCharacter.equals(goal)) {
						solutionLocation = neighbor;
						markPathFromTo(currentLocation, direction, solutionLocation);
						return;						
					}
					if (neighborMapCharacter.equals('.') || neighborMapCharacter.equals('T') ) {
						if (!directions.containsKey(neighbor)) {
							markPathFromTo(currentLocation, direction, neighbor);
							locations.offer(neighbor);
						}
					}
					
				};
			}
		}

		private void markPathFromTo(Location currentLocation, Direction direction, Location neighbor) {
			List<Direction> newDirections = new ArrayList<>(directions.get(currentLocation));
			newDirections.add(direction);
			directions.put(neighbor, newDirections);
		}

		@Override
		public char getGoal() {
			return goal;
		}

		@Override
		public boolean solutionFound() {
			return directions.get(solutionLocation) != null;
		}

		@Override
		public Direction getDirection() {
			return directions.get(solutionLocation).get(0);
		}
		
	}
	
	
	enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}
}


