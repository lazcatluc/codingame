import java.util.Arrays;
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
        int length = in.nextInt();
        long[] ys = new long[length];
        long minX = Long.MAX_VALUE;
        long maxX = Long.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            int x = in.nextInt();
            if (x < minX) {
            	minX = x;
            }
            if (x > maxX) {
            	maxX = x;
            }
            
            ys[i] = in.nextInt();
        }
        long cableLength = computeCableLength(ys, minX, maxX);

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(cableLength);
    }

	static long computeCableLength(long[] ys, long minX, long maxX) {
		Arrays.sort(ys);
		long median = ys[ys.length / 2];  
        long cableLength = maxX - minX;
        for (long y : ys) {
        	cableLength += Math.abs(y - median);
        }
		return cableLength;
	}
	
}
