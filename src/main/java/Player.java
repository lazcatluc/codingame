import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
        int width = in.nextInt();
        int height = in.nextInt();
        in.nextLine();
        List<String> map = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
        	map.add(in.nextLine());
        }
        WaterProspector waterProspector = new WaterProspector(map);
        int numberOfProspects = in.nextInt();
        for (int i = 0; i < numberOfProspects; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            System.out.println(waterProspector.getWaterArea(x, y));
        }
//        for (int i = 0; i < numberOfProspects; i++) {
//
//            // Write an action using System.out.println()
//            // To debug: System.err.println("Debug messages...");
//
//            System.out.println("answer");
//        }
    }
	
	static class WaterProspector {
		private final List<String> map;
		private final Map<Cell, Integer> waterCells = new HashMap<>();

		public WaterProspector(List<String> map) {
			this.map = map;
		}
		
		public int getWaterArea(int x, int y) {
			Cell cell = new Cell(x, y);
			if (!isWater(cell)) {
				return 0;
			}
			Integer area = waterCells.get(cell);
			if (area != null) {
				return area;
			}
			Set<Cell> currentCells = new HashSet<>();
			Queue<Cell> cellsToBeProcessed = new LinkedList<>();
			currentCells.add(cell);
			cellsToBeProcessed.offer(cell);
			area = 1;
			while (!cellsToBeProcessed.isEmpty()) {
				Cell currentCell = cellsToBeProcessed.poll();
				Set<Cell> unprocessedNeighbors = getNeighbors(currentCell).stream().filter(this::isWater)
						.filter(neighbor -> !currentCells.contains(neighbor)).collect(Collectors.toSet());
				area += unprocessedNeighbors.size();
				currentCells.addAll(unprocessedNeighbors);
				cellsToBeProcessed.addAll(unprocessedNeighbors);
			}
			final int myArea = area; 
			currentCells.forEach(processedCell -> waterCells.put(processedCell, myArea));
			return area;
		}

		private boolean isWater(Cell cell) {
			return map.get(cell.y).charAt(cell.x) == '0';
		}
		
		private Collection<Cell> getNeighbors(Cell currentCell) {
			Set<Cell> neighbors = new HashSet<>();
			if (currentCell.x > 0) {
				neighbors.add(new Cell(currentCell.x - 1, currentCell.y));
			}
			if (currentCell.y > 0) {
				neighbors.add(new Cell(currentCell.x, currentCell.y - 1));
			}
			if (currentCell.x < map.get(0).length() - 1) {
				neighbors.add(new Cell(currentCell.x + 1, currentCell.y));
			}
			if (currentCell.y < map.size() - 1) {
				neighbors.add(new Cell(currentCell.x, currentCell.y + 1));
			}
			return neighbors;
		}
	}
	
	static class Cell {
		private final int x;
		private final int y;
		
		public Cell(int x, int y) {
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
			Cell other = (Cell) obj;
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
}

