import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.OptionalDouble;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int lastPeakValue = Integer.MIN_VALUE;
        int biggestLoss = 0;
        for (int i = 0; i < n; i++) {        	
        	int stockValue = in.nextInt();
        	if (stockValue > lastPeakValue) {
        		lastPeakValue = stockValue;        		
        	}
        	int currentLoss = stockValue - lastPeakValue;
        	if (currentLoss < biggestLoss) {
        		biggestLoss = currentLoss;
        	}
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(biggestLoss);
    }
}
