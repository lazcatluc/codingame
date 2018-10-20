import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        MyScanner in = new MyCustomScanner(new Scanner(System.in));
        PrintStream out = System.out;
        run(in, out);
    }

    static void run(MyScanner in, PrintStream out) {
        for (int i = 0; i < 10; i++) {
            String line = in.next();
        }
        int robotCount = in.nextInt();
        for (int i = 0; i < robotCount; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            String direction = in.next();
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        out.println("14 4 L");
        System.err.println(in);
    }

    interface MyScanner {
        int nextInt();
        String nextLine();
        String next();
    }

    static class MyCustomScanner implements MyScanner {
        private final List<String> lines = new ArrayList<>();
        private final Scanner scanner;

        public MyCustomScanner(Scanner scanner) {
            this.scanner = scanner;
        }

        public int nextInt() {
            int nextInt = scanner.nextInt();
            lines.add(String.valueOf(nextInt));
            return nextInt;
        }

        public String nextLine() {
            String nextLine = scanner.nextLine();
            lines.add(nextLine);
            return nextLine;
        }

        public String next() {
            String next = scanner.next();
            lines.add(next);
            return next;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            lines.forEach(line -> sb.append(line).append('\n'));
            return sb.toString();
        }
    }

}
