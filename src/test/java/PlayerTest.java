
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void findsPath() throws Exception {
		String input =
				"######\n"+
				"#....#\n"+
				"#.C@.#\n"+
				"#....#\n"+
				"######\n";
		Scanner in = new Scanner(new ByteArrayInputStream(input.getBytes()));
		Map<Player.Location, Character> map = Player.readMap(in, 5);
		
		Player.Location kirkLocation = new Player.Location(2,3);
		assertThat(map.get(kirkLocation)).isEqualTo('@');
		assertThat(map.get(kirkLocation.getNeighbor(Player.Direction.LEFT))).isEqualTo('C');
		assertThat(map.get(kirkLocation.getNeighbor(Player.Direction.UP))).isEqualTo('.');
		assertThat(map.get(kirkLocation.getNeighbor(Player.Direction.DOWN))).isEqualTo('.');
		assertThat(map.get(kirkLocation.getNeighbor(Player.Direction.RIGHT))).isEqualTo('.');
		
		Player.DirectSolver solver = new Player.DirectSolver(map, kirkLocation, 'C');
		solver.solve();
		
		assertThat(solver.solutionFound()).isTrue();
		assertThat(solver.getDirection()).isEqualTo(Player.Direction.LEFT);
	}
	
	@Test
	public void movesOnTopOfGoal() throws Exception {
		String input =
				"##############################\n"+
				"##.##....#.##.....##.###.....#\n"+
				"##....##......####.......###.#\n"+
				"#..##.##.#.##.####.##.##.###.#\n"+
				"##.##.....#........##C@......#\n"+
				"#.....###...###......###.#####\n"+
				"###.#.###.#.....##..........##\n"+
				"#.........###.###.##........##\n"+
				"?##.###.#............###.##..#\n"+
				"###.....###.###.#........##.##\n"+
				"#.########...##.##.##.#.#...##\n"+
				"#.............###..#.#......##\n"+
				"#.####.######.###.#######...##\n"+
				"###........T#.......##......#?\n"+
				"???#################??#######?\n";
		Scanner in = new Scanner(new ByteArrayInputStream(input.getBytes()));
		Map<Player.Location, Character> map = Player.readMap(in, 15);
		
		Player.Location kirkLocation = new Player.Location(4,22);
		Player.DirectSolver solver = new Player.DirectSolver(map, kirkLocation, 'C');
		solver.solve();
		assertThat(solver.solutionFound()).isTrue();
		assertThat(solver.getDirection()).isEqualTo(Player.Direction.LEFT);
	}
}
