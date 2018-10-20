import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
    private static final int LENGTH = 10;
    private static final int WIDTH = 19;

    public static void main(String args[]) {
        MyScanner in = new MyCustomScanner(new Scanner(System.in));
        PrintStream out = System.out;
        run(in, out);
    }

    static void run(MyScanner in, PrintStream out) {
        Game game = init(in);
        out.println("14 4 L");
        System.err.println(in);
    }

    static Game init(MyScanner in) {
        Board board = new Board(LENGTH, WIDTH);
        IntStream.range(0, LENGTH).forEach(i -> board.readLine(i, in.next()));
        Set<Robot> robots = IntStream.range(0, in.nextInt()).mapToObj(i ->
                new Robot(board, new Node(in.nextInt(), in.nextInt(), in.next().charAt(0)))).collect(Collectors.toSet());
        return new Game(board, robots);
    }

    static class Game {
        private final Board board;
        private final Set<Robot> robots;

        Game(Board board, Set<Robot> robots) {
            this.board = board;
            this.robots = robots;
        }

        Set<Robot> getRobots() {
            return Collections.unmodifiableSet(robots);
        }
    }

    static class Robot {
        private final Board board;
        private final Node initialPosition;

        Robot(Board board, Node initialPosition) {
            this.board = board;
            this.initialPosition = initialPosition;
        }

        Set<Node> getPath() {
            Set<Node> path = new LinkedHashSet<>();
            Node current = initialPosition;
            while (!board.isDead(current) && !path.contains(current)) {
                path.add(current);
                current = board.getNextNode(current);
            }
            return path;
        }
    }

    static class Board {;
        private final int length;
        private final int width;
        private final char[][] board;
        private final Map<Character, IntUnaryOperator> xModifier;
        private final Map<Character, IntUnaryOperator> yModifier;

        Board(int length, int width) {
            this.length = length;
            this.width = width;
            board = new char[width][length];
            xModifier = new HashMap<>();
            xModifier.put('U', i -> i);
            xModifier.put('R', i -> (i + 1) % length);
            xModifier.put('D', i -> i);
            xModifier.put('L', i -> (i + length - 1) % length);
            yModifier = new HashMap<>();
            yModifier.put('U', i -> (i + width - 1) % width);
            yModifier.put('R', i -> i);
            yModifier.put('D', i -> (i + 1) % width);
            yModifier.put('L', i -> i);
        }

        void readLine(int line, String string) {
            System.arraycopy(string.toCharArray(), 0, board[line], 0, length);
        }

        Node getNextNode(Node origin) {
            char direction = origin.getDirection();
            if (xModifier.keySet().contains(board[origin.getY()][origin.getX()])) {
                direction = board[origin.getY()][origin.getX()];
            }
            return new Node(xModifier.get(direction).applyAsInt(origin.getX()),
                    yModifier.get(direction).applyAsInt(origin.getY()), direction);
        }

        boolean isDead(Node node) {
            return board[node.getY()][node.getX()] == '#';
        }
    }

    static class Node {
        private final int x;
        private final int y;
        private final char direction;

        Node(int x, int y, char direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return x == node.x &&
                    y == node.y &&
                    direction == node.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, direction);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "x=" + x +
                    ", y=" + y +
                    ", direction='" + direction + '\'' +
                    '}';
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        char getDirection() {
            return direction;
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

        @Override
        public int nextInt() {
            int nextInt = scanner.nextInt();
            lines.add(String.valueOf(nextInt));
            return nextInt;
        }

        @Override
        public String nextLine() {
            String nextLine = scanner.nextLine();
            lines.add(nextLine);
            return nextLine;
        }

        @Override
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
