import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	static final int TOTAL_DIGITS = 20;
	static final Map<List<String>, Integer> DIGITS = new HashMap<>(TOTAL_DIGITS);
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int digitLength = in.nextInt();
        int digitHeight = in.nextInt();
        buildDigits(in, digitLength, digitHeight);
        int S1 = in.nextInt();
        for (int i = 0; i < S1; i++) {
            String num1Line = in.next();
        }
        int S2 = in.nextInt();
        for (int i = 0; i < S2; i++) {
            String num2Line = in.next();
        }
        String operation = in.next();

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println("result");
    }

	static List<List<String>> buildDigits(Scanner in, int digitLength, int digitHeight) {
		List<List<String>> digits = new ArrayList<>(TOTAL_DIGITS);
        for (int i = 0; i < TOTAL_DIGITS; i++) {
        	digits.add(new ArrayList<>(digitHeight));
        }
        for (int i = 0; i < digitHeight; i++) {
            String numeral = in.next();
            for (int j = 0; j < TOTAL_DIGITS; j++) {
            	digits.get(j).add(numeral.substring(j*digitLength, (j + 1)*digitLength));
            }
        }
        return digits;
	}
}

