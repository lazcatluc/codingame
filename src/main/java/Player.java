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
		int M = in.nextInt(); // the amount of motorbikes to control
		int V = in.nextInt(); // the minimum amount of motorbikes that must
								// survive
		String L0 = in.next(); // L0 to L3 are lanes of the road. A dot
								// character . represents a safe space, a zero 0
								// represents a hole in the road.
		String L1 = in.next();
		String L2 = in.next();
		String L3 = in.next();

		// game loop
		while (true) {
			int S = in.nextInt(); // the motorbikes' speed
			for (int i = 0; i < M; i++) {
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

}
