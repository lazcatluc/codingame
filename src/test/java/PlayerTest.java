import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void location11has8Neighbors() throws Exception {
		assertThat(new Player.Location(1, 1).getNeighbors().size()).isEqualTo(8);
	}

	@Test
	public void location01Has5Neighbors() throws Exception {
		assertThat(new Player.Location(0, 1).getNeighbors().size()).isEqualTo(5);
	}

	@Test
	public void distanceFrom00To99Is9() throws Exception {
		assertThat(new Player.Location(0, 0).distanceTo(new Player.Location(9, 9))).isEqualTo(9);
	}

	@Test
	public void moveFrom00Towards99Is11() throws Exception {
		assertThat(new Player.Location(0, 0).moveTowards(new Player.Location(9, 9)).getValue())
				.isEqualTo(new Player.Location(1, 1));
	}

	@Test
	public void moveFrom00Towards52IsHorizontalFirst() throws Exception {
		assertThat(new Player.Location(0, 0).moveTowards(new Player.Location(5, 2)).getValue())
				.isEqualTo(new Player.Location(1, 0));
	}

	@Test
	public void moveFrom52Towards00IsHorizontalFirst() throws Exception {
		assertThat(new Player.Location(5, 2).moveTowards(new Player.Location(0, 0)).getValue())
				.isEqualTo(new Player.Location(4, 2));
	}

	@Test
	public void moveTowardsNeighborIsNeighbor() throws Exception {
		new Player.Location(1, 1).getNeighbors()
				.forEach((action, neighbor) -> assertThat(new Player.Location(1, 1).moveTowards(neighbor).getValue())
						.isEqualTo(neighbor));
	}

	@Test
	public void neighborGiantIsStrikeable() throws Exception {
		assertThat(new Player.ThorGiantsState(Arrays.asList(new Player.Location(1, 1), new Player.Location(8, 13)),
				new Player.Location(0, 0), 1).getStrikableGiants())
						.isEqualTo(Collections.singleton(new Player.Location(1, 1)));
	}

	@Test
	public void solves1Giant() throws Exception {
		List<String> input = Arrays.asList("30", "11", "10", "1", "8", "5");
		solve(input);
	}

	@Test
	public void moveTowards() throws Exception {
		System.out.println(new Player.Location(30, 11).moveTowards(new Player.Location(8, 5)));
	}

	@Test
	public void solves2Giants() throws Exception {
		List<String> input = Arrays.asList("20", "9", "10", "2", "8", "5", "39", "17");
		solve(input);
	}

	@Test
	public void solves2GiantsOneStrike() throws Exception {
		List<String> input = Arrays.asList("22", "11", "1", "2", "7", "11", "26", "11");
		solve(input);
	}

	@Test
	public void solves15Giants4Strikes() throws Exception {
		List<String> input = Arrays.asList("20", "9", "4", "15", "5", "9", "2", "17", "0", "0", "24", "9", "38", "7",
				"20", "12", "28", "2", "3", "8", "12", "11", "11", "11", "32", "4", "31", "17", "39", "8", "10", "4",
				"8", "15");
		solve(input);
	}

	protected void solve(List<String> input) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		Player.MyCustomScanner in = new LinesScanner(input);
		Player.solve(out, in);
		System.out.println(baos.toString());
	}

}
