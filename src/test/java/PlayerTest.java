import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class PlayerTest {
	
	@Test
	public void generatesReachable() throws Exception {

		int width = 35;
		int floors = 12;
		int exitFloor = 11;
		int exitPosition = 12;
		List<String> elevatorList = Arrays.asList("2:34", "5:34", "4:9", "8:23", "0:34", "4:23", "8:1", "10:3", "6:34",
				"3:17", "4:34", "5:4", "11:13", "7:34", "9:34", "11:11", "1:34", "7:17", "6:13", "1:4", "2:24", "8:9",
				"1:17", "11:4", "6:22", "1:24", "10:23", "3:34", "9:17", "2:3", "8:34", "2:23", "10:34", "9:2");
		Map<Integer, List<Integer>> elevators = new HashMap<>();
		elevatorList.stream().map(s -> s.split(":")).forEach(ss -> 
			Player.addElevator(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), elevators));

		Player.Reachable myReachable = new Player.Reachable(floors, width, exitFloor, exitPosition, elevators, 
				Collections.emptyMap(), 4, 67, 6);
		myReachable.generateReachable();
		myReachable.generateReachable();
		int[][] reachable = myReachable.getReachable();
		String expectedReachablePicture = 
				"0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	\n"+
				"0	0	0	0	0	0	0	0	0	0	0	0	1	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	0	\n"+
				"0	0	0	0	12	11	10	9	8	7	6	5	4	5	6	7	8	9	10	11	12	13	14	0	0	0	0	0	0	0	0	0	0	0	0	\n"+
				"0	0	0	26	25	24	23	22	21	20	19	18	17	16	15	14	13	10	11	12	13	14	15	16	17	18	19	20	21	22	23	24	25	26	0	\n"+
				"0	0	28	27	26	25	24	23	22	21	24	25	26	27	28	27	26	25	24	23	22	21	20	17	18	19	20	21	22	23	24	25	26	27	0	\n"+
				"0	42	41	40	39	38	37	36	35	34	33	32	31	30	29	28	27	26	29	30	31	32	33	34	35	36	37	38	39	40	41	42	43	44	0	\n"+
				"0	43	42	41	40	39	38	37	36	35	34	33	32	31	34	35	36	37	38	39	40	37	34	35	36	37	38	39	40	41	42	43	44	45	0	\n"+
				"0	44	43	42	41	44	45	46	47	48	49	50	51	52	53	54	55	56	57	58	59	60	61	62	63	64	65	66	67	68	69	70	71	72	0	\n"+
				"0	59	58	57	56	55	54	53	52	49	50	51	52	53	54	55	56	57	58	59	60	61	66	63	64	65	66	67	68	69	70	71	72	73	0	\n"+
				"0	76	75	74	73	72	71	70	69	68	67	66	65	64	63	62	61	58	59	60	61	62	63	64	65	66	67	68	69	70	71	72	73	74	0	\n"+
				"0	77	76	75	78	79	80	81	82	81	80	79	78	77	76	75	74	73	72	71	70	69	68	65	66	67	68	69	70	71	72	73	74	75	0	\n"+
				"0	84	83	82	79	80	81	82	83	82	81	80	79	78	77	76	75	74	75	74	73	72	71	70	67	68	69	70	71	72	73	74	75	76	0	\n"+
				"0	95	94	93	92	91	90	89	88	87	86	85	84	83	82	81	80	79	78	77	76	75	74	73	70	71	72	73	74	75	76	77	78	79	0	\n";
		assertThat(print(reachable)).isEqualTo(expectedReachablePicture);

		System.out.println(myReachable.getActions(6));
	}

	private String print(int[][] reachable) {
		StringBuilder sb = new StringBuilder();
		for (int i = reachable.length - 1; i >= 0; i--) {
			print(reachable[i], sb);
		}
		return sb.toString();
	}

	private void print(int[] reachableRow, StringBuilder sb) {
		for (int i = 0; i < reachableRow.length; i++) {
			sb.append(reachableRow[i]);
			sb.append('\t');
		}
		sb.append('\n');
	}
}

