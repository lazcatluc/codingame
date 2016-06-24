import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {
	
	@Test
	public void location11has8Neighbors() throws Exception {
		assertThat(new Player.Location(1, 1).getNeighbors().size()).isEqualTo(8);
	}
	
	@Test
	public void location01Has5Neighbors() throws Exception {
		assertThat(new Player.Location(0, 1).getNeighbors().size()).isEqualTo(5);
	}
}


