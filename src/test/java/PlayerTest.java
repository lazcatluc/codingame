import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void fillsReachableMatrixFromStartingPoint() throws Exception {
		boolean[] topFloor = new boolean[] { false, false, false, true, false, false, false, false };
		boolean[] bottomFloor = new boolean[topFloor.length];
		boolean[][] reachable = new boolean[][] { bottomFloor, topFloor };

		List<Integer> elevators = Arrays.asList(1, 6);
		boolean[] expectedTopFloor = new boolean[] { false, false, true, true, true, true, false, false };
		boolean[] expectedBottomFloor = expectedTopFloor;

		assertThat(Player.findFirstReachablePoint(reachable, 1)).isEqualTo(3);
		assertThat(Player.findFirstNonReachableElevatorToTheLeft(reachable, 1, elevators, 3)).isEqualTo(1);
		Player.fillBasedOnElevators(reachable, 1, elevators);
		assertThat(Arrays.deepEquals(reachable, new boolean[][] { expectedBottomFloor, expectedTopFloor })).isTrue();
	}

	@Test
	public void generatesReachable() throws Exception {

		int width = 35;
		int floors = 12;
		int exitFloor = 11;
		int exitPosition = 12;
		List<String> elevatorList = Arrays.asList("2:34", "5:34", "4:9", "8:23", "0:34", "4:23", "8:1", "10:3", "6:34",
				"3:17", "4:34", "5:4", "11:13", "7:34", "9:34", "11:11", "1:34", "7:17", "6:13", "1:4", "2:24", "8:9",
				"1:17", "11:4", "6:22", "1:24", "10:23", "3:34", "9:17", "2:3", "8:34", "2:23", "10:34", "9:2", "0:6");
		Map<Integer, List<Integer>> elevators = new HashMap<>();
		elevatorList.stream().map(s -> s.split(":")).forEach(ss -> 
			Player.addElevator(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), elevators));

		boolean[][] reachable = Player.generateReachable(floors, width, exitFloor, exitPosition, elevators);
		String expectedReachablePicture = 
				"00000000000000000000000000000000000\n"+
				"0000e000000e1e000000000000000000000\n"+
				"000e1111111111111111111e0000000000e\n"+
				"00e11111111111111E1111111111111111e\n"+
				"0e1111111E1111111111111E1111111111e\n"+
				"01111111111111111E1111111111111111e\n"+
				"0111111111111E11111111E11111111111e\n"+
				"0111E11111111111111111111111111111e\n"+
				"011111111E1111111111111E1111111111e\n"+
				"01111111111111111E1111111111111111e\n"+
				"011E1111111111111111111EE111111111e\n"+
				"0111E111111111111E111111E111111111e\n"+
				"011111E111111111111111111111111111e\n";
		assertThat(print(reachable, elevators)).isEqualTo(expectedReachablePicture);

	}

	private String print(boolean[][] reachable, Map<Integer, List<Integer>> elevators) {
		StringBuilder sb = new StringBuilder();
		for (int i = reachable.length - 1; i >= 0; i--) {
			print(reachable[i], sb, elevators.getOrDefault(i, Collections.emptyList()));
		}
		return sb.toString();
	}

	private void print(boolean[] reachableRow, StringBuilder sb, List<Integer> elevators) {
		for (int i = 0; i < reachableRow.length; i++) {
			boolean elevator = elevators.contains(i);
			boolean reachable = reachableRow[i];
			if (elevator) {
				sb.append(reachable?'E':'e');
			}
			else {
				sb.append(reachable?'1':'0');
			}
		}
		sb.append('\n');
	}
}
