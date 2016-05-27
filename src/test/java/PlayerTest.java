
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void smallDictionary() throws Exception {
		Player.Dictionary dictionary= new Player.Dictionary();
		dictionary.add("which");
		
		assertThat(dictionary.findBiggestWord("hicquwh")).isEqualTo("which");
	}
	
	@Test
	public void sortedLetters() throws Exception {
		assertThat(Player.sortedLetters("bcad")).isEqualTo("abcd");
	}
	
	@Test
	public void whichIsContainedInHicquwh() throws Exception {		
		assertThat(Player.matches("chhiquw","chhiw")).isTrue();
	}
	
	@Test
	public void potsieIsContainedInSopitez() throws Exception {
		assertThat(Player.matches(Player.sortedLetters("sopitez"),Player.sortedLetters("potsie"))).isTrue();
	}
	
	@Test
	public void backIsNotContainedInSopitez() throws Exception {
		assertThat(Player.matches(Player.sortedLetters("sopitez"),Player.sortedLetters("back"))).isFalse();
	}
	
}
