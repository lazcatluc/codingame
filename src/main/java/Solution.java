import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        MyScanner in = new MyCustomScanner(new Scanner(System.in));
        PrintStream out = System.out;
        run(in, out);
    }

    static void run(MyScanner in, PrintStream out) {
        String[] firstLine = in.nextLine().split(" ");
        int width = Integer.parseInt(firstLine[0]);
        int height = Integer.parseInt(firstLine[1]);
        int tumbles = in.nextInt();
        in.nextLine();
        List<String> lines = IntStream.range(0, height).mapToObj(i -> in.nextLine()).collect(Collectors.toList());
        Box box = new Box(height, width, lines);
        for (int i = 0; i < tumbles; i++) {
            box = box.tumble();
        }
        box.getLines().forEach(out::println);
        System.err.println(in);
    }

    static class Box {
        private final int height;
        private final int width;
        private final int[] level;

        Box(int height, int width, int[] level) {
            this.height = height;
            this.width = width;
            this.level = new int[level.length];
            System.arraycopy(level, 0, this.level, 0, level.length);
        }

        Box(int height, int width, List<String> lines) {
            this.height = height;
            this.width = width;
            this.level = new int[width];
            int currentLevel = height;
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    char current = lines.get(j).charAt(i);
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

        Box tumble() {
            int[] newLevels = new int[level.length];
            for(int currentLevel = height; currentLevel > 0; currentLevel--) {
                for (int i = 0; i < width; i++) {
                    if (level[i] >= currentLevel) {
                        newLevels[height - currentLevel]++;
                    }
                }
            }
            return new Box(width, height, newLevels);
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
