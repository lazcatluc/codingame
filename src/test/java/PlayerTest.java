import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;

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
		assertThat(new Player.Location(0, 0).moveTowards(new Player.Location(9, 9)))
				.isEqualTo(new Player.Location(1, 1));
	}

	@Test
	public void moveTowardsNeighborIsNeighbor() throws Exception {
		new Player.Location(1, 1).getNeighbors()
				.forEach(neighbor -> assertThat(new Player.Location(1, 1).moveTowards(neighbor)).isEqualTo(neighbor));
	}

	@Test
	public void neighborGiantIsStrikeable() throws Exception {
		assertThat(new Player.ThorGiantsState(Arrays.asList(new Player.Location(1, 1), new Player.Location(8, 13)),
				new Player.Location(0, 0), 1).strikableGiants()).isEqualTo(Collections.singleton(new Player.Location(1, 1)));
	}
}
