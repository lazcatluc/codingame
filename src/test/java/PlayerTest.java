
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

public class PlayerTest {
	@Test
	public void solvesExample() throws Exception {
		Player.Location min = new Player.Location(0, 0);
		Player.Location max = new Player.Location(9, 9);
		Player.Location current = new Player.Location(2, 5);

		Player.Solver solver = new Player.Solver(min, max, current);
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.colder();
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.same();
		System.out.println(solver);
	}

	@Test
	public void movesCloser() throws Exception {
		Player.Location min = new Player.Location(0, 0);
		Player.Location max = new Player.Location(4, 15);
		Player.Location current = new Player.Location(1, 5);

		Player.Solver solver = new Player.Solver(min, max, current);
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.warmer();
		System.out.println(solver);
		solver = solver.colder();
		System.out.println(solver);
		solver = solver.colder();
		System.out.println(solver);
	}
}
