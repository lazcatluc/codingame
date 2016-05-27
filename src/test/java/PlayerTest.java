
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void benderPathTest() throws Exception {
		List<String> map = Arrays.asList("######",
										 "#@E $#",
										 "# N  #",
										 "#X   #",
										 "######");
		Player.BenderPath benderPath = new Player.BenderPath(map);
		assertThat(benderPath.getPath()).isEqualTo("SOUTH\nEAST\nNORTH\nEAST\nEAST\n");
	}
	
	@Test
	public void breakerTest() throws Exception {
		List<String> map = Arrays.asList("######",
										 "#@ W$#",
										 "# BN #",
										 "#X   #",
										 "######");
		Player.BenderPath benderPath = new Player.BenderPath(map);
		assertThat(benderPath.getPath()).isEqualTo("SOUTH\nEAST\nEAST\nNORTH\nWEST\nWEST\nSOUTH\nSOUTH\nEAST\nEAST\nEAST\nNORTH\nNORTH\n");
	}
	
	@Test
	public void teleporterTest() throws Exception {
		List<String> map = Arrays.asList(
				 "######",
				 "#@T $#",
				 "# T  #",
				 "#X   #",
				 "######");
		Player.BenderPath benderPath = new Player.BenderPath(map);
		assertThat(benderPath.getPath()).isEqualTo("SOUTH\nEAST\nEAST\nEAST\n");
	}
	
	@Test
	public void breakerSimpleTest() throws Exception {
		List<String> map = Arrays.asList(
				 "###",
				 "#@#",
				 "#B#",
				 "#X#",
				 "#$#",
				 "###");
		Player.BenderPath benderPath = new Player.BenderPath(map);
		assertThat(benderPath.getPath()).isEqualTo("SOUTH\nSOUTH\nSOUTH\n");
	}
}
