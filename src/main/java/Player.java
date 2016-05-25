import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // the number of adjacency relations
        for (int i = 0; i < n; i++) {
            int xi = in.nextInt(); // the ID of a person which is adjacent to yi
            int yi = in.nextInt(); // the ID of a person which is adjacent to xi
            Node.get(xi).addNeighbor(Node.get(yi));
            Node.get(yi).addNeighbor(Node.get(xi));
        }
        Node.buildLeaves();
        int steps = 0;
        do {
        	Set<Node> leaves = Node.getLeaves();
        	if (leaves.size() < 2) {
        		break;
        	}
        	leaves.stream().forEach(Node::kill);
        	steps++;
        }
        while(true);
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");


        // The minimal amount of steps required to completely propagate the advertisement
        System.out.println(steps);
    }
	
	static class Node {
		private static final Map<Integer, Node> NODES = new HashMap<>();
		private static final Set<Node> LEAVES = new HashSet<>();
		private final Set<Node> neighbors = new HashSet<>();
		
		private Node() {
		}
		
		public static Set<Node> getLeaves() {
			return new HashSet<>(LEAVES);
		}

		public void addNeighbor(Node node) {
			neighbors.add(node);
		}
		
		public void kill() {
			neighbors.forEach(neighbor -> {
				neighbor.neighbors.remove(this);
				if (neighbor.isLeaf()) {
					LEAVES.add(neighbor);
				}
			});
			LEAVES.remove(this);
		}
		
		public boolean isLeaf() {
			return neighbors.size() == 1;
		}
		
		public static Node get(Integer i) {
			Node node = NODES.get(i);
			if (node == null) {
				node = new Node();
				NODES.put(i, node);
			}
			return node;
		}
		
		public static void buildLeaves() {
			NODES.values().stream().filter(Node::isLeaf).forEach(LEAVES::add);
		}

	}
	
}
