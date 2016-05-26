
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void transformsSingleNumberInto1AndNumber() throws Exception {
		assertThat(Player.transform(Arrays.asList(5))).isEqualTo(Arrays.asList(1, 5));
	}
}

