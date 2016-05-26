import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int firstNumber = in.nextInt();
        int line = in.nextInt();
        List<Integer> currentLine = Arrays.asList(firstNumber);
        
        while (line > 1) {
        	line--;
        	currentLine = transform(currentLine);
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println("answer");
    }

	static List<Integer> transform(List<Integer> currentLine) {
		List<Integer> result = new ArrayList<>();
		result.add(1);
		result.addAll(currentLine);
		return result;
	}
	
}
