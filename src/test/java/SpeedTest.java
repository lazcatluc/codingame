import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

public class SpeedTest {
    @Test
    public void translatesSpeedOfSound() {
        Player.Speed sound = new Player.Speed(1234);
        assertThat(sound.meters(1)).isCloseTo(343.0, Offset.offset(1.0));
    }
}
