import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	private static final Map<Integer, Node> NODES = new HashMap<>();
	private static final Map<Node, Integer> MAX_CHAIN_LENGTH = new HashMap<>();
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // the number of relationships of influence
        for (int i = 0; i < n; i++) {
            int x = in.nextInt(); // a relationship of influence between two people (x influences y)
            int y = in.nextInt();
            Node.get(y).addLink(Node.get(x));
        }
        
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");


        // The number of people involved in the longest succession of influences
        System.out.println(NODES.values().stream().map(Player::parse).max((i1, i2) -> i1 - i2).orElse(0));
    }
	
	static int parse(Node node) {
		if (MAX_CHAIN_LENGTH.containsKey(node)) {
			return MAX_CHAIN_LENGTH.get(node);
		}
		node.getLinks().stream().filter(myNode -> !MAX_CHAIN_LENGTH.containsKey(myNode)).forEach(Player::parse);
		Integer maxChain = node.getLinks().stream().map(MAX_CHAIN_LENGTH::get).max((i1, i2) -> i1 - i2).orElse(0) + 1;
		MAX_CHAIN_LENGTH.put(node, maxChain);
		return maxChain;
	}
    
    static class Node {
    	private final int index;
    	private final Set<Node> linked = new LinkedHashSet<>();
    	
    	private Node(int index) {
    		this.index = index;
    	}
    	
    	public void addLink(Node node) {
    		linked.add(node);
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

		@Override
		public String toString() {
			return String.valueOf(index);
		}
		
    }

}
