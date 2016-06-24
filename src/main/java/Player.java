import java.	io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) throws InterruptedException {
		run(new MyScanner(new Scanner(System.in)), System.out);
    }

	protected static void run(MyCustomScanner in, PrintStream out) throws InterruptedException {
		int TX = in.nextInt();
        int TY = in.nextInt();

        // game loop
        while (true) {
            int hammerStrikes = in.nextInt(); // the remaining number of hammer strikes.
            int giants = in.nextInt(); // the number of giants which are still present on the map.
            for (int i = 0; i < giants; i++) {
                int giantX = in.nextInt();
                int giantY = in.nextInt();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // The movement or action to be carried out: WAIT STRIKE N NE E SE S SW W or N
            out.println("WAIT");
        }
	}
	
	interface MyCustomScanner {
		String nextLine();
		int nextInt();
	}
	
	static class MyScanner implements MyCustomScanner {
		private final Scanner scanner;
		private final List<String> lines = new ArrayList<>();

		public MyScanner(Scanner scanner) {
			this.scanner = scanner;
		}

		public String nextLine() {
			String nextLine = scanner.nextLine();
			lines.add(nextLine);
			return nextLine;
		}

		public int nextInt() {
			int i = scanner.nextInt();
			lines.add(String.valueOf(i));
			return i;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			appendLine(0, sb);
			for(int i = 1; i < lines.size(); i++) {
				sb.append(",\n");
				appendLine(i, sb);
			}
			return sb.toString();
		}
		
		private void appendLine(int line, StringBuilder sb) {
			sb.append("\"").append(lines.get(line).replaceAll("\"", "\\\"")).append("\"");
		}
	}
	
}

