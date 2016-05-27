import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	static final int TOTAL_DIGITS = 20;
	static final Map<List<String>, Character> DIGITS = new HashMap<>(TOTAL_DIGITS);
	static final Map<Character, List<String>> DIGITS_BACK = new HashMap<>(TOTAL_DIGITS);
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int digitLength = in.nextInt();
        int digitHeight = in.nextInt();
        List<List<String>> digits = buildDigits(in, digitLength, digitHeight);
        for (int i = 0; i < digits.size(); i++) {
        	DIGITS.put(digits.get(i), Character.forDigit(i, TOTAL_DIGITS));
        	DIGITS_BACK.put(Character.forDigit(i, TOTAL_DIGITS), digits.get(i));
        }
        int numberLength = in.nextInt();
        long first = readNumber(in, digitHeight, numberLength);
        numberLength = in.nextInt();
        long second = readNumber(in, digitHeight, numberLength);
        String operation = in.next();
        long result = operate(operation, first, second);
        String resultBaseDigits = Long.toString(result, TOTAL_DIGITS);
        writeNumber(resultBaseDigits);
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");


    }
	
	private static long operate(String operation, long arg1, long arg2) {
		switch(operation) {
		case "+": return arg1 + arg2;
		case "-": return arg1 - arg2;
		case "*": return arg1 * arg2;
		case "/": return arg1 / arg2;
		default: throw new UnsupportedOperationException(operation);
		}
	}

	private static long readNumber(Scanner in, int digitHeight, int numberLength) {
		List<Character> number = new ArrayList<>();
        for (int i = 0; i < numberLength / digitHeight; i++) {
        	List<String> digitList = new ArrayList<>();
        	for (int j = 0; j < digitHeight; j++) {
        		digitList.add(in.next());
        	}
        	number.add(DIGITS.get(digitList));
        }
        StringBuilder sb = new StringBuilder();
        number.forEach(sb::append);
        return Long.parseLong(sb.toString(), TOTAL_DIGITS);
        
	}

	private static void writeNumber(String numberBaseTotalDigits) {
		for (char ch : numberBaseTotalDigits.toCharArray()) {
			DIGITS_BACK.get(ch).forEach(System.out::println);
		}
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


