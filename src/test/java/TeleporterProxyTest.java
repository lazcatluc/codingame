import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import org.junit.Test;

public class TeleporterProxyTest {
	@Test
	public void initializesTeleporterUsingSecondTeleporter() throws Exception {
		Player.TeleporterProxy proxy = new Player.TeleporterProxy();
		Player.Location location01 = new Player.Location(0, 1);
		Player.Location location10 = new Player.Location(1, 0);
		Player.Teleporter teleporter01 = proxy.getTeleporter(location01);
		Player.Teleporter teleporter10 = proxy.getTeleporter(location10);
		
		assertThat(teleporter01.newLocation()).isEqualTo(location10);
		assertThat(teleporter10.newLocation()).isEqualTo(location01);
	}
}

