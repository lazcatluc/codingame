import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;
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
        String line = in.nextLine();
        Piece rook = new Rook("0 " + line);
        Board board = new Board(8,
                IntStream.range(0, in.nextInt() + 1)
                        .mapToObj(i -> in.nextLine())
                        .filter(s -> !s.isEmpty())
                        .map(Rook::new)
                        .collect(Collectors.toSet()));
        board.getMoves(rook).forEach(out::println);
        System.err.println(in);
    }

    static class Point implements Comparable<Point> {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        char getDisplayX() {
            return (char) (x + 'a');
        }

        char getDisplayY() {
            return (char) (y + '1');
        }

        @Override
        public int compareTo(Point o) {
            return Comparator.comparing(Point::getX).thenComparing(Point::getY).compare(this, o);
        }
    }

    static abstract class Piece {
        enum Color {
            WHITE,
            BLACK
        }

        private final Color color;
        private final Point point;

        Piece(String line) {
            color = line.charAt(0) == '0' ? Color.WHITE : Color.BLACK;
            point = new Point(line.charAt(2) - 'a', line.charAt(3) - '1');
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Piece{");
            sb.append("color=").append(color);
            sb.append(", x=").append(point.getDisplayX());
            sb.append(", y=").append(point.getDisplayY());
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Piece piece = (Piece) o;
            return color == piece.color &&
                    Objects.equals(point, piece.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(color, point);
        }

        Point getPoint() {
            return point;
        }

        Color getColor() {
            return color;
        }

        char getPieceNotation() {
            return getClass().getSimpleName().charAt(0);
        }

        abstract Set<UnaryOperator<Point>> getPaths();
    }

    static class Rook extends Piece {

        private final Set<UnaryOperator<Point>> paths;

        Rook(String line) {
            super(line);
            paths = new HashSet<>();
            paths.add(point -> new Point(point.getX() + 1, point.getY()));
            paths.add(point -> new Point(point.getX() - 1, point.getY()));
            paths.add(point -> new Point(point.getX(), point.getY() + 1));
            paths.add(point -> new Point(point.getX(), point.getY() - 1));
        }

        @Override
        Set<UnaryOperator<Point>> getPaths() {
            return paths;
        }
    }

    static class Move {
        private final Piece piece;
        private final Point point;
        private final char moveSeparator;

        Move(Piece piece, Point point, char moveSeparator) {
            this.piece = piece;
            this.point = point;
            this.moveSeparator = moveSeparator;
        }

        static Move simple(Piece piece, Point point) {
            return new Move(piece, point, '-');
        }

        static Move capture(Piece piece, Point point) {
            return new Move(piece, point, 'x');
        }

        @Override
        public String toString() {
            return new StringBuilder().append(piece.getPieceNotation()).append(piece.getPoint().getDisplayX())
                    .append(piece.getPoint().getDisplayY()).append(moveSeparator).append(point.getDisplayX())
                    .append(point.getDisplayY()).toString();
        }

        Point getPoint() {
            return point;
        }

        char getMoveSeparator() {
            return moveSeparator;
        }
    }

    static class Board {
        private final int size;
        private final Map<Point, Piece> pieces;

        Board(int size, Collection<? extends Piece> pieces) {
            this.size = size;
            this.pieces = pieces.stream().collect(Collectors.toMap(Piece::getPoint, p -> p));
        }

        boolean isOnBoard(Point point) {
            return isOnBoard(point.getX()) && isOnBoard(point.getY());
        }

        private boolean isOnBoard(int coord) {
            return coord >= 0 && coord < size;
        }

        List<Move> getMoves(Piece piece) {
            List<Move> moves = new ArrayList<>();
            piece.getPaths().forEach(path -> {
                Point point = path.apply(piece.getPoint());
                while (isOnBoard(point)) {
                    Piece existingPiece = pieces.get(point);
                    if (existingPiece != null) {
                        if (existingPiece.getColor() != piece.getColor()) {
                            moves.add(Move.capture(piece, point));
                        }
                        break;
                    }
                    moves.add(Move.simple(piece, point));
                    point = path.apply(point);
                }
            });
            moves.sort(Comparator.comparing(Move::getMoveSeparator).thenComparing(Move::getPoint));
            return moves;
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
