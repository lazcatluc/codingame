import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
    private static final int LENGTH = 19;
    private static final int WIDTH = 10;
    private static final int MAX_OUTPUT_LENTGH = 9993;
    private static final long MILLISECONDS_ALLOWED = 950;

    public static void main(String args[]) {
        MyScanner in = new MyCustomScanner(new Scanner(System.in));
        PrintStream out = System.out;
        run(in, out);
    }

    static void run(MyScanner in, PrintStream out) {
        GameRound gameRound = init(in);
        System.err.println(in);
        StringBuilder outputBuilder = new StringBuilder();
        boolean first = true;
        long initialTime = System.currentTimeMillis();
        long time;
        do {
            Optional<GameRound> nextRound = gameRound.improveByAddingOneArrow();
            if (!nextRound.isPresent()) {
                break;
            }
            if (!first) {
                outputBuilder.append(" ");
            }
            first = false;
            outputBuilder.append(gameRound.getAddedArrow().getX()).append(" ")
                    .append(gameRound.getAddedArrow().getY()).append(" ")
                    .append(gameRound.getAddedArrow().getDirection());
            gameRound = nextRound.get();
            time = System.currentTimeMillis();
        } while (outputBuilder.length() <= MAX_OUTPUT_LENTGH && time - initialTime < MILLISECONDS_ALLOWED);
        out.println(outputBuilder.toString());
    }

    static GameRound init(MyScanner in) {
        Board board = new Board(LENGTH, WIDTH);
        IntStream.range(0, WIDTH).forEach(i -> board.readLine(i, in.next()));
        Set<Robot> robots = IntStream.range(0, in.nextInt()).mapToObj(i ->
                new Robot(board, new Node(in.nextInt(), in.nextInt(), in.next().charAt(0)))).collect(Collectors.toSet());
        return new GameRound(board, robots);
    }

    static class GameRound {
        private final Board board;
        private final List<Robot> robots;
        private final int score;
        private Node addedArrow;

        GameRound(Board board, Collection<Robot> robots) {
            this.board = board;
            this.robots = new ArrayList<>(robots);
            Collections.sort(this.robots, Comparator.comparingInt(robot -> robot.getPath().size()));
            this.score = this.robots.stream().map(Robot::getPath).mapToInt(List::size).sum();
        }

        List<Robot> getRobots() {
            return Collections.unmodifiableList(robots);
        }

        Optional<GameRound> improveByAddingOneArrow() {
            List<Node> expansionCandidates = robots.stream().map(Robot::lastEmptyOnPath)
                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            for (Node node : expansionCandidates) {
                Map<Node, Board> expanded = board.expand(node);
                Map<Node, GameRound> expandedRounds = new HashMap<>();
                expanded.forEach((myNode, board) -> expandedRounds.put(myNode, updateBoard(board)));
                Optional<Map.Entry<Node, GameRound>> newGameRound = expandedRounds.entrySet().stream()
                        .sorted((entry1, entry2) -> entry2.getValue().getScore() - entry1.getValue().getScore())
                        .findFirst();
                if (newGameRound.isPresent() && newGameRound.get().getValue().getScore() > this.getScore()) {
                    addedArrow = newGameRound.get().getKey();
                    return Optional.of(newGameRound.get().getValue());
                }
            }
            return Optional.empty();

        }

        GameRound updateBoard(Board newBoard) {
            return new GameRound(newBoard,
                robots.stream().map(robot -> new Robot(newBoard, robot.getInitialPosition()))
                        .collect(Collectors.toList()));
        }

        int getScore() {
            return score;
        }

        Board getBoard() {
            return board;
        }

        Node getAddedArrow() {
            return addedArrow;
        }
    }

    static class Robot {
        private final Board board;
        private final Node initialPosition;
        private List<Node> path;
        private Node lastEmptyOnPath;

        Robot(Board board, Node initialPosition) {
            this.board = board;
            this.initialPosition = initialPosition;
        }

        List<Node> getPath() {
            if (path == null) {
                path = new ArrayList<>();
                Set<Node> parsed = new HashSet<>();
                Node current = initialPosition;
                while (!board.isDead(current) && !parsed.contains(current)) {
                    parsed.add(current);
                    path.add(current);
                    if (board.isEmpty(current)) {
                        lastEmptyOnPath = current;
                    }
                    current = board.getNextNode(current);
                }
            }
            return path;
        }

        Optional<Node> lastEmptyOnPath() {
            return Optional.ofNullable(lastEmptyOnPath);
        }

        Node getInitialPosition() {
            return initialPosition;
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

        private Board(Board parent, Node addedArrow) {
            this.length = parent.length;
            this.width = parent.width;
            this.xModifier = parent.xModifier;
            this.yModifier = parent.yModifier;
            this.board = new char[width][length];
            for (int i = 0; i < width; i++) {
                if (i != addedArrow.getY()) {
                    this.board[i] = parent.board[i];
                }
            }
            this.board[addedArrow.getY()] = new char[length];
            System.arraycopy(parent.board[addedArrow.getY()], 0, this.board[addedArrow.getY()], 0, length);
            this.board[addedArrow.getY()][addedArrow.getX()] = addedArrow.getDirection();
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

        boolean isEmpty(Node node) {
            return board[node.getY()][node.getX()] == '.';
        }

        Map<Node, Board> expand(Node node) {
            return xModifier.keySet().stream().filter(direction -> node.getDirection() != direction)
                    .collect(Collectors.toMap(direction -> new Node(node.getX(), node.getY(), direction),
                            direction -> new Board(this, new Node(node.getX(), node.getY(), direction))));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            IntStream.range(0, width).forEach(i -> sb.append(new String(board[i])).append('\n'));
            return sb.toString();
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
