
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.*;

import org.junit.After;
import org.junit.Test;

public class PlayerTest {

	@Test
	public void gattaOverlap() throws Exception {
		assertThat(new Player.OverlapperCache().get("AGATTA", "GATTACA").getOverlapCount()).isEqualTo(5);

	}

	@Test
	public void generatesPermutations() throws Exception {
		Set<String> set = new HashSet<>();
		set.add("1");
		set.add("2");
		set.add("3");
		Set<List<String>> permutations = new HashSet<>();
		permutations.add(Arrays.asList("1", "2", "3"));
		permutations.add(Arrays.asList("1", "3", "2"));
		permutations.add(Arrays.asList("2", "1", "3"));
		permutations.add(Arrays.asList("2", "3", "1"));
		permutations.add(Arrays.asList("3", "1", "2"));
		permutations.add(Arrays.asList("3", "2", "1"));
		assertThat(Player.generatePermutations(set)).isEqualTo(permutations);

	}

	@Test
	public void getsSmallestSequence() throws Exception {
		Set<String> set = new HashSet<>();
		set.add("AGATTA");
		set.add("GATTACA");
		set.add("TACAGA");

		assertThat(Player.getSmallestSequenceLength(set)).isEqualTo(10);
	}

	@Test
	public void computesAggregatedOverlapCount() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("AGATTA");
		list.add("GATTACA");
		list.add("TACAGA");

		assertThat(Player.getOverlapCount(list, new Player.OverlapperCache())).isEqualTo(9);
	}
}
