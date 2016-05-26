
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void transformsSingleNumberInto1AndNumber() throws Exception {
		assertThat(Player.transform(Arrays.asList(5))).isEqualTo(Arrays.asList(1, 5));
	}
	
	@Test
	public void transformsSingleNumberTwoTimesInto2AndNumber() throws Exception {
		assertThat(Player.transform(Arrays.asList(1, 1))).isEqualTo(Arrays.asList(2, 1));
	}
	
	@Test
	public void transformsTwoNumbersTwoTimesIntoEach1AndNumber() throws Exception {
		assertThat(Player.transform(Arrays.asList(2, 1))).isEqualTo(Arrays.asList(1, 2, 1, 1));
	}
}

