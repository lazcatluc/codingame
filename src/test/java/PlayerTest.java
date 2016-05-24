
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class PlayerTest {
	private final Player.CardComparator cc = new Player.CardComparator();
	@Test
	public void lowCardLessThanHighCard() throws Exception {
		assertThat(cc.compare(Player.getCard("10H"), Player.getCard("AS"))).isLessThan(0);
	}
	
	@Test
	public void highCardGreaterThanLowCard() throws Exception {
		assertThat(cc.compare(Player.getCard("KD"), Player.getCard("2C"))).isGreaterThan(0);
	}
	
	@Test
	public void lowCardLessThanLowCard() throws Exception {
		assertThat(cc.compare(Player.getCard("9H"), Player.getCard("10C"))).isLessThan(0);
	}
	
	@Test
	public void lowCardGreaterThanLowCard() throws Exception {
		assertThat(cc.compare(Player.getCard("9H"), Player.getCard("2D"))).isGreaterThan(0);
	}
	
	@Test
	public void highCardGreaterThanHighCard() throws Exception {
		assertThat(cc.compare(Player.getCard("KD"), Player.getCard("JC"))).isGreaterThan(0);
	}
	
	@Test
	public void lowCardsEqual() throws Exception {
		assertThat(cc.compare(Player.getCard("9H"), Player.getCard("9D"))).isEqualTo(0);
	}
	
	@Test
	public void highCardsEqual() throws Exception {
		assertThat(cc.compare(Player.getCard("QS"), Player.getCard("QS"))).isEqualTo(0);
	}
	
	@Test
	public void player1WinsFastTest() throws Exception {
		assertThat(new Player.Battle(Arrays.asList(Player.getCard("10D")), 
							Arrays.asList(Player.getCard("9D"))).getResult().toString()).isEqualTo("1 1");
	}
	
	@Test
	public void fastPat() throws Exception {
		assertThat(new Player.Battle(Arrays.asList(Player.getCard("10D")), 
							Arrays.asList(Player.getCard("10C"))).getResult().toString()).isEqualTo("PAT");
	}
	
	@Test
	public void player2WinsFastTest() throws Exception {
		assertThat(new Player.Battle(Arrays.asList(Player.getCard("2D")), 
							Arrays.asList(Player.getCard("9D"))).getResult().toString()).isEqualTo("2 1");
	}
	
	@Test
	public void player1WinsTwoRoundsTest() throws Exception {
		assertThat(new Player.Battle(Arrays.asList(Player.getCard("10D"), Player.getCard("KC")), 
							Arrays.asList(Player.getCard("9D"), Player.getCard("2D"))).getResult().toString())
				.isEqualTo("1 2");
	}
	
	@Test
	public void complexPat() throws Exception {
		assertThat(new Player.Battle(Arrays.asList(Player.getCard("10D"), Player.getCard("9S"), Player.getCard("8D"), Player.getCard("KH"), Player.getCard("7D"), Player.getCard("5H"), Player.getCard("6S")), 
							Arrays.asList(Player.getCard("10H"), Player.getCard("7H"), Player.getCard("5C"), Player.getCard("QC"), Player.getCard("2C"), Player.getCard("4H"), Player.getCard("6D")))
				.getResult().toString()).isEqualTo("PAT");
	}
	
	@Test
	public void complexPlayer1Wins() throws Exception {
		assertThat(new Player.Battle(Arrays.asList(Player.getCard("10D"), Player.getCard("9S"), Player.getCard("8D"), Player.getCard("KH"), Player.getCard("7D"), Player.getCard("5H"), Player.getCard("7S")), 
							Arrays.asList(Player.getCard("10H"), Player.getCard("7H"), Player.getCard("5C"), Player.getCard("QC"), Player.getCard("2C"), Player.getCard("4H"), Player.getCard("6D")))
				.getResult().toString()).isEqualTo("1 3");
	}
}
