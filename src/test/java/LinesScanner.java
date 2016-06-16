import java.util.ArrayList;
import java.util.List;

public class LinesScanner implements Player.MyCustomScanner {
	
	private final List<String> input;
	private int line = 0;

	public LinesScanner(List<String> input) {
		this.input = new ArrayList<>(input);
	}

	@Override
	public String nextLine() {
		return input.get(line++);
	}

	@Override
	public int nextInt() {
		return Integer.parseInt(nextLine());
	}

}
