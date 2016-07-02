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

    public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		String startPoint = in.next();
		String endPoint = in.next();
		int N = in.nextInt();
		in.nextLine();
		for (int i = 0; i < N; i++) {
			String stopName = in.nextLine();
		}
		int M = in.nextInt();
		in.nextLine();
		for (int i = 0; i < M; i++) {
			String route = in.nextLine();
		}

		// Write an action using System.out.println()
		// To debug: System.err.println("Debug messages...");

		System.out.println("IMPOSSIBLE");
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


}
