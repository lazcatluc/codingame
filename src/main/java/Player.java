import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		String startPoint = in.next();
		String endPoint = in.next();
		int numberOfNodes = in.nextInt();
		in.nextLine();
		for (int i = 0; i < numberOfNodes; i++) {
			Node.add(in.nextLine());
		}
		int links = in.nextInt();
		in.nextLine();
		for (int i = 0; i < links; i++) {
			String[] route = in.nextLine().split(" ");
			Node.get(route[0]).addLink(Node.get(route[1]));
		}
		Node start = Node.get(startPoint);
		Node end = Node.get(endPoint);
		Solver solver = new Solver(start, end);
		// Write an action using System.out.println()
		// To debug: System.err.println("Debug messages...");
		Set<Node> path = solver.getPath();
		if (path.isEmpty()) {
			System.out.println("IMPOSSIBLE");
		}
		else {
			path.stream().map(Node::getName).forEach(System.out::println);
		}
    }

	static class NodeWithPath {
		private final Node node;
		private final Set<Node> path;
		private final double totalCost;

		private NodeWithPath(Node node, Set<Node> path, double totalCost) {
			this.node = node;
			this.path = path;
			this.totalCost = totalCost;
		}

		public NodeWithPath(Node start) {
			this.node = start;
			this.path = Collections.singleton(start);
			this.totalCost = 0;
		}

		public Set<NodeWithPath> expand(Set<Node> alreadyExpanded) {
			Set<Node> newPath = new LinkedHashSet<>(path);
			newPath.add(node);
			return node.getLinks().stream().filter(myNode -> !alreadyExpanded.contains(myNode))
					   .map(myNode -> new NodeWithPath(myNode, newPath, totalCost +
							node.location.distanceTo(myNode.location))).collect(Collectors.toSet());
		}
	}

	static class Solver {
		private final Node start;
		private final Node end;
		private final Set<Node> path;

		public Solver(Node start, Node end) {
			this.start = start;
			this.end = end;
			this.path = buildPath();
		}

		private Set<Node> buildPath() {
			if (start.equals(end)) {
				return Collections.singleton(start);
			}
			Queue<NodeWithPath> nodesToBeParsed = new PriorityQueue<>((n1, n2) ->
					Double.compare(n1.totalCost, n2.totalCost));
			Set<Node> expanded = new HashSet<>();
			nodesToBeParsed.add(new NodeWithPath(start));
			while (!nodesToBeParsed.isEmpty()) {
				NodeWithPath next = nodesToBeParsed.poll();
				if (next.node.equals(end)) {
					Set<Node> fullPath = new LinkedHashSet<>(next.path);
					fullPath.add(end);
					return fullPath;
				}
				if (expanded.contains(next.node)) {
					continue;
				}
				expanded.add(next.node);
				nodesToBeParsed.addAll(next.expand(expanded));
			}
			return Collections.emptySet();
		}

		public Set<Node> getPath() {
			return Collections.unmodifiableSet(path);
		}
	}

	static final class Location {
		public static final double EARTH_RADIUS_KILOMETERS = 6371;
		private final double latitude;
		private final double longitude;

		public Location(double latitude, double longitude) {
			this.latitude = Math.toRadians(latitude);
			this.longitude = Math.toRadians(longitude);
		}

		public double distanceTo(Location other) {
			double distanceLongitude = (longitude - other.longitude) *
					Math.cos((latitude + other.latitude) / 2);
			double distanceLatitude = latitude - other.latitude;
			return EARTH_RADIUS_KILOMETERS * Math.sqrt(distanceLongitude * distanceLongitude +
					distanceLatitude * distanceLatitude);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Location)) return false;
			Location location = (Location) o;
			return Double.compare(location.latitude, latitude) == 0 &&
					Double.compare(location.longitude, longitude) == 0;
		}

		@Override
		public int hashCode() {
			return Objects.hash(latitude, longitude);
		}

		@Override
		public String toString() {
			return "Location{" +
					"latitude=" + Math.toDegrees(latitude) +
					", longitude=" + Math.toDegrees(longitude) +
					'}';
		}
	}

    static class Node {
		private final String id;
		private final Location location;
		private final String name;
    	private final Set<Node> linked = new LinkedHashSet<>();
    	private static final Map<String, Node> NODES = new HashMap<>();
    	
    	private Node(String id, String name, Location location) {
			this.id = id;
			this.name = name;
    		this.location = location;
    	}
    	
    	public void addLink(Node node) {
    		linked.add(node);
    	}
    	
    	public static Node get(String id) {
    		return NODES.get(id);
    	}

		public static void add(String commaSeparated) {
			String[] nodeData = commaSeparated.split(",");
			String id = nodeData[0];
			String name = nodeData[1].substring(1, nodeData[1].length() - 1);
			double latitude = Double.parseDouble(nodeData[3]);
			double longitude = Double.parseDouble(nodeData[4]);
			NODES.put(id, new Node(id, name, new Location(latitude, longitude)));
		}

		public Set<Node> getLinks() {
			return linked;
		}

		@Override
		public String toString() {
			return location.toString();
		}
		
		public static void clearNodes() {
			NODES.clear();
		}

		public String getName() {
			return name;
		}
	}


}
