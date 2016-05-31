
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

public class PlayerTest {
	private String in = 
			"...0......\n"+
			"...00.....\n"+
			"...0..0...\n"+
			"...0......\n";
	private Player.Runway newRunway() {
		return new Player.Runway(new Scanner(new ByteArrayInputStream(in.getBytes())));
	}
	
	@Test
	public void zeroZeroIsClear() throws Exception {
		assertThat(newRunway().isClear(0, 0)).isTrue();
	}
	
	@Test
	public void zeroThreeIsNotClear() throws Exception {
		assertThat(newRunway().isClear(3, 0)).isFalse();
	}
	
	@Test
	public void outsideYIsClear() throws Exception {
		assertThat(newRunway().isClear(1000, 0)).isTrue();
	}
	
	@Test
	public void downFrom42IsClear() throws Exception {
		assertThat(newRunway().isClear(4, 2)).isTrue();
		assertThat(newRunway().isClear(6, 3)).isTrue();
		assertThat(newRunway().isClear(5, 2)).isTrue();
		assertThat(newRunway().isClear(5, 3)).isTrue();
		assertThat(newRunway().isPathClear(4, 2, 6, 3)).isTrue();
	}

}
