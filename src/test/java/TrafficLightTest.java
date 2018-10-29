import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TrafficLightTest {
    @Test
    public void isGreenInitially() {
        assertThat(trafficLight().isGreen(0)).isTrue();
    }

    @Test
    public void turnsRedAfterFirstPeriodExpires() {
        assertThat(trafficLight().isGreen(1)).isFalse();
    }

    @Test
    public void turnsGreenAgainAfterSecondPeriodExpires() {
        assertThat(trafficLight().isGreen(2)).isTrue();
    }

    @Test
    public void fourKmPerHourPassesGreenLight() {
        assertThat(trafficLight().isGreen(new Player.Speed(4))).isTrue();
    }

    @Test
    public void threeKmPerHourGetsCaughtByRedLight() {
        assertThat(trafficLight().isGreen(new Player.Speed(3))).isFalse();
    }

    private Player.TrafficLight trafficLight() {
        return new Player.TrafficLight(1, 1);
    }
}
