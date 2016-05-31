import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

	static final Set<Node> GATEWAYS = new LinkedHashSet<>();
	
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        readData(in);

        // game loop
        while (true) {
            int skynetAgentIndex = nextInt(in); // The index of the node on which the Skynet agent is positioned this turn
            
            List<Node> shortestPath = findShortestPathToGatewaysFrom(skynetAgentIndex);
            Node nodeWithGateways = shortestPath.get(0);
			for (Node node : GATEWAYS) {
				if (nodeWithGateways.getLinks().contains(node)) {
					serverLinkBetween(nodeWithGateways, node);
					System.out.println(printLink(nodeWithGateways, node));
					break;
				}
			}
        }
    }

	protected static void readData(Scanner in) {
		int numberOfNodes = nextInt(in); 
		int numberOfLinks = nextInt(in); 
        int numberOfGateways = nextInt(in); 
        for (int i = 0; i < numberOfLinks; i++) {
            int firstNode = nextInt(in); // N1 and N2 defines a link between these nodes
            int secondNode = nextInt(in);
            Node.get(firstNode).addLink(Node.get(secondNode));
        }
        for (int i = 0; i < numberOfGateways; i++) {
            markGateway(nextInt(in)); // the index of a gateway node
        }
	}

	protected static int nextInt(Scanner in) {
		int x = in.nextInt();
		System.err.println(x);
		return x;
	}
    
    static class Node {
    	private final int index;
    	private final Set<Node> linked = new LinkedHashSet<>();
    	private static final Map<Integer, Node> NODES = new HashMap<>();
    	
    	private Node(int index) {
    		this.index = index;
    	}
    	
    	public void addLink(Node node) {
    		linked.add(node);
    		node.linked.add(this);
    	}
    	
    	public boolean isConnectedToGateway() {
    		return getGatewaysConnected() > 0;
    	}
    	
    	public int getGatewaysConnected() {
    		Set<Node> gateways = new HashSet<>(GATEWAYS);
    		gateways.retainAll(linked);
    		return gateways.size();
    	}
    	
    	public static Node get(int index) {
    		Node node = NODES.get(index);
    		if (node == null) {
    			node = new Node(index);
    			NODES.put(index, node);
    		}
    		
    		return node;
    	}

		public Set<Node> getLinks() {
			return linked;
		}

		public void severLink(Node node) {
			linked.remove(node);
			node.linked.remove(this);
		}

		@Override
		public String toString() {
			return String.valueOf(index);
		}
		
		public static void clearNodes() {
			NODES.clear();
		}
	
    }

	public static void severLinkBetween(int i, int j) {
		serverLinkBetween(Node.get(i),Node.get(j));
	}
	
	public static void serverLinkBetween(Node first, Node second) {
		second.severLink(first);
		first.severLink(second);
	}

	public static void markGateway(int index) {
		GATEWAYS.add(Node.get(index));
	}

	public static Set<List<Node>> findPathsToGatewaysFrom(int startingPoint) {
		int level = 1;
		do {
			Set<List<Node>> pathsForLevel = findPathsToGatewaysFrom(Node.get(startingPoint), Collections.emptyList(), level);
			if (!pathsForLevel.isEmpty()) {
				return pathsForLevel;
			}
			level++;
		}
		while(true);
	}
	
	protected static Set<List<Node>> findPathsToGatewaysFrom(Node start, List<Node> previousNodes, int lookUpToLevel) {
		List<Node> completeNodes = new ArrayList<>(previousNodes);
		completeNodes.add(start);
		if (GATEWAYS.contains(start)) {
			return Collections.singleton(completeNodes);
		}
		if (lookUpToLevel == 0) {
			return Collections.emptySet();
		}
		Set<List<Node>> ret = new LinkedHashSet<>();
		start.getLinks().stream().filter(node -> !previousNodes.contains(node))
				.map(node -> findPathsToGatewaysFrom(node, completeNodes, lookUpToLevel - 1)).forEach(ret::addAll);
		return ret;
	}

	public static List<Node> findShortestPathToGatewaysFrom(int startingPoint) {
		Set<List<Node>> pathsToGateways = findPathsToGatewaysFrom(startingPoint);
		return findNodesWithMostGateways(pathsToGateways);
	}

	protected static List<Node> findNodesWithMostGateways(Set<List<Node>> minimalPaths) {
		Set<Node> closeNodes = minimalPaths.stream().map(Player::findNodeBeforeGateway).collect(Collectors.toSet());
		List<Node> nodesWithDirectConnections = new ArrayList<>(findOtherNeighborsWithDirectConnections(closeNodes));
		Collections.sort(nodesWithDirectConnections, (node1, node2) -> node2.getGatewaysConnected() - node1.getGatewaysConnected());
		return nodesWithDirectConnections;
	}
	
	protected static Set<Node> findOtherNeighborsWithDirectConnections(Set<Node> startingNodes) {
		Set<Node> newNodes = new LinkedHashSet<>(startingNodes);
		startingNodes.stream().map(Node::getLinks)
			.forEach(set -> set.stream().filter(Node::isConnectedToGateway).forEach(newNodes::add));
		if (newNodes.equals(startingNodes)) {
			return startingNodes;
		}
		return findOtherNeighborsWithDirectConnections(newNodes);
	}
	
	private static Node findNodeBeforeGateway(List<Node> path) {
		return path.get(path.size() - 2);
	}

	public static String printLink(Node start, Node finish) {
		return start.toString() + " " + finish.toString();
	}
}
