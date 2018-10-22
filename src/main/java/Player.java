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
        System.err.println(in);
    }

    static class Piece {
        enum Color {
            WHITE,
            BLACK
        }

        private final Color color;
        private final char x;
        private final int y;

        public Piece(String line) {
            color = line.charAt(0) == '0' ? Color.WHITE : Color.BLACK;
            x = line.charAt(2);
            y = line.charAt(3) - '0';
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Piece{");
            sb.append("color=").append(color);
            sb.append(", x=").append(x);
            sb.append(", y=").append(y);
            sb.append('}');
            return sb.toString();
        }
    }

    interface MyScanner {
        int nextInt();
        String nextLine();
        String next();
    }

    static class MyCustomScanner implements MyScanner {
        private final List<String> lines = new ArrayList<>();
        private final Scanner scanner;

        MyCustomScanner(Scanner scanner) {
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
