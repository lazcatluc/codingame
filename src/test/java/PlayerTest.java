import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class PlayerTest {
	private List<String> map = 
			Arrays.asList(
			"...@...",
			"...#...",
			".......",
			".....@@",
			".......",
			".......",
			".......");
	
	
	@Test
	public void generatesRelevantNodes() throws Exception {
		assertThat(new Player.RelevantNodeFinder(newMap(map)).getRelevantNodes())
			.isEqualTo(new HashSet<>(Arrays.asList(new Player.Node(3, 0), new Player.Node(5, 3), new Player.Node(6, 3))));
	}
	
	@Test
	public void generatesAffectedNodes33() throws Exception {
		assertThat(newMap(map).getAffectedNodes(new Player.Node(3, 3)))
				.isEqualTo(new HashSet<>(Arrays.asList(new Player.Node(5, 3), new Player.Node(6, 3))));

	}

	@Test
	public void generatesAffectedNodes00() throws Exception {
		assertThat(newMap(map).getAffectedNodes(new Player.Node(0, 0)))
				.isEqualTo(Collections.singleton(new Player.Node(3, 0)));
	}

	@Test
	public void generatesReasonableLocations() throws Exception {
		assertThat(new Player.ReasonableLocationsGenerator(newMap(map)).getReasonableLocation())
				.containsKeys(new Player.Node(6, 0), new Player.Node(5, 0));
	}
	
	@Test
	public void coversSetOptimally() throws Exception {
		Map<Player.Node, Set<Player.Node>> reasonableLocations = new LinkedHashMap<>();
		reasonableLocations.put(new Player.Node(6, 0), new HashSet<>(Arrays.asList(new Player.Node(3,0), new Player.Node(6, 3))));
		reasonableLocations.put(new Player.Node(3, 3), new HashSet<>(Arrays.asList(new Player.Node(6,3))));
		reasonableLocations.put(new Player.Node(5, 0), new HashSet<>(Arrays.asList(new Player.Node(3,0), new Player.Node(5, 3))));
		
		Player.BombMap bombMap = newMap(map);
		Set<Player.Node> allNodes = new HashSet<>(Arrays.asList(new Player.Node(6,3), new Player.Node(5, 3), new Player.Node(3,0)));
		assertThat(new HashSet<>(new Player.SetCoverGenerator(reasonableLocations , allNodes , 2, bombMap).getNodesCoveringAllNodes())).isEqualTo(
				new HashSet<>(Arrays.asList(new Player.Node(5, 0), new Player.Node(6, 0))));
		assertThat(new Player.SetCoverGenerator(reasonableLocations , allNodes , 1, bombMap).getNodesCoveringAllNodes()).isNull();
	}

	protected Player.BombMap newMap(List<String> map) {
		return new Player.BombMap(map);
	}
}

