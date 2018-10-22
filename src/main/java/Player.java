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

    static class Box {
        private final int height;
        private final int width;
        private final int[] level;

        Box(int height, int width, List<String> lines) {
            this.height = height;
            this.width = width;
            this.level = new int[width];
            int currentLevel = height;
            for (String line : lines) {
                for (int i = 0; i < width; i++) {
                    char current = line.charAt(i);
                    if (current == '#' && level[i] == 0) {
                        level[i] = currentLevel;
                    }
                }
                currentLevel--;
            }
        }

        List<String> getLines() {
            List<String> lines = new ArrayList<>();
            for(int currentLevel = height; currentLevel > 0; currentLevel--) {
                StringBuilder lineBuilder = new StringBuilder();
                for (int i = 0; i < width; i++) {
                    if (level[i] >= currentLevel) {
                        lineBuilder.append('#');
                    } else {
                        lineBuilder.append('.');
                    }
                }
                lines.add(lineBuilder.toString());
            }
            return lines;
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
