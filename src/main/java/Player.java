import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	private static final Map<Integer, List<Integer>> ELEVATORS = new HashMap<>();
	private static final Set<List<Integer>> BLOCKED_CLONES = new HashSet<>();
	private static int nbAdditionalElevators;

	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int nbFloors = in.nextInt(); // number of floors
        int width = in.nextInt(); // width of the area
        int nbRounds = in.nextInt(); // maximum number of rounds
        int exitFloor = in.nextInt(); // floor on which the exit is found
        int exitPos = in.nextInt(); // position of the exit on its floor
        int nbTotalClones = in.nextInt(); // number of generated clones
        nbAdditionalElevators = in.nextInt(); // ignore (always zero)
        int nbElevators = in.nextInt(); // number of elevators
        for (int i = 0; i < nbElevators; i++) {
            int elevatorFloor = in.nextInt(); // floor on which this elevator is found
            int elevatorPos = in.nextInt(); // position of the elevator on its floor
            addElevator(elevatorFloor, elevatorPos);
        }

        // game loop
        while (true) {
            int cloneFloor = in.nextInt(); // floor of the leading clone
            int clonePos = in.nextInt(); // position of the leading clone on its floor
            Direction direction = Direction.valueOf(in.next()); // direction of the leading clone: LEFT or RIGHT
            List<Strategy> strategies = Arrays.asList(
            		new NoActiveClone(),
            		new OnTopOfBlockedClone(),
            		new MovesTowardsExit(exitFloor, exitPos, direction),
            		new BecomeElevatorIfNoneIsFound(exitFloor),
            		new AddElevatorIfElevatorIsTooFarAndWeCanAffordIt(width, exitFloor),
            		new MovesTowardsElevator(direction),
            		new BlockIfEverythingElseFails()
        		);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(strategies.stream().filter(strategy -> strategy.applies(cloneFloor, clonePos))
            		.findFirst().get().getAction()); // action: WAIT or BLOCK
        }
    }
	
	public static void addElevator(int elevatorFloor, int elevatorPosition) {
		List<Integer> elevators = ELEVATORS.get(elevatorFloor);
		if (elevators == null) {
			elevators = new ArrayList<>();
			ELEVATORS.put(elevatorFloor, elevators);
		}
		elevators.add(elevatorPosition);
	}
	
	static boolean movingOnRightDirection(Direction direction, int clonePosition, int targetPosition) {
		int distance = clonePosition - targetPosition;
		return distance == 0 ||
			   (direction == Direction.LEFT && distance > 0) ||
			   (direction == Direction.RIGHT && distance < 0);
	}
	
	interface Strategy {
		boolean applies(int floor, int position);
		Action getAction();
	}
	
	static class AddElevatorIfElevatorIsTooFarAndWeCanAffordIt extends ClosestElevator {

		private final int width;
		private final int exitFloor;

		public AddElevatorIfElevatorIsTooFarAndWeCanAffordIt(int width, int exitFloor) {
			this.width = width;
			this.exitFloor = exitFloor;
		}

		@Override
		public Action getAction() {
			return Action.ELEVATOR;
		}

		@Override
		protected boolean appliesClosestElevator(int floor, int position, int closestElevator) {
			boolean elevatorTooFar = Math.abs(position - closestElevator) > width / 2;
			int floorsAboveNeedingElevators = 0;
			for (int upperFloor = floor + 1; upperFloor < exitFloor; upperFloor++) {
				if (!ELEVATORS.containsKey(upperFloor)) {
					floorsAboveNeedingElevators++;
				}
			}
			boolean weCanAffordIt = nbAdditionalElevators > floorsAboveNeedingElevators;
			if (elevatorTooFar && weCanAffordIt) {
				addElevator(floor, position);
				nbAdditionalElevators--;
				return true;
			}
			return false;
		}
		
	}
	
	static class BecomeElevatorIfNoneIsFound implements Strategy {

		private final int exitFloor;

		public BecomeElevatorIfNoneIsFound(int exitFloor) {
			this.exitFloor = exitFloor;
		}

		@Override
		public boolean applies(int floor, int position) {
			boolean floorLacksElevator = floor != exitFloor && !ELEVATORS.containsKey(floor);
			if (floorLacksElevator) {
				addElevator(floor, position);
				nbAdditionalElevators--;
			}
			return floorLacksElevator;
		}

		@Override
		public Action getAction() {
			return Action.ELEVATOR;
		}
		
	}
	
	static class MovesTowardsExit implements Strategy {
		private final int exitFloor;
		private final int exitPosition;
		private final Direction direction;
		
		public MovesTowardsExit(int exitFloor, int exitPosition, Direction direction) {
			this.exitFloor = exitFloor;
			this.exitPosition = exitPosition;
			this.direction = direction;
		}

		@Override
		public boolean applies(int floor, int position) {
			return floor == exitFloor && movingOnRightDirection(direction, position, exitPosition);
		}

		@Override
		public Action getAction() {
			return Action.WAIT;
		}
		
	}
	
	static class NoActiveClone implements Strategy {

		@Override
		public boolean applies(int floor, int position) {
			return floor == -1;
		}

		@Override
		public Action getAction() {
			return Action.WAIT;
		}
		
	}
	
	static abstract class ClosestElevator implements Strategy {
		@Override
		public final boolean applies(int floor, int position) {
			List<Integer> targetPosition = new ArrayList<>(ELEVATORS.getOrDefault(floor, Collections.emptyList()));
			if (targetPosition.isEmpty()) {
				return false;
			}
			Collections.sort(targetPosition, (i1, i2) -> Math.abs(i1 - position) - Math.abs(i2 - position));
			
			return appliesClosestElevator(floor, position, targetPosition.get(0));
		}

		protected abstract boolean appliesClosestElevator(int floor, int position, int closestElevator);
	}

	static class MovesTowardsElevator extends ClosestElevator {
		
		private final Direction direction;
		
		public MovesTowardsElevator(Direction direction) {
			this.direction = direction;
		}
		
		@Override
		public boolean appliesClosestElevator(int floor, int position, int closestElevator) {
			return movingOnRightDirection(direction, position, closestElevator);
		}
		
		@Override
		public Action getAction() {
			return Action.WAIT;
		}
	}
	
	static class OnTopOfBlockedClone implements Strategy {

		@Override
		public boolean applies(int floor, int position) {
			return BLOCKED_CLONES.contains(Arrays.asList(floor, position));
		}

		@Override
		public Action getAction() {
			return Action.WAIT;
		}
		
	}
	
	static class BlockIfEverythingElseFails implements Strategy {

		@Override
		public boolean applies(int floor, int position) {
			BLOCKED_CLONES.add(Arrays.asList(floor, position));
			return true;
		}

		@Override
		public Action getAction() {
			return Action.BLOCK;
		}
		
	}
	
	enum Action {
		WAIT, BLOCK, ELEVATOR;
	}
	
	enum Direction {
		LEFT, RIGHT, NONE;
	}
}
