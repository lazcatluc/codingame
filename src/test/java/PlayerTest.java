import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class PlayerTest {
	
	@Test
	public void generatesAffectedNodes() throws Exception {
		List<String> map = Arrays.asList(
				"...@...",
				"...#...",
				".......",
				".....@@",
				".......",
				".......",
				".......");
		assertThat(new Player.BombMap(map).getAffectedNodes(new Player.Node(3,3))).isEqualTo(
				new HashSet<>(Arrays.asList(new Player.Node(5,3), new Player.Node(6,3))));
		assertThat(new Player.BombMap(map).getAffectedNodes(new Player.Node(0,0))).isEqualTo(
				Collections.singleton(new Player.Node(3,0)));
	}
}

