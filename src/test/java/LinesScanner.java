import java.util.List;

/**
 * Created by Catalin on 7/2/2016.
 */
public class LinesScanner implements Solution.MyScanner {
    private final List<String> lines;
    private int line = 0;

    public LinesScanner(List<String> lines) {
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
