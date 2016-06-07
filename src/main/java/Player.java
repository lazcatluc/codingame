import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
            addElevator(elevatorFloor, elevatorPos, ELEVATORS);
        }
        boolean[][] reachable = generateReachable(nbFloors, width, exitFloor, exitPos, ELEVATORS);

        // game loop
        while (true) {
            int cloneFloor = in.nextInt(); // floor of the leading clone
            int clonePos = in.nextInt(); // position of the leading clone on its floor
            Direction direction = Direction.valueOf(in.next()); // direction of the leading clone: LEFT or RIGHT
            List<Strategy> strategies = Arrays.asList(
            		new NoActiveClone(),
            		new OnTopOfBlockedClone(),
            		new MovesTowardsExit(exitFloor, exitPos, direction),
            		new BecomeElevatorIfNoneIsFound(exitFloor, reachable),
            		new AddElevatorIfOnExitPositionAndWeCanAffordIt(exitFloor, exitPos, reachable),
            		new AddElevatorIfElevatorIsTooFarAndWeCanAffordIt(width, exitFloor, reachable),
            		new AddElevatorIfWeCanChainThreeAndWeCanAffordIt(exitFloor, reachable),
            		new MovesTowardsElevator(direction, reachable),
            		new MovesTowardsAnAreaWhichHasReachableUpstairs(direction, reachable),
            		new BlockIfEverythingElseFails()
        		);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(strategies.stream().filter(strategy -> strategy.applies(cloneFloor, clonePos))
            		.findFirst().get().getAction()); // action: WAIT or BLOCK
        }
    }
	
	protected static boolean isElevator(int floor, int position) {
		return ELEVATORS.getOrDefault(floor, Collections.emptyList()).contains(position);
	}

	
	protected static int extraElevatorsWeCanAfford(int floor, int exitFloor, boolean[][] reachable) {
		int floorsAboveNeedingElevators = 0;
		for (int upperFloor = floor + 1; upperFloor < exitFloor; upperFloor++) {
			if (ELEVATORS.getOrDefault(upperFloor, Collections.emptyList()).stream().
					noneMatch(elevatorPosition -> reachable[floor][elevatorPosition])) {
				floorsAboveNeedingElevators++;
			}
		}
		int extraElevatorsWeCanAfford = nbAdditionalElevators - floorsAboveNeedingElevators;
		return extraElevatorsWeCanAfford;
	}

	protected static boolean[][] generateReachable(int nbFloors, int width, int exitFloor, int exitPos, 
			Map<Integer, List<Integer>> allElevators) {
		boolean[][] reachable = new boolean[nbFloors + 1][width];
        reachable[exitFloor][exitPos] = true;
        for (int floor = exitFloor; floor >= 0; floor--) {
        	List<Integer> elevators = allElevators.getOrDefault(floor, Collections.emptyList());
        	fillBasedOnElevators(reachable, floor, elevators);
        }
        return reachable;
	}

	protected static void fillBasedOnElevators(boolean[][] reachable, int floor, List<Integer> elevators) {
		int width = reachable[0].length;
		int i = findFirstReachablePoint(reachable, floor);
		i = findFirstNonReachableElevatorToTheLeft(reachable, floor, elevators, i);
		i++;
		i = fillUntilTheFirstNonReachableElevatorToTheRight(reachable, floor, elevators, width, i);
	}

	protected static int fillUntilTheFirstNonReachableElevatorToTheRight(boolean[][] reachable, int floor,
			List<Integer> elevators, int width, int i) {
		while((!elevators.contains(i) || reachable[floor][i]) && i < width) {
			reachable[floor][i] = true;
			if(floor > 0) {
				reachable[floor-1][i] = true;
			}
			i++;
		}
		return i;
	}

	protected static int findFirstNonReachableElevatorToTheLeft(boolean[][] reachable, int floor, List<Integer> elevators, int i) {
		while((!elevators.contains(i) || reachable[floor][i]) && i > 0) {
			reachable[floor][i] = true;
			i--;		
		}
		return i;
	}

	protected static int findFirstReachablePoint(boolean[][] reachable, int floor) {
		int i = 0;
		while (!reachable[floor][i]) {
			i++;
		}
		return i;
	}
	
	public static void addElevator(int elevatorFloor, int elevatorPosition, Map<Integer, List<Integer>> allElevators) {
		List<Integer> elevators = allElevators.get(elevatorFloor);
		if (elevators == null) {
			elevators = new ArrayList<>();
			allElevators.put(elevatorFloor, elevators);
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
	
	static class MovesTowardsAnAreaWhichHasReachableUpstairs implements Strategy {

		private final Direction direction;
		private final boolean[][] reachable;

		public MovesTowardsAnAreaWhichHasReachableUpstairs(Direction direction, boolean[][] reachable) {
			this.direction = direction;
			this.reachable = reachable;
		}

		@Override
		public boolean applies(int floor, int position) {
			if (floor == reachable.length) {
				return false;
			}
			boolean[] upstairs = reachable[floor + 1];
			if (upstairs[position]) {
				return false;
			}
			if (direction == Direction.RIGHT) {
				return searchRight(floor, position, upstairs);
			}
			if (direction == Direction.LEFT) {
				return searchLeft(floor, position, upstairs);
			}
			return false;
		}

		protected boolean searchLeft(int floor, int position, boolean[] upstairs) {
			for (int i = position; i >= 0; i--) {
				if (upstairs[i]) {
					return true;
				}
				if (isElevator(floor, i)) {
					return false;
				}
			}
			return false;
		}

		protected boolean searchRight(int floor, int position, boolean[] upstairs) {
			for (int i = position; i < upstairs.length; i++) {
				if (upstairs[i]) {
					return true;
				}
				if (isElevator(floor, i)) {
					return false;
				}
			}
			return false;
		}

		protected boolean isElevator(int floor, int i) {
			return ELEVATORS.getOrDefault(floor, Collections.emptyList()).contains(i);
		}

		@Override
		public Action getAction() {
			return Action.WAIT;
		}
		
	}
	
	static class AddElevatorIfOnExitPositionAndWeCanAffordIt extends ClosestReachableElevator {

		private final int exitPosition;
		private final int exitFloor;

		public AddElevatorIfOnExitPositionAndWeCanAffordIt(int exitFloor, int exitPosition, boolean[][] reachable) {
			super(reachable);
			this.exitFloor = exitFloor;
			this.exitPosition = exitPosition;
		}

		@Override
		public Action getAction() {
			return Action.ELEVATOR;
		}

		@Override
		protected boolean appliesClosestElevator(int floor, int position, int closestElevator) {
			if (isElevator(floor, position)) {
				return false;
			}
			boolean weCanAffordIt = nbAdditionalElevators >= exitFloor - floor;
			if (position == exitPosition && weCanAffordIt) {
				nbAdditionalElevators--;
				return true;
			}
			return false;
		}
		
	}
	
	static class AddElevatorIfElevatorIsTooFarAndWeCanAffordIt extends ClosestReachableElevator {

		private final int width;
		private final int exitFloor;
		private final boolean[][] reachable;

		public AddElevatorIfElevatorIsTooFarAndWeCanAffordIt(int width, int exitFloor, boolean[][] reachable) {
			super(reachable);
			this.width = width;
			this.exitFloor = exitFloor;
			this.reachable = reachable;
		}

		@Override
		public Action getAction() {
			return Action.ELEVATOR;
		}

		@Override
		protected boolean appliesClosestElevator(int floor, int position, int closestElevator) {
			if (isElevator(floor, position)) {
				return false;
			}
			int extraElevatorsWeCanAfford = extraElevatorsWeCanAfford(floor, exitFloor, reachable);
			boolean elevatorTooFar = Math.abs(position - closestElevator) > width / (extraElevatorsWeCanAfford + 1);
			boolean upstairsIsReachable = floor < exitFloor && reachable(floor + 1, position);
			if (elevatorTooFar && extraElevatorsWeCanAfford > 0 && upstairsIsReachable) {
				addElevator(floor, position, ELEVATORS);
				nbAdditionalElevators--;
				return true;
			}
			return false;
		}

		
	}
	
	static class AddElevatorIfWeCanChainThreeAndWeCanAffordIt implements Strategy {

		private final int exitFloor;
		private final boolean[][] reachable;

		public AddElevatorIfWeCanChainThreeAndWeCanAffordIt(int exitFloor, boolean[][] reachable) {
			this.exitFloor = exitFloor;
			this.reachable = reachable;
		}

		@Override
		public Action getAction() {
			return Action.ELEVATOR;
		}

		@Override
		public boolean applies(int floor, int position) {
			if (isElevator(floor, position)) {
				return false;
			}
			int extraElevatorsWeCanAfford = extraElevatorsWeCanAfford(floor, exitFloor, reachable);
			boolean applies = extraElevatorsWeCanAfford > 0 && floor < exitFloor && floor > 0 && reachable[floor + 1][position] && 
					isElevator(floor + 1, position) && isElevator(floor - 1, position) && !isElevator(floor, position);
			if (applies) {
				addElevator(floor, position, ELEVATORS);
			}
			return applies;
		}

		
		
	}
	
	static class BecomeElevatorIfNoneIsFound implements Strategy {

		private final int exitFloor;
		private final boolean[][] reachable;

		public BecomeElevatorIfNoneIsFound(int exitFloor, boolean[][] reachable) {
			this.exitFloor = exitFloor;
			this.reachable = reachable;
		}

		@Override
		public boolean applies(int floor, int position) {
			if (isElevator(floor, position)) {
				return false;
			}
			boolean floorLacksElevator = floorLacksReachableElevator(floor);
			boolean addingElevator = floorLacksElevator && reachable[floor + 1][position];
			if (addingElevator) {
				addElevator(floor, position, ELEVATORS);
				nbAdditionalElevators--;
			}
			return addingElevator;
		}

		protected boolean floorLacksReachableElevator(int floor) {
			List<Integer> elevators = ELEVATORS.getOrDefault(floor, Collections.emptyList());
			
			return floor < exitFloor && elevators.stream().noneMatch(elevatorPosition -> reachable[floor][elevatorPosition]);
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
	
	static abstract class ClosestReachableElevator implements Strategy {
		
		private final boolean[][] reachable;
		
		public ClosestReachableElevator(boolean[][] reachable) {
			this.reachable = reachable;
		}
		
		protected boolean reachable(int floor, int elevatorPosition) {
			return reachable[floor][elevatorPosition];
		}

		@Override
		public final boolean applies(int floor, int position) {
			List<Integer> targetPosition = new ArrayList<>(ELEVATORS.getOrDefault(floor, Collections.emptyList()));
			List<Integer> reachableTargetPosition = targetPosition.stream()
					.filter(elevatorPosition -> reachable(floor, elevatorPosition)).collect(Collectors.toList());
			Collections.sort(reachableTargetPosition, (i1, i2) -> Math.abs(i1 - position) - Math.abs(i2 - position));
			
			if (reachableTargetPosition.isEmpty()) {
				return false;
			}
			
			return appliesClosestElevator(floor, position, reachableTargetPosition.get(0));
		}

		protected abstract boolean appliesClosestElevator(int floor, int position, int closestElevator);
	}

	static class MovesTowardsElevator extends ClosestReachableElevator {
		
		private final Direction direction;
		
		public MovesTowardsElevator(Direction direction, boolean[][] reachable) {
			super(reachable);
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
