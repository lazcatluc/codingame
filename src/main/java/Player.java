import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int motorbikes = in.nextInt(); // the amount of motorbikes to control
		int necessaryToSurvive = in.nextInt(); // the minimum amount of motorbikes that must
		Runway runway = new Runway(in);
		Queue<Action> actions = new LinkedList<>();
		// game loop
		while (true) {
			Set<Motorcycle> motorcycles = new HashSet<>();
			int speed = in.nextInt(); // the motorbikes' speed
			for (int i = 0; i < motorbikes; i++) {
				int x = in.nextInt(); // x coordinate of the motorbike
				int y = in.nextInt(); // y coordinate of the motorbike
				int active = in.nextInt(); // indicates whether the motorbike is
										// activated "1" or detroyed "0"
				motorcycles.add(new Motorcycle.Builder().atX(x).atY(y)
						.goingAt(speed).active(active).build());
			}
			if (actions.isEmpty()) {
				SearchMotorcyclesPath searchMotorcyclesPath = new SearchMotorcyclesPath(motorcycles, runway, necessaryToSurvive);
				actions.addAll(searchMotorcyclesPath.getActionsToGoal());
			}
			// Write an action using System.out.println()
			// To debug: System.err.println("Debug messages...");

			// A single line containing one of 6 keywords: SPEED, SLOW, JUMP,
			// WAIT, UP, DOWN.
			if (actions.isEmpty()) {
				System.out.println(Action.WAIT);
			}
			else {
				System.out.println(actions.poll());
			}
		}
	}
	
	static class SearchMotorcyclesPath {
		private final int maxPathLength = 50;
		private final Set<Motorcycle> motorcycles;
		private final Runway runway;
		private final int goal;
		private final int necessaryToSurvive;
		private final int okSpeed;
		
		public SearchMotorcyclesPath(Collection<Motorcycle> motorcycles, Runway runway, int necessaryToSurvive) {
			this.motorcycles = new HashSet<>(motorcycles);
			this.runway = runway;
			goal = runway.length();
			okSpeed = maxPathLength / goal + 1;
			this.necessaryToSurvive = necessaryToSurvive; 
		}
		
		private boolean isValid(MotorcyclesOnRunway motorcyclesOnRunway) {
			return motorcyclesOnRunway.activeMotorcycles() >= necessaryToSurvive;
		}
		
		private boolean isGoal(MotorcyclesOnRunway motorcyclesOnRunway) {
			return motorcyclesOnRunway.furthestMotorcycleLevel() >= goal;
		}
		
		public List<Action> getActionsToGoal() {
			return getActionsToGoal(new MotorcyclesOnRunway(motorcycles, runway, okSpeed), 0);
		}

		private List<Action> getActionsToGoal(MotorcyclesOnRunway motorcyclesOnRunway, int pathLength) {
			if (pathLength > maxPathLength) {
				return null;
			}
			if (!isValid(motorcyclesOnRunway)) {
				return null;
			}
			if (isGoal(motorcyclesOnRunway)) {
				return Collections.emptyList();
			}
			for (MotorcyclesOnRunway nextMotorcyclesOnRunway : motorcyclesOnRunway.nextMotorcyclesOnRunways()) {
				List<Action> actions = getActionsToGoal(nextMotorcyclesOnRunway, pathLength + 1);
				if (actions == null) {
					continue;
				}
				Action action = getActionFor(nextMotorcyclesOnRunway, motorcyclesOnRunway);
				List<Action> ret = new ArrayList<>(actions.size() + 1);
				ret.add(action);
				ret.addAll(actions);
				return ret;
			}
			return null;
		}

		private Action getActionFor(MotorcyclesOnRunway nextMotorcyclesOnRunway,
				MotorcyclesOnRunway currentMotorcyclesOnRunway) {
			return currentMotorcyclesOnRunway.allowedActions().stream().filter(action -> 
				new MotorcyclesOnRunway(currentMotorcyclesOnRunway.actedOnMotorcycles(action),
						runway, okSpeed).equals(nextMotorcyclesOnRunway)).findAny().get();
		}

		
	}
	
	static class MotorcyclesOnRunway {
		private final Set<Motorcycle> motorcycles;
		private final Runway runway;
		private final int okSpeed;
		
		public MotorcyclesOnRunway(Collection<Motorcycle> motorcycles, Runway runway, int okSpeed) {
			this.motorcycles = new HashSet<>(motorcycles);
			this.runway = runway;
			this.okSpeed = okSpeed;
		}

		public Set<Action> allowedActions() {
			Set<Action> actions = EnumSet.allOf(Player.Action.class);
			if (hasActiveMotorcycleOnLane(0)) {
				actions.remove(Action.UP);
			}
			if (hasActiveMotorcycleOnLane(runway.lastLane())) {
				actions.remove(Action.DOWN);
			}
			return actions;
		}
		
		public List<MotorcyclesOnRunway> nextMotorcyclesOnRunways() {
			List<MotorcyclesOnRunway> nextStates = allowedActions().stream().map(this::actedOnMotorcycles)
					.map(afterActionMotorcycles -> new MotorcyclesOnRunway(afterActionMotorcycles, runway, okSpeed))
					.collect(Collectors.toList());
			Comparator<MotorcyclesOnRunway> c0 = (mor1, mor2) -> mor1.speedDelta() - mor2.speedDelta();
			Comparator<MotorcyclesOnRunway> c1 = (mor1, mor2) -> mor1.activeMotorcycles() - mor2.activeMotorcycles();
			Comparator<MotorcyclesOnRunway> c2 = (mor1, mor2) -> mor1.furthestMotorcycleLevel() - mor2.furthestMotorcycleLevel();
			Collections.sort(nextStates, c2.reversed().thenComparing(c0).thenComparing(c1.reversed()));
			return nextStates;
		}
		
		private Set<Motorcycle> actedOnMotorcycles(Action action) {
			return motorcycles.stream().map(motorcycle -> new MotorcyleActingOnRunway(motorcycle, runway, action))
				.map(MotorcyleActingOnRunway::getAfterAction).collect(Collectors.toSet());
		}
		
		public int activeMotorcycles() {
			return (int)motorcycles.stream().filter(Motorcycle::isActive).count();
		}
		
		public int furthestMotorcycleLevel() {
			return motorcycles.stream().map(Motorcycle::getX).max((i1, i2) -> i1 - i2).orElse(0);
		}
		
		public int speedDelta() {
			return Math.abs(motorcycles.stream().findAny().get().getSpeed() - okSpeed);
		}
		
		private boolean hasActiveMotorcycleOnLane(int lane) {
			return motorcycles.stream().filter(Motorcycle::isActive).map(Motorcycle::getY).anyMatch(x -> x.equals(lane));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((motorcycles == null) ? 0 : motorcycles.hashCode());
			result = prime * result + ((runway == null) ? 0 : runway.hashCode());
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
			MotorcyclesOnRunway other = (MotorcyclesOnRunway) obj;
			if (motorcycles == null) {
				if (other.motorcycles != null)
					return false;
			} else if (!motorcycles.equals(other.motorcycles))
				return false;
			if (runway == null) {
				if (other.runway != null)
					return false;
			} else if (!runway.equals(other.runway))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MotorcyclesOnRunway [motorcycles=" + motorcycles + ", runway=" + runway + "]";
		}
		
		

	}
	

	static class Runway {
		static final int WIDTH = 4;
		final String[] lanes;

		public Runway(Scanner in) {
			lanes = new String[WIDTH];
			for (int i = 0; i < WIDTH; i ++) {
				lanes[i] = in.next();
			}
		}
		
		public int length() {
			return lanes[0].length();
		}

		public int lastLane() {
			return lanes.length - 1;
		}

		public boolean isClear(int x, int y) {
			if (x < 0) {
				return false;
			}
			if (y < WIDTH && x < lanes[0].length()) {
				return lanes[y].charAt(x) == '.';
			}
			return x >= length();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(lanes);
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
			Runway other = (Runway) obj;
			if (!Arrays.equals(lanes, other.lanes))
				return false;
			return true;
		}
		
		
	}
	
	static class MotorcyleActingOnRunway {
		private final Motorcycle motorcycle;
		private final Runway runway;
		private final Action action;
		private final Motorcycle afterAction;
		
		public MotorcyleActingOnRunway(Motorcycle motorcycle, Runway runway, Action action) {
			this.motorcycle = motorcycle;
			this.runway = runway;
			this.action = action;
			this.afterAction = motorcycle.act(action);
		}
		
		public Motorcycle getAfterAction() {
			if (isPathClear()) {
				return afterAction;
			}
			return new Motorcycle.Builder(motorcycle).active(false).build();
		}
		
		public boolean isPathClear() {
			if (!runway.isClear(motorcycle.getX(), motorcycle.getY())) {
				return false;
			}
			if (!runway.isClear(afterAction.getX(), afterAction.getY())) {
				return false;
			}
			if (action == Action.JUMP) {
				return true;
			}
			for (int i = motorcycle.getX() + 1; i < afterAction.getX(); i++) {
				int startY = motorcycle.getY() > afterAction.getY() ? afterAction.getY() : motorcycle.getY();
				int endY = motorcycle.getY() + afterAction.getY() - startY;
				for (int j = startY; j <= endY; j++) {
					if (!runway.isClear(i, j)) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public String toString() {
			return "MotorcyleActingOnRunway [motorcycle=" + motorcycle + ", action=" + action + ", afterAction="
					+ afterAction + "]";
		}
		
		
	}
	
	enum Action {
		SPEED, SLOW, JUMP, WAIT, UP, DOWN;
	}
	
	static class Motorcycle {

		private final int x;
		private final int y;
		private final boolean active;
		private final int speed;
		
		private Motorcycle(Builder builder) {
			this.x = builder.x;
			this.y = builder.y;
			this.active = builder.active;
			this.speed = builder.speed;
		}		
		
		public Motorcycle act(Action action) {
			Builder builder = new Builder(this);
			if (action == Action.SPEED) {
				builder.goingAt(speed + 1);
			}
			if (action == Action.SLOW) {
				builder.goingAt(speed - 1);
			}
			if (active) {
				builder.atX(x + builder.speed);
				if (action == Action.UP) {
					builder.atY(y - 1);
				}
				if (action == Action.DOWN) {
					builder.atY(y + 1);
				}
			}
			return builder.build();
		}
		
		static class Builder {
			int x = 0;
			int y = 0;
			boolean active = true;
			int speed = 0;
			
			public Builder() {
				
			}
			
			public Builder(Motorcycle motorcycle) {
				x = motorcycle.x;
				y = motorcycle.y;
				active = motorcycle.active;
				speed = motorcycle.speed;
			}

			public Builder atX(int x) {
				this.x = x;
				return this;
			}
			
			public Builder atY(int y) {
				this.y = y;
				return this;
			}
			
			public Builder goingAt(int speed) {
				this.speed = speed;
				return this;
			}
			
			public Builder active(boolean active) {
				this.active = active;
				return this;
			}
			
			public Builder active(int active) {
				this.active = active > 0;
				return this;
			}
			
			public Motorcycle build() {
				return new Motorcycle(this);
			}
		}

		public int getSpeed() {
			return speed;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		public boolean isActive() {
			return active;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (active ? 1231 : 1237);
			result = prime * result + speed;
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
			Motorcycle other = (Motorcycle) obj;
			if (active != other.active)
				return false;
			if (speed != other.speed)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Motorcycle [x=" + x + ", y=" + y + ", active=" + active + ", speed=" + speed + "]";
		}
		
		
	}

}

