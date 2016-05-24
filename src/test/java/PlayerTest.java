
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PlayerTest {
	private final Player.CardComparator cc = new Player.CardComparator();
	@Test
	public void lowCardLessThanHighCard() throws Exception {
		assertThat(cc.compare(Player.get("10H"), Player.get("AS"))).isLessThan(0);
	}
	
	@Test
	public void highCardGreaterThanLowCard() throws Exception {
		assertThat(cc.compare(Player.get("KD"), Player.get("2C"))).isGreaterThan(0);
	}
	
	@Test
	public void lowCardLessThanLowCard() throws Exception {
		assertThat(cc.compare(Player.get("9H"), Player.get("10C"))).isLessThan(0);
	}
	
	@Test
	public void lowCardGreaterThanLowCard() throws Exception {
		assertThat(cc.compare(Player.get("9H"), Player.get("2D"))).isGreaterThan(0);
	}
	
	@Test
	public void highCardGreaterThanHighCard() throws Exception {
		assertThat(cc.compare(Player.get("KD"), Player.get("JC"))).isGreaterThan(0);
	}
	
	@Test
	public void lowCardsEqual() throws Exception {
		assertThat(cc.compare(Player.get("9H"), Player.get("9D"))).isEqualTo(0);
	}
	
	@Test
	public void highCardsEqual() throws Exception {
		assertThat(cc.compare(Player.get("QS"), Player.get("QS"))).isEqualTo(0);
	}
}
