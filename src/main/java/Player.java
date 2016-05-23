import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	static final Set<Node> GATEWAYS = new LinkedHashSet<>();
	
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numberOfNodes = in.nextInt(); 
        int numberOfLinks = in.nextInt(); 
        int numberOfGateways = in.nextInt(); 
        for (int i = 0; i < numberOfLinks; i++) {
            int firstNode = in.nextInt(); // N1 and N2 defines a link between these nodes
            int secondNode = in.nextInt();
            Node.get(firstNode).addLink(Node.get(secondNode));
        }
        for (int i = 0; i < numberOfGateways; i++) {
            markGateway(in.nextInt()); // the index of a gateway node
        }

        // game loop
        while (true) {
            int skynetAgentIndex = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn
            
            List<Node> shortestPath = findShortestPathToGatewaysFrom(skynetAgentIndex);
            
            System.out.println(printLink(shortestPath.get(0), shortestPath.get(1)));
        }
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
		
    }

	public static void severLinkBetween(int i, int j) {
		Node.get(i).severLink(Node.get(j));
		Node.get(j).severLink(Node.get(i));
	}

	public static void markGateway(int index) {
		GATEWAYS.add(Node.get(index));
	}

	public static Set<List<Node>> findPathsToGatewaysFrom(int startingPoint) {
		return findPathsToGatewaysFrom(Node.get(startingPoint), Collections.emptyList());
		
	}
	
	private static Set<List<Node>> findPathsToGatewaysFrom(Node start, List<Node> previousNodes) {
		List<Node> completeNodes = new ArrayList<>(previousNodes);
		completeNodes.add(start);
		if (GATEWAYS.contains(start)) {
			return Collections.singleton(completeNodes);
		}
		Set<List<Node>> ret = new LinkedHashSet<>();
		start.getLinks().stream().filter(node -> !previousNodes.contains(node))
				.map(node -> findPathsToGatewaysFrom(node, completeNodes)).forEach(ret::addAll);
		return ret;
	}

	public static List<Node> findShortestPathToGatewaysFrom(int startingPoint) {
		return findPathsToGatewaysFrom(startingPoint).stream().min((path1, path2) -> path1.size() - path2.size()).get();
	}

	public static String printLink(Node start, Node finish) {
		return start.toString() + " " + finish.toString();
	}
}
