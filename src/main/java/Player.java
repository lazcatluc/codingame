import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	public static void main(String args[]) throws InterruptedException {
		run(new MyScanner(new Scanner(System.in)), System.out);
	}

	protected static void run(MyCustomScanner in, PrintStream out) throws InterruptedException {
		ThorGiantsState state = initState(in);
		int maxExpansion = 20;
		List<Action> actions = buildActions(state, maxExpansion);
		int action = 0;
		// game loop
		while (true) {
			// Write an action using System.out.println()
			// To debug: System.err.println("Debug messages...");

			// The movement or action to be carried out: WAIT STRIKE N NE E SE S
			// SW W or N
			out.println(actions.get(action++));
			state = state.nextState();
			Location newThor = state.thor;
			ThorGiantsState newState = buildInitialStateWithThor(in, newThor);
			
			if (!state.areGiantsAtLocations(newState.giants) || action == actions.size()) {
				state = newState;
				actions = buildActions(state, maxExpansion);
				action = 0;
			}
		}
	}
	
	protected static List<Action> buildActions(ThorGiantsState initialState, int maxExpansion) {
		List<Action> actions = new ArrayList<>();
		int expansions = 0;
		while (Double.compare(initialState.score(), 0d) > 0d) {
			initialState.expand();
			expansions++;
			if (expansions == maxExpansion) {
				break;
			}
		}
		Player.ThorGiantsState state = initialState;
		while (state.isExpanded()) {
			actions.add(state.nextAction());
			state = state.nextState();
		}
		return actions;
	}

	protected static ThorGiantsState solve(PrintStream out, Player.MyCustomScanner in) {
		ThorGiantsState initialState = initState(in);
		while (Double.compare(initialState.score(), 0d) > 0d) {
			initialState.expand();
		}
		Player.ThorGiantsState state = initialState;
		while (state.isExpanded()) {
			out.println(state.nextAction());
			state.expand();
			state = state.nextState();
		}
		return initialState;
	}

	protected static ThorGiantsState initState(MyCustomScanner in) {
		int thorX = in.nextInt();
		int thorY = in.nextInt();
		Location thor = new Location(thorX, thorY);
		return buildInitialStateWithThor(in, thor);
	}

	protected static ThorGiantsState buildInitialStateWithThor(MyCustomScanner in, Location thor) {
		Set<Location> giants = new HashSet<>();
		int hammerStrikes = in.nextInt(); // the remaining number of hammer
											// strikes

		int giantsCount = in.nextInt(); // the number of giants
		for (int i = 0; i < giantsCount; i++) {
			int giantX = in.nextInt();
			int giantY = in.nextInt();
			giants.add(new Location(giantX, giantY));
		}
		//System.err.println(in);
		return new ThorGiantsState(giants, thor, hammerStrikes);
	}

	static class ThorGiantsState {
		private static final int MAX_STRIKE = 4;
		private final Set<Location> giants;
		private final Location thor;
		private final int availableStrikes;
		private Map<ThorGiantsState, Action> expandedStates;
		private final Set<Location> strikableGiants;
		private final int distanceToClosestStrikableGiant;
		private final int distanceToFurthestNonStrikableGiant;

		public ThorGiantsState(Collection<Location> giants, Location thor, int availableStrikes) {
			this.giants = new HashSet<>(giants);
			this.thor = thor;
			this.availableStrikes = availableStrikes;
			this.strikableGiants = strikableGiants();
			this.distanceToClosestStrikableGiant = distanceToClosestStrikableGiant();
			this.distanceToFurthestNonStrikableGiant = distanceToFurthestNonStrikableGiant();
		}

		public boolean areGiantsAtLocations(Set<Location> newGiants) {
			Set<Location> missingGiants = new HashSet<>(giants);
			missingGiants.removeAll(newGiants);
			Set<Location> extraGiants = new HashSet<>(newGiants);
			extraGiants.removeAll(giants);
			boolean fail = false;
			if (!missingGiants.isEmpty()) {
				fail = true;
				System.err.println("Missing giants: "+missingGiants);
			}
			if (!extraGiants.isEmpty()) {
				fail = true;
				System.err.println("Extra giants: "+extraGiants);
			}
			return !fail;
		}

		public boolean isSolved() {
			return giants.isEmpty();
		}

		public boolean isFailed() {
			if (isSolved()) {
				return false;
			}
			return availableStrikes == 0 || giants.contains(thor);
		}

		private Set<Location> strikableGiants() {
			return giants.stream().filter(giant -> thor.distanceTo(giant) <= MAX_STRIKE).collect(Collectors.toSet());
		}

		public Set<Location> getStrikableGiants() {
			return Collections.unmodifiableSet(strikableGiants);
		}

		private int distanceToClosestStrikableGiant() {
			return strikableGiants().stream().map(thor::distanceTo).min((i1, i2) -> i1 - i2).orElse(MAX_STRIKE);
		}

		public int getDistanceToClosestStrikableGiant() {
			return distanceToClosestStrikableGiant;
		}

		private int distanceToFurthestNonStrikableGiant() {
			return giants.stream().map(thor::distanceTo).filter(i -> i > MAX_STRIKE).min((i1, i2) -> i1 - i2)
					.orElse(MAX_STRIKE + 1);
		}

		public int getDistanceToFurthestNonStrikableGiant() {
			return distanceToFurthestNonStrikableGiant;
		}

		public double score() {
			if (isSolved()) {
				return 0;
			}
			if (isFailed()) {
				return Integer.MAX_VALUE;
			}
			if (expandedStates == null) {
				return 1.0 * (giants.size() - strikableGiants.size()) / availableStrikes
						+ distanceToFurthestNonStrikableGiant - distanceToClosestStrikableGiant;
			}
			return expandedStates.keySet().iterator().next().score();
		}

		public void expand() {
			if (isSolved()) {
				return;
			}
			
			if (expandedStates == null) {
				expandedStates = new LinkedHashMap<>();
				thor.getValidActions().forEach(action -> {
					ThorGiantsState state = expandWith(action);
					expandedStates.put(state, action);
				});
			} else {
				expandedStates.keySet().iterator().next().expand();
			}
			expandedStates = sortByScore(expandedStates);

		}
		
		private Map<ThorGiantsState, Action> sortByScore(Map<ThorGiantsState, Action> original) {
			Comparator<ThorGiantsState> stateComparator = (state1, state2) -> Double.compare(state1.score(),
					state2.score());
			List<ThorGiantsState> keys = new ArrayList<>(original.keySet());
			Collections.sort(keys, stateComparator);
			Map<ThorGiantsState, Action> ret = new LinkedHashMap<>();
			keys.forEach(key -> ret.put(key, original.get(key)));
			return ret;
		}

		private ThorGiantsState expandWith(Action action) {
			List<Location> giants = new ArrayList<>(action == Action.STRIKE ? killCloseGiants() : this.giants);
			Collections.sort(giants, (l1, l2) -> thor.distanceTo(l1) - thor.distanceTo(l2));
			int newAvailableStrikes = action == Action.STRIKE ? (availableStrikes - 1) : availableStrikes;
			Location newThor = thor.getNewLocationFor(action);
			Set<Location> movedGiants = new HashSet<>();
			Iterator<Location> giantsIt = giants.iterator();
			while (giantsIt.hasNext()) {
				Location giant = giantsIt.next();
				Location newGiant = giant.moveTowards(newThor).getValue();
				if (movedGiants.contains(newGiant) || giants.contains(newGiant)) {
					movedGiants.add(giant);
				} else {
					movedGiants.add(newGiant);
				}
				giantsIt.remove();
			}

			return new ThorGiantsState(movedGiants, newThor, newAvailableStrikes);
		}

		private Set<Location> killCloseGiants() {
			Set<Location> giants = new HashSet<>(this.giants);
			giants.removeAll(getStrikableGiants());
			return giants;
		}

		@Override
		public String toString() {
			return "ThorGiantsState [score=" + score() + ", giants=" + giants + ", thor=" + thor + ", availableStrikes="
					+ availableStrikes + ", expandedStates=" + expandedStates + ", strikableGiants=" + strikableGiants
					+ ", distanceToClosestStrikableGiant=" + distanceToClosestStrikableGiant
					+ ", distanceToFurthestNonStrikableGiant=" + distanceToFurthestNonStrikableGiant + "]";
		}

		public boolean isExpanded() {
			return expandedStates != null;
		}

		public ThorGiantsState nextState() {
			return expandedStates.keySet().iterator().next();
		}

		public Action nextAction() {
			return expandedStates.entrySet().iterator().next().getValue();
		}

	}

	static class Location {
		public static final Location BOTTOM_RIGHT = new Location(39, 17);

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
			return "(" + x + "," + y + ")";
		}

		private void addIfValid(Action action, Location location, Map<Action, Location> locations) {
			if (location.x < 0 || location.y < 0 || location.x > BOTTOM_RIGHT.x || location.y > BOTTOM_RIGHT.y) {
				return;
			}
			locations.put(action, location);
		}

		public Map<Action, Location> getNeighbors() {
			Map<Action, Location> ret = new LinkedHashMap<>();
			Arrays.asList(Action.E, Action.W, Action.N, Action.S, Action.NE, Action.NW, Action.SE, Action.SW)
					.forEach(action -> addIfValid(action, getNewLocationFor(action), ret));
			return ret;
		}

		public int distanceTo(Location other) {
			return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
		}

		public Map.Entry<Action, Location> moveTowards(Location other) {
			if (this.equals(other)) {
				return Collections.singletonMap(Action.WAIT, this).entrySet().iterator().next();
			}
			return getNeighbors().entrySet().stream()
					.min((l1, l2) -> other.distanceTo(l1.getValue()) - other.distanceTo(l2.getValue())).get();
		}

		public Set<Action> getValidActions() {
			Set<Action> valid = EnumSet.allOf(Action.class);
			if (x == BOTTOM_RIGHT.x) {
				valid.removeAll(Arrays.asList(Action.E, Action.SE, Action.NE));
			}
			if (x == 0) {
				valid.removeAll(Arrays.asList(Action.W, Action.SW, Action.NW));
			}
			if (y == 0) {
				valid.removeAll(Arrays.asList(Action.N, Action.NE, Action.NW));
			}
			if (y == BOTTOM_RIGHT.y) {
				valid.removeAll(Arrays.asList(Action.S, Action.SE, Action.SW));
			}
			return valid;
		}

		public Location getNewLocationFor(Action action) {
			switch (action) {
			case N:
				return new Location(x, y - 1);
			case S:
				return new Location(x, y + 1);
			case E:
				return new Location(x + 1, y);
			case W:
				return new Location(x - 1, y);
			case NE:
				return new Location(x + 1, y - 1);
			case NW:
				return new Location(x - 1, y - 1);
			case SE:
				return new Location(x + 1, y + 1);
			case SW:
				return new Location(x - 1, y + 1);
			default:
				return this;
			}
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
			for (int i = 1; i < lines.size(); i++) {
				sb.append(",\n");
				appendLine(i, sb);
			}
			return sb.toString();
		}

		private void appendLine(int line, StringBuilder sb) {
			sb.append("\"").append(lines.get(line).replaceAll("\"", "\\\"")).append("\"");
		}

	}

	enum Action {
		WAIT, STRIKE, N, E, S, W, NE, NW, SE, SW;
	}
}
