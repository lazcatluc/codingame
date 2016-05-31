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
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int motorbikes = in.nextInt(); // the amount of motorbikes to control
		int necessaryToSurvive = in.nextInt(); // the minimum amount of motorbikes that must
		
		// game loop
		while (true) {
			int S = in.nextInt(); // the motorbikes' speed
			for (int i = 0; i < motorbikes; i++) {
				int X = in.nextInt(); // x coordinate of the motorbike
				int Y = in.nextInt(); // y coordinate of the motorbike
				int A = in.nextInt(); // indicates whether the motorbike is
										// activated "1" or detroyed "0"
			}

			// Write an action using System.out.println()
			// To debug: System.err.println("Debug messages...");

			// A single line containing one of 6 keywords: SPEED, SLOW, JUMP,
			// WAIT, UP, DOWN.
			System.out.println("SPEED");
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
		
		public boolean isClear(int x, int y) {
			if (y < WIDTH && x < lanes[0].length()) {
				return lanes[y].charAt(x) == '.';
			}
			return x >= lanes[0].length();
		}
		
		public boolean isPathClear(int fromX, int fromY, int toX, int toY) {
			if (!isClear(fromX, fromY)) {
				return false;
			}
			if (!isClear(toX, toY)) {
				return false;
			}
			for (int i = fromX + 1; i < toX; i++) {
				int startY = fromY > toY ? toY : fromY;
				int endY = fromY + toY - startY;
				for (int j = startY; j <= endY; j++) {
					if (!isClear(i, j)) {
						return false;
					}
				}
			}
			return true;
		}
	}

}
