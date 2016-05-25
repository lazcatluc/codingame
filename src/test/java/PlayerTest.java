
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void buildsFlatSurface() throws Exception {
		Player.FlatSurface.Builder builder = new Player.FlatSurface.Builder();
		Player.FlatSurface surface = builder.usingPoint(0, 10).usingPoint(10, 5).usingPoint(12, 5).usingPoint(20, 7).build();
		
		assertThat(surface.getDistanceFrom(15)).isEqualTo(-3);
		assertThat(surface.getDistanceFrom(9)).isEqualTo(1);
		assertThat(surface.getDistanceFrom(10)).isEqualTo(0);
	}
}

