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
        int cloneFloor = in.nextInt(); // floor of the leading clone
        int clonePos = in.nextInt(); // position of the leading clone on its floor
        Reachable myReachable = new Reachable(nbFloors, width, exitFloor, exitPos, ELEVATORS, 
        		Collections.emptyMap(), nbAdditionalElevators, nbRounds, clonePos);
        if (myReachable.generateReachable()) {
        	myReachable.optimizeAddedElevators();
        }
		int[][] reachable = myReachable.getReachable();

        // game loop
        while (true) {
            Direction direction = Direction.valueOf(in.next()); // direction of the leading clone: LEFT or RIGHT
            List<Strategy> strategies = Arrays.asList(
            		new NoActiveClone(),
            		new OnTopOfBlockedClone(),
            		new WaitIfElevatorAlreadyThere(myReachable, direction),
            		new KeepLeft(myReachable, direction),
            		new KeepRight(myReachable, direction),
            		new ChangeToLeft(myReachable, direction),
            		new ChangeToRight(myReachable, direction),
            		new AddElevator(myReachable, direction),
            		new MovesTowardsExit(exitFloor, exitPos, direction),
            		new BecomeElevatorIfNoneIsFound(exitFloor, reachable),
            		new AddElevatorIfOnExitPositionAndWeCanAffordIt(exitFloor, exitPos, reachable),
            		new AddElevatorIfElevatorIsTooFarAndWeCanAffordIt(width, exitFloor, reachable),
            		new MovesTowardsElevator(direction, reachable),
            		new MovesTowardsAnAreaWhichHasReachableUpstairs(direction, reachable),
            		new BlockIfEverythingElseFails()
        		);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            final int myCloneFloor = cloneFloor;
            final int myClonePosition = clonePos;
            System.out.println(strategies.stream().filter(strategy -> strategy.applies(myCloneFloor, myClonePosition))
            		.findFirst().get().getAction()); // action: WAIT, BLOCK or ELEVATOR
            cloneFloor = in.nextInt(); // floor of the leading clone
            clonePos = in.nextInt(); // position of the leading clone on its floor
        }
    }
	
	protected static boolean isElevator(int floor, int position) {
		return ELEVATORS.getOrDefault(floor, Collections.emptyList()).contains(position);
	}

	
	protected static int extraElevatorsWeCanAfford(int floor, int exitFloor, int[][] reachable) {
		int floorsAboveNeedingElevators = 0;
		for (int upperFloor = floor + 1; upperFloor < exitFloor; upperFloor++) {
			if (ELEVATORS.getOrDefault(upperFloor, Collections.emptyList()).stream().
					noneMatch(elevatorPosition -> reachable[floor][elevatorPosition] > 0)) {
				floorsAboveNeedingElevators++;
			}
		}
		int extraElevatorsWeCanAfford = nbAdditionalElevators - floorsAboveNeedingElevators;
		return extraElevatorsWeCanAfford;
	}
	
	static abstract class ReachableStrategy implements Strategy {
		private final Reachable myReachable;
		protected final Direction direction;
		
		public ReachableStrategy(Reachable myReachable, Direction direction) {
			this.myReachable = myReachable;
			this.direction = direction;
		}
		
		@Override
		public final boolean applies(int floor, int position) {
			return myReachable.solved(direction) && myReachable.getReachable()[floor][position] > 0 && directionMatches(floor, position);
		}

		protected boolean shouldMoveLeft(int cloneFloor, int clonePos) {
			return !isElevator(cloneFloor, clonePos) && clonePos > 0 && myReachable.getReachable()[cloneFloor][clonePos - 1] > 0 &&
					myReachable.getReachable()[cloneFloor][clonePos] > myReachable.getReachable()[cloneFloor][clonePos - 1];
		}
		
		protected boolean shouldMoveRight(int cloneFloor, int clonePos) {
			return !isElevator(cloneFloor, clonePos) && clonePos < myReachable.getReachable()[0].length - 1 && 
				myReachable.getReachable()[cloneFloor][clonePos + 1] > 0 &&
				myReachable.getReachable()[cloneFloor][clonePos] > myReachable.getReachable()[cloneFloor][clonePos + 1];
		}
		
		protected abstract boolean directionMatches(int cloneFloor, int clonePos);
	}
	
	static class AddElevator extends ReachableStrategy {

		public AddElevator(Reachable myReachable, 
				Direction direction) {
			super(myReachable, direction);
		}

		@Override
		public Action getAction() {
			return Action.ELEVATOR;
		}

		@Override
		protected boolean directionMatches(int cloneFloor, int clonePos) {
			addElevator(cloneFloor, clonePos, ELEVATORS);
			nbAdditionalElevators--;
			return true;
		}
		
	}
	
	static class WaitIfElevatorAlreadyThere extends ReachableStrategy {

		public WaitIfElevatorAlreadyThere(Reachable myReachable, Direction direction) {
			super(myReachable, direction);
		}

		@Override
		public Action getAction() {
			return Action.WAIT;
		}

		@Override
		protected boolean directionMatches(int cloneFloor, int clonePos) {
			return isElevator(cloneFloor, clonePos);
		}
		
	}
	
	static class KeepLeft extends ReachableStrategy {

		public KeepLeft(Reachable myReachable, Direction direction) {
			super(myReachable, direction);
		}

		@Override
		public Action getAction() {
			return Action.WAIT;
		}

		@Override
		protected boolean directionMatches(int cloneFloor, int clonePos) {
			return direction == Direction.LEFT && shouldMoveLeft(cloneFloor, clonePos);
		}
		

	}
	
	static class KeepRight extends ReachableStrategy {

		public KeepRight(Reachable myReachable, Direction direction) {
			super(myReachable, direction);
		}

		@Override
		public Action getAction() {
			return Action.WAIT;
		}

		@Override
		protected boolean directionMatches(int cloneFloor, int clonePos) {
			return direction == Direction.RIGHT && shouldMoveRight(cloneFloor, clonePos);
		}

	}
	
	static class ChangeToLeft extends ReachableStrategy {

		public ChangeToLeft(Reachable myReachable, Direction direction) {
			super(myReachable, direction);
		}

		@Override
		public Action getAction() {
			return Action.BLOCK;
		}

		@Override
		protected boolean directionMatches(int cloneFloor, int clonePos) {
			return direction == Direction.RIGHT && shouldMoveLeft(cloneFloor, clonePos);
		}
		
	}
	
	static class ChangeToRight extends ReachableStrategy {

		public ChangeToRight(Reachable myReachable, Direction direction) {
			super(myReachable, direction);
		}

		@Override
		public Action getAction() {
			return Action.BLOCK;
		}

		@Override
		protected boolean directionMatches(int cloneFloor, int clonePos) {
			return direction == Direction.LEFT && shouldMoveRight(cloneFloor, clonePos);
		}
		
	}
	
	static class Reachable {
		private final int[][] reachable;
		private final Map<Integer, List<Integer>> addedElevators;
		private final int exitFloor;
		private final int exitPosition;
		private final Map<Integer, List<Integer>> initialElevators;
		private final int numberOfAdditionalElevators;
		private final int numberOfTurns;
		private final int startingPosition;
		
		private Reachable(Reachable original) {
			this.exitFloor = original.exitFloor;
			this.exitPosition = original.exitPosition;
			this.initialElevators = original.initialElevators;
			this.addedElevators = new HashMap<>(original.addedElevators);
			reachable = new int[original.reachable.length][original.reachable[0].length];
			this.numberOfAdditionalElevators = original.numberOfAdditionalElevators;
			this.numberOfTurns = original.numberOfTurns;
			this.startingPosition = original.startingPosition;
		}
		
		public Reachable(int numberOfFloors, int width, int exitFloor, int exitPosition,
				Map<Integer, List<Integer>> initialElevators, Map<Integer, List<Integer>> addedElevators, 
				int numberOfAdditionalElevators, int numberOfTurns, int startingPosition) {
			this.exitFloor = exitFloor;
			this.exitPosition = exitPosition;
			this.initialElevators = initialElevators;
			this.addedElevators = new HashMap<>(addedElevators);
			reachable = new int[numberOfFloors + 1][width];
			this.numberOfAdditionalElevators = numberOfAdditionalElevators;
			this.numberOfTurns = numberOfTurns;
			this.startingPosition = startingPosition;
		}
		
		public boolean solved(Direction direction) {
			
			int numberOfTurnsWithPossibleInitialTurn = numberOfTurns;
			System.err.println(reachable[0][startingPosition]+"---"+numberOfTurnsWithPossibleInitialTurn);
			if ((direction == Direction.LEFT && reachable[0][startingPosition] <= reachable[0][startingPosition - 1]) ||
				(direction == Direction.RIGHT && reachable[0][startingPosition] <= reachable[0][startingPosition + 1])) {
				numberOfTurnsWithPossibleInitialTurn-=3;
			}
			return reachable[0][startingPosition] > 0 && reachable[0][startingPosition] <= numberOfTurnsWithPossibleInitialTurn;
		}
		
		public void optimizeAddedElevators() {
			do {
				int availableElevators = numberOfAdditionalElevators - addedElevators.keySet().size();
				if (availableElevators == 0) {
					return;
				}
				if (addedFastestReacheableNewElevator()) {
					continue;
				}
				if (tryReplacingOneAddedElevatorWithTwoElevators()) {
					continue;
				}
				return;
			}
			while(true);
			
		}
		
		
		public boolean generateReachable() {
			for (int i = 0; i < reachable.length; i++) {
				for (int j = 0; j < reachable[0].length; j++) {
					reachable[i][j] = 0;
				}
			}
			reachable[exitFloor][exitPosition] = 1;
			for (int floor = exitFloor - 1; floor >=0; floor--) {
				if (isElevator(floor, exitPosition)) {
					reachable[floor][exitPosition] = exitFloor - floor + getElevatorMovingCost(floor, exitPosition); 
					if (exitPosition > 0 && !isElevator(floor, exitPosition - 1)) {
						reachable[floor][exitPosition - 1] = reachable[floor][exitPosition] + 1;
					}
					if (exitPosition < reachable[exitFloor].length && !isElevator(floor, exitPosition + 1)) {
						reachable[floor][exitPosition + 1] = reachable[floor][exitPosition] + 1;
					}
					continue;
				}
				break;
			}
			for (int floor = exitFloor; floor >= 0; floor--) {
	        	
	        	if (!fillBasedOnElevators(floor)) {
	        		return false;
	        	}
	        }
			return true;
		}
		
		private boolean fillBasedOnElevators(int floor) {
			int width = reachable[0].length;
			int i = findFirstReachablePoint(floor);
			if (i == width) {
				if (addedElevators.containsKey(floor)) {
					return false;
				}
				i = findFastestReachableNewElevator(floor);
				addElevator(floor, i, addedElevators);
				reachable[floor][i] = reachable[floor + 1][i] + 3;
			}
			i = findFirstNonReachableElevatorToTheLeft(floor, width, i - 1);
			i = fillUntilTheFirstNonReachableElevatorToTheRight(floor, width, i + 1);
			for (i = 0; i < width - 1; i++) {
				if (reachable[floor][i + 1] > 0 && reachable[floor][i] > reachable[floor][i + 1] + 4) {
					i = findFirstNonReachableElevatorToTheLeft(floor, width, i);
				}
			}
			return true;
		}
		
		private boolean tryReplacingOneAddedElevatorWithTwoElevators() {
			int min = reachable[0][startingPosition];
			int minFloor = -1;
			int minPosition = -1;
			int addedElevatorToBeRemoved = -1;
			for (int floor = 0; floor < exitFloor - 1; floor++) {
				if (!addedElevators.containsKey(floor)) {
					continue;
				}
				int currentElevator = addedElevators.get(floor).get(0);
				removeElevator(floor, currentElevator, addedElevators);
				FastestReachableTwoNewElevators newElevator = new FastestReachableTwoNewElevators(floor);	
				newElevator.compute();
				if (newElevator.min < min && newElevator.min > 0) {
					min = newElevator.min;
					minPosition = newElevator.position;
					minFloor = floor;
					addedElevatorToBeRemoved = currentElevator;
				}
				addElevator(floor, currentElevator, addedElevators);
			}
			if (minFloor > -1) {
				removeElevator(minFloor, addedElevatorToBeRemoved, addedElevators);
				addElevator(minFloor, minPosition, addedElevators);
				addElevator(minFloor + 1, minPosition, addedElevators);
				generateReachable();
				return true;
			}
			return false;
		}
		
		private class FastestReachableTwoNewElevators {
			private final int floor;
			private final Integer[] candidates;
			private int min = Integer.MAX_VALUE;
			private int position = -1;
			
			public FastestReachableTwoNewElevators(int floor) {
				this.floor = floor;
				List<Integer> localMinimaFromAboveFloor = new ArrayList<>();
				int topFloor = floor + 2;
				for (int i = 1; i < reachable[topFloor].length - 1; i++) {
					if ((reachable[topFloor][i] > 0)
							&&
						(reachable[topFloor][i-1] == 0 || reachable[topFloor][i-1] > reachable[topFloor][i])&&
						(reachable[topFloor][i+1] == 0 || reachable[topFloor][i+1] > reachable[topFloor][i])
						) {
						localMinimaFromAboveFloor.add(i);
					}
				}
				candidates = localMinimaFromAboveFloor.toArray(new Integer[localMinimaFromAboveFloor.size()]);
			}

			public void compute() {
				
				for (int j = 0; j < candidates.length; j++) {
					int i = candidates[j];
					if (isElevator(floor, i) || isElevator(floor + 1, i)) {
						continue;
					}
					addElevator(floor, i, addedElevators);
					addElevator(floor + 1, i, addedElevators);
					Reachable child = new Reachable(Reachable.this);
					if (child.generateReachable() && child.reachable[0][startingPosition] < min && 
							child.reachable[0][startingPosition] > 0) {
						position = i;
						min = child.reachable[0][startingPosition];
					
					}
					removeElevator(floor, i, addedElevators);
					removeElevator(floor + 1, i, addedElevators);
				}
			}
		}

		
		private boolean addedFastestReacheableNewElevator() {
			int min = reachable[0][startingPosition];
			int minFloor = -1;
			int minPosition = -1;
			for (int floor = 0; floor < exitFloor; floor++) {
				if (addedElevators.containsKey(floor)) {
					continue;
				}
				FastestReachableNewElevator newElevator = new FastestReachableNewElevator(floor);	
				newElevator.compute();
				if (newElevator.min < min && newElevator.min > 0) {
					min = newElevator.min;
					minPosition = newElevator.position;
					minFloor = floor;
				}
			}
			if (minFloor > -1) {
				addElevator(minFloor, minPosition, addedElevators);
				generateReachable();
				return true;
			}
			return false;
		}
		
		private int findFastestReachableNewElevator(int floor) {
			FastestReachableNewElevator fastestReachableNewElevator = new FastestReachableNewElevator(floor);
			fastestReachableNewElevator.compute();
			return fastestReachableNewElevator.position;
		}
		
		private class FastestReachableNewElevator {
			private final int floor;
			private final Integer[] candidates;
			private int min = Integer.MAX_VALUE;
			private int position = -1;
			
			public FastestReachableNewElevator(int floor) {
				this.floor = floor;
				List<Integer> localMinimaFromAboveFloor = new ArrayList<>();
				int topFloor = floor + 1;
				for (int i = 1; i < reachable[topFloor].length - 1; i++) {
					if ((reachable[topFloor][i] > 0)
							&&
						(reachable[topFloor][i-1] == 0 || reachable[topFloor][i-1] > reachable[topFloor][i])&&
						(reachable[topFloor][i+1] == 0 || reachable[topFloor][i+1] > reachable[topFloor][i])
						) {
						localMinimaFromAboveFloor.add(i);
					}
				}
				candidates = localMinimaFromAboveFloor.toArray(new Integer[localMinimaFromAboveFloor.size()]);
			}

			public void compute() {
				
				for (int j = 0; j < candidates.length; j++) {
					int i = candidates[j];
					if (isElevator(floor, i)) {
						continue;
					}
					addElevator(floor, i, addedElevators);
					Reachable child = new Reachable(Reachable.this);
					if (child.generateReachable() && child.reachable[0][startingPosition] < min && 
							child.reachable[0][startingPosition] > 0) {
						position = i;
						min = child.reachable[0][startingPosition];
					}
					removeElevator(floor, i, addedElevators);
				}
			}
		}
		
		protected int getElevatorMovingCost(int floor, int position) {
			return addedElevators.getOrDefault(floor, Collections.emptyList()).contains(position)?3:1;
		}
		
		protected int fillUntilTheFirstNonReachableElevatorToTheRight(int floor, int width, int i) {
			while((!isElevator(floor, i) || reachable[floor][i] > 0) && i < width) {
				if (reachable[floor][i] == 0) {
					reachable[floor][i] = reachable[floor][i - 1] + 1;
				}
				fillElevatorOnLowerFloorToTheRight(floor, width, i);
				i++;
			}
			return i;
		}

		protected void fillElevatorOnLowerFloorToTheRight(int floor, int width, int i) {
			int lowerFloor = floor - 1;
			if(floor > 0 && isElevator(lowerFloor, i) && reachable[lowerFloor][i] == 0) {
				int elevatorMovingCost = getElevatorMovingCost(lowerFloor, i);
				reachable[lowerFloor][i] = reachable[floor][i] + elevatorMovingCost;
				fillElevatorOnLowerFloorToTheRight(lowerFloor, width, i);
				if (i < width - 1 && !isElevator(lowerFloor, i + 1)) {
					reachable[lowerFloor][i + 1] = reachable[floor][i] + elevatorMovingCost + 1;
					fillElevatorOnLowerFloorToTheRight(lowerFloor, width, i + 1);
				}
				if (i > 0 && !isElevator(lowerFloor, i - 1)) {
					reachable[lowerFloor][i - 1] = reachable[floor][i] + elevatorMovingCost + 4;
					fillElevatorOnLowerFloorToTheLeft(width, i - 1, lowerFloor);
				}
			}
		}
		
		protected int findFirstNonReachableElevatorToTheLeft(int floor, int width, int i) {
			while(!isElevator(floor, i) && i > 0 && (reachable[floor][i] == 0 || reachable[floor][i] > reachable[floor][i + 1] + 1)) {
				reachable[floor][i] = reachable[floor][i + 1] + 1;	
				fillElevatorOnLowerFloorToTheLeft(width, i, floor);
				i--;		
			}
			return i;
		}

		protected void fillElevatorOnLowerFloorToTheLeft(int width, int i, int floor) {
			int lowerFloor = floor - 1;
			if(floor > 0 && isElevator(lowerFloor, i)) {
				int elevatorMovingCost = getElevatorMovingCost(lowerFloor, i);
				reachable[lowerFloor][i] = reachable[floor][i] + elevatorMovingCost;
				fillElevatorOnLowerFloorToTheLeft(width, i, lowerFloor);
				if (i < width - 1 && !isElevator(lowerFloor, i + 1)) {
					reachable[lowerFloor][i + 1] = reachable[floor][i] + elevatorMovingCost + 4;
					fillElevatorOnLowerFloorToTheRight(lowerFloor, width, i + 1);
				}
				if (i > 0 && !isElevator(lowerFloor,(i - 1))) {
					reachable[lowerFloor][i - 1] = reachable[floor][i] + elevatorMovingCost + 1;
					fillElevatorOnLowerFloorToTheLeft(width, i - 1, lowerFloor);
				}
			}
		}
		
		protected int findFirstReachablePoint(int floor) {
			int i = 0;
			while (i < reachable[floor].length && reachable[floor][i] == 0) {
				i++;
			}
			return i;
		}

		public int[][] getReachable() {
			return reachable;
		}
		
		public Map<Integer, List<Integer>> getAddedElevators() {
			return addedElevators;
		}
		
		private boolean isElevator(int floor, int position) {
			return initialElevators.getOrDefault(floor, Collections.emptyList()).contains(position) ||
				   addedElevators.getOrDefault(floor, Collections.emptyList()).contains(position);
		}
		
		public List<Direction> getActions(int startPosition) {
			int floor = 0;
			int position = startPosition;
			List<Direction> actions = new ArrayList<>();
			while (reachable[floor][position] > 1) {
				if (isElevator(floor, position)) {
					actions.add(Direction.NONE);
					floor++;
					continue;
				}
				if (position < reachable[floor].length - 1 && reachable[floor][position + 1] > 0 &&
						reachable[floor][position + 1] < reachable[floor][position]) {
					actions.add(Direction.RIGHT);
					position++;
					continue;
				}
				if (position > 0 && reachable[floor][position - 1] > 0 && 
						reachable[floor][position - 1] < reachable[floor][position]) {
					actions.add(Direction.LEFT);
					position--;
					continue;
				}
				throw new IllegalStateException("Nothing to do on floor "+floor+" at "+position);
			}
			return actions;
		}
		
	}

	public static void addElevator(int elevatorFloor, int elevatorPosition, Map<Integer, List<Integer>> allElevators) {
		List<Integer> elevators = allElevators.get(elevatorFloor);
		if (elevators == null) {
			elevators = new ArrayList<>();
			allElevators.put(elevatorFloor, elevators);
		}
		elevators.add(elevatorPosition);
	}
	
	public static void removeElevator(int elevatorFloor, Integer elevatorPosition, Map<Integer, List<Integer>> allElevators) {
		List<Integer> elevators = allElevators.get(elevatorFloor);
		elevators.remove(elevatorPosition);
		if (elevators.isEmpty()) {
			allElevators.remove(elevatorFloor);
		}
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
		private final int[][] reachable;

		public MovesTowardsAnAreaWhichHasReachableUpstairs(Direction direction, int[][] reachable) {
			this.direction = direction;
			this.reachable = reachable;
		}

		@Override
		public boolean applies(int floor, int position) {
			if (floor == reachable.length) {
				return false;
			}
			int[] upstairs = reachable[floor + 1];
			if (upstairs[position] > 0) {
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

		protected boolean searchLeft(int floor, int position, int[] upstairs) {
			for (int i = position; i >= 0; i--) {
				if (upstairs[i] > 0) {
					return true;
				}
				if (isElevator(floor, i)) {
					return false;
				}
			}
			return false;
		}

		protected boolean searchRight(int floor, int position, int[] upstairs) {
			for (int i = position; i < upstairs.length; i++) {
				if (upstairs[i] > 0) {
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

		public AddElevatorIfOnExitPositionAndWeCanAffordIt(int exitFloor, int exitPosition, int[][] reachable) {
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
		private final int[][] reachable;

		public AddElevatorIfElevatorIsTooFarAndWeCanAffordIt(int width, int exitFloor, int[][] reachable) {
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
		private final int[][] reachable;

		public AddElevatorIfWeCanChainThreeAndWeCanAffordIt(int exitFloor, int[][] reachable) {
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
			boolean applies = extraElevatorsWeCanAfford > 0 && floor < exitFloor && floor > 0 && reachable[floor + 1][position] > 0 && 
					isElevator(floor + 1, position) && isElevator(floor - 1, position) && !isElevator(floor, position);
			if (applies) {
				addElevator(floor, position, ELEVATORS);
			}
			return applies;
		}

		
		
	}
	
	static class BecomeElevatorIfNoneIsFound implements Strategy {

		private final int exitFloor;
		private final int[][] reachable;

		public BecomeElevatorIfNoneIsFound(int exitFloor, int[][] reachable) {
			this.exitFloor = exitFloor;
			this.reachable = reachable;
		}

		@Override
		public boolean applies(int floor, int position) {
			if (isElevator(floor, position)) {
				return false;
			}
			boolean floorLacksElevator = floorLacksReachableElevator(floor);
			boolean addingElevator = floorLacksElevator && reachable[floor + 1][position] > 0;
			if (addingElevator) {
				addElevator(floor, position, ELEVATORS);
				nbAdditionalElevators--;
			}
			return addingElevator;
		}

		protected boolean floorLacksReachableElevator(int floor) {
			List<Integer> elevators = ELEVATORS.getOrDefault(floor, Collections.emptyList());
			
			return floor < exitFloor && elevators.stream().noneMatch(elevatorPosition -> reachable[floor][elevatorPosition] > 0);
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
		
		private final int[][] reachable;
		
		public ClosestReachableElevator(int[][] reachable) {
			this.reachable = reachable;
		}
		
		protected boolean reachable(int floor, int elevatorPosition) {
			return reachable[floor][elevatorPosition] > 0;
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
		
		public MovesTowardsElevator(Direction direction, int[][] reachable) {
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
