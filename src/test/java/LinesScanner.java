import java.util.List;

class LinesScanner implements Player.MyScanner {
    private final List<String> lines;
    private int line = 0;

    LinesScanner(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public int nextInt() {
        return Integer.parseInt(nextLine());
    }

    @Override
    public String nextLine() {
        return lines.get(line++);
    }

    @Override
    public String next() {
        return nextLine();
    }
}
