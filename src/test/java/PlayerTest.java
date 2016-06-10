import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
		assertThat(new Player.ReasonableLocationsGenerator(newMap(map), 7, 7).getReasonableLocation())
				.contains(new Player.Node(6, 0), new Player.Node(5, 0));
	}

	protected Player.BombMap newMap(List<String> map) {
		return new Player.BombMap(map);
	}
}

