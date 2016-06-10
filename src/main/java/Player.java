import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        List<String> map = new ArrayList<>();
        for (int i = 0; i < height; i++) {
        	map.add(in.nextLine()); // one line of the firewall grid
        }
        int rounds = in.nextInt(); // number of rounds left before the end of the game
        int bombs = in.nextInt(); // number of bombs left
        SetCoverGenerator setCoverGenerator = makeSetCoverGenerator(map, width, height, bombs);
        Iterator<Node> nodes = setCoverGenerator.getNodesCoveringAllNodes().iterator();
        // game loop
        while (true) {
         
        	
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
        	if (nodes.hasNext()) {
        		Node node = nodes.next();
        		System.out.println(node.x+" "+node.y);
        	}
        	else {
        		System.out.println("WAIT");
        	}
            rounds = in.nextInt(); // number of rounds left before the end of the game
            bombs = in.nextInt(); // number of bombs left
        }
    }
	
	static SetCoverGenerator makeSetCoverGenerator(List<String> map, int width, int height, int maxBombs) {
		BombMap bombMap = new BombMap(map);
		return new SetCoverGenerator(new ReasonableLocationsGenerator(bombMap, width, height).getReasonableLocation(), 
				new RelevantNodeFinder(bombMap, width, height).getRelevantNodes(), maxBombs);
	}
	
	static Map<Node, Set<Node>> deepCopy(Map<Node, Set<Node>> original) {
		Map<Node, Set<Node>> ret = new LinkedHashMap<>();
		original.forEach((node, set) -> ret.put(node, new HashSet<>(set)));
		return ret;
	}
	
	static class RelevantNodeFinder {
		private final BombMap bombMap;
		private final int width;
		private final int height;

		public RelevantNodeFinder(BombMap bombMap, int width, int height) {
			this.bombMap = bombMap;
			this.width = width;
			this.height = height;
		}
		
		public Set<Node> getRelevantNodes() {
			Set<Node> relevantNodes = new HashSet<>();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {					
					if (bombMap.charAt(x, y) == '@') {
						relevantNodes.add(new Node(x, y));
					}
				}
			}
			return relevantNodes;
		}
	}
	
	static class SetCoverGenerator {
		private final Map<Node, Set<Node>> reasonableLocations;
		private final Set<Node> allNodes;
		private final int maxBombs;
		
		public SetCoverGenerator(Map<Node, Set<Node>> reasonableLocations, Set<Node> allNodes, int maxBombs) {
			this.reasonableLocations = deepCopy(reasonableLocations);
			this.allNodes = new HashSet<>(allNodes);
			this.maxBombs = maxBombs;
		}
		
		public Set<Node> getNodesCoveringAllNodes() {
			if (allNodes.isEmpty()) {
				return new HashSet<>();
			}
			if (reasonableLocations.isEmpty() || maxBombs == 0) {
				return null;
			}
			Iterator<Entry<Node, Set<Node>>> iterator = reasonableLocations.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Node, Set<Node>> currentNode = iterator.next();
				Map<Node, Set<Node>> myReasonableLocations = deepCopy(reasonableLocations);
				Set<Node> myAllNodes = new HashSet<>(allNodes);
				Node node = currentNode.getKey();
				Set<Node> coveredNodes = currentNode.getValue();
				myReasonableLocations.remove(node);
				myAllNodes.removeAll(coveredNodes);
				myReasonableLocations.values().forEach(set -> set.removeAll(coveredNodes));
				List<Node> reasonableNodes = new ArrayList<>(myReasonableLocations.keySet());
				Collections.sort(reasonableNodes, (node1, node2) -> 
						myReasonableLocations.get(node2).size() - 
						myReasonableLocations.get(node1).size());
				Map<Node, Set<Node>> ret = new LinkedHashMap<>();
				reasonableNodes.forEach(myNode -> ret.put(myNode, new HashSet<>(myReasonableLocations.get(myNode))));
				Set<Node> subset = new SetCoverGenerator(myReasonableLocations, myAllNodes, maxBombs - 1).getNodesCoveringAllNodes();
				if (subset != null) {
					subset.add(node);
					return subset;
				}
			}
			return null;
		}
	}
	
	static class ReasonableLocationsGenerator {
		private final BombMap bombMap;
		private final int width;
		private final int height;
		
		public ReasonableLocationsGenerator(BombMap bombMap, int width, int height) {
			this.bombMap = bombMap;
			this.width = width;
			this.height = height;
		}
		
		public Map<Node, Set<Node>> getReasonableLocation() {
			Map<Node, Set<Node>> affectedAreas = new HashMap<>();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {					
					if (bombMap.charAt(x, y) == '.') {
						Node node = new Node(x, y);	
						affectedAreas.put(node, bombMap.getAffectedNodes(node));
					}
				}
			}
			Map<Node, Set<Node>> affectedAreasExcludingThoseAlreadyIncluded = new HashMap<>();
			affectedAreas.forEach((node, set) -> { 
				if (affectedAreasExcludingThoseAlreadyIncluded.values().stream().noneMatch(oldSet -> oldSet.containsAll(set))) {
					removeAllIncluded(affectedAreasExcludingThoseAlreadyIncluded, set);
					affectedAreasExcludingThoseAlreadyIncluded.put(node, set);
				};
			});
			Set<Node> reasonableLocations = affectedAreasExcludingThoseAlreadyIncluded.keySet();
			List<Node> reasonableLocationsMostAffectedNodesFirst = new ArrayList<>(reasonableLocations);
			Collections.sort(reasonableLocationsMostAffectedNodesFirst, (node1, node2) -> 
				affectedAreasExcludingThoseAlreadyIncluded.get(node2).size()-
				affectedAreasExcludingThoseAlreadyIncluded.get(node1).size());
			Map<Node, Set<Node>> ret = new LinkedHashMap<>();
			reasonableLocationsMostAffectedNodesFirst.forEach(node -> ret.put(node,
					new HashSet<>(affectedAreasExcludingThoseAlreadyIncluded.get(node))));
			return ret;
		}
		
		private void removeAllIncluded(Map<Node, Set<Node>> nodes, Set<Node> container) {
			Iterator<Map.Entry<Node, Set<Node>>> entryIt = nodes.entrySet().iterator();
			while (entryIt.hasNext()) {
				if (container.containsAll(entryIt.next().getValue())) {
					entryIt.remove();
				}
			}
		}
	}
	
	static class Node {
		private final int x;
		private final int y;
		public Node(int x, int y) {
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
			Node other = (Node) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "[x=" + x + ", y=" + y + "]";
		}
		
	}
	
	static class BombMap {
		private final int bombPower = 3;
		private final List<String> map;
		
		public BombMap(List<String> map) {
			this.map = new ArrayList<>(map);
		}

		public Set<Node> getAffectedNodes(Node node) {
			Set<Node> nodes = new HashSet<>();
			right(node, nodes);
			left(node, nodes);
			down(node, nodes);
			up(node, nodes);
			return nodes;
		}

		protected char charAt(int x, int y) {
			if (x < 0 || x >= map.get(0).length() || y < 0 || y >= map.size()) {
				return '.';
			}
			return map.get(y).charAt(x);
		}

		protected void up(Node node, Set<Node> nodes) {
			for (int y = -1; y >= -bombPower; y--) {
				char mapChar = charAt(node.x, node.y+y);
				if (mapChar == '#') {
					break;
				}
				if (mapChar == '@') {
					nodes.add(new Node(node.x, node.y+y));
				}
			}
		}


		protected void down(Node node, Set<Node> nodes) {
			for (int y = 1; y <= bombPower; y++) {
				char mapChar = charAt(node.x, node.y+y);
				if (mapChar == '#') {
					break;
				}
				if (mapChar == '@') {
					nodes.add(new Node(node.x, node.y+y));
				}
			}
		}


		protected void left(Node node, Set<Node> nodes) {
			for (int x = -1; x >= -bombPower; x--) {
				char mapChar = charAt(node.x+x, node.y);
				if (mapChar == '#') {
					break;
				}
				if (mapChar == '@') {
					nodes.add(new Node(node.x+x, node.y));
				}
			}
		}


		protected void right(Node node, Set<Node> nodes) {
			for (int x = 1; x <= bombPower; x++) {
				char mapChar = charAt(node.x+x, node.y);
				if (mapChar == '#') {
					break;
				}
				if (mapChar == '@') {
					nodes.add(new Node(node.x+x, node.y));
				}
			}
		}
	}
}
