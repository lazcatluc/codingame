import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void rotatedRoomType2HasBottom() throws Exception {
		assertThat(new Player.Rotation.Builder().atX(2).atY(1).withRoomType(2).withRotation(1).build().getRoom()
				.getOut(Player.Direction.TOP)).isEqualTo(Optional.of(Player.Direction.BOTTOM));
	}

	@Test
	public void nonRotatedRoomType2HasNoBottom() throws Exception {
		assertThat(new Player.Rotation.Builder().atX(2).atY(1).withRoomType(2).build().getRoom()
				.getOut(Player.Direction.TOP)).isEmpty();
	}

	@Test
	public void buildsPathOfRotatedRoom() throws Exception {
		Map<Player.Location, Player.Rotation> map = new HashMap<>();
		Player.Rotation top = new Player.Rotation.Builder().atX(2).atY(0).withRoomType(-3).build();
		Player.Rotation rotatedMiddle = new Player.Rotation.Builder().atX(2).atY(1).withRoomType(2).withRotation(1)
				.build();
		Player.Rotation bottom = new Player.Rotation.Builder().atX(2).atY(2).withRoomType(-3).build();
		map.put(new Player.Location(2, 0), top);
		map.put(new Player.Location(2, 1), rotatedMiddle);
		map.put(new Player.Location(2, 2), bottom);

		Player.PathBuilder pathBuilder = new Player.PathBuilder(new Player.Location(2, 2), map,
				new Player.Location(2, 0), Player.Direction.TOP);
		assertThat(pathBuilder.isSolved()).isTrue();
	}

	@Test
	public void solvesPathWithOneRotation() throws Exception {
		Map<Player.Location, Player.Rotation> rotations = new HashMap<>();
		Player.Rotation.Builder x2 = new Player.Rotation.Builder().atX(2);
		Player.Rotation top = x2.atY(0).withRoomType(-3).build();
		Player.Rotation middle = x2.atY(1).withRoomType(2).build();
		Player.Rotation bottom = x2.atY(2).withRoomType(-3).build();
		rotations.put(new Player.Location(2, 0), top);
		rotations.put(new Player.Location(2, 1), middle);
		rotations.put(new Player.Location(2, 2), bottom);

		Player.Direction initialDirection = Player.Direction.TOP;
		Player.Location initialLocation = new Player.Location(2, 0);
		Player.IndyState initialState = new Player.IndyState.Builder().withRooms(rotations).startingAt(initialLocation)
				.going(initialDirection).exitAt(new Player.Location(2, 2)).build();
		assertThat(initialState.score()).isGreaterThan(0);
		initialState.expand();
		assertThat(initialState.score()).isEqualTo(0);
	}

	@Test
	public void solvesBrokenSewer() throws Exception {
		int[][] input = { { 0, -3, 0, 0, 0, 0, 0, 0 }, { 0, 12, 3, 3, 2, 3, 12, 0 }, { 0, 0, 0, 0, 0, 0, 2, 0 },
				{ 0, -12, 3, 2, 2, 3, 13, 0 } };
		Map<Player.Location, Player.Rotation> rotations = buildRotations(input);

		Player.Location exit = new Player.Location(1, 3);
		Player.Direction initialDirection = Player.Direction.TOP;
		Player.Location initialLocation = new Player.Location(1, 0);
		Player.IndyState initialState = new Player.IndyState.Builder().withRooms(rotations).startingAt(initialLocation)
				.going(initialDirection).exitAt(exit).build();
		int rounds = 0;
		while (initialState.score() > 0 && rounds++ < 100) {
			initialState.expand();
		}

		assertThat(initialState.score()).isEqualTo(0);
	}

	protected Map<Player.Location, Player.Rotation> buildRotations(int[][] input) {
		Map<Player.Location, Player.Rotation> rotations = new HashMap<>();
		for (int y = 0; y < input.length; y++) {
			for (int x = 0; x < input[y].length; x++) {
				rotations.put(new Player.Location(x, y),
						new Player.Rotation.Builder().atX(x).atY(y).withRoomType(input[y][x]).build());
			}
		}
		return rotations;
	}

	@Test
	public void solvesBrokenMausoleum() throws Exception {
		int[][] input = { 
				{ -3, 12, 8, 6, 3, 2, 7, 2, 7, 0, 0, 0, 0 }, 
				{ 11, 5, 13, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0 },
				{ 0, 11, 2, 2, 3, 3, 8, 2, -9, 2, 3, 13, 0 }, 
				{ 0, 0, 0, 0, 0, 12, 8, 3, 1, 3, 2, 7, 0 },
				{ 0, 0, 11, 2, 3, 1, 5, 2, 10, 0, 0, 11, 13 }, 
				{ 0, 0, 3, 0, 0, 6, 8, 0, 0, 0, 0, 0, 2 },
				{ 0, 0, 11, 3, 3, 10, 11, 2, 3, 2, 3, 2, 8 }, 
				{ 0, 12, 6, 3, 2, 3, 3, 6, 3, 3, 2, 3, 12 },
				{ 0, 11, 4, 2, 3, 2, 2, 11, 12, 13, 13, 13, 0 }, 
				{ 0, 0, -3, 12, 7, 8, 13, 13, 4, 5, 4, 10, 0 } };
		Map<Player.Location, Player.Rotation> rotations = buildRotations(input);
		Player.Location exit = new Player.Location(2, 9);
		Player.Direction initialDirection = Player.Direction.TOP;
		Player.Location initialLocation = new Player.Location(0, 0);
		Player.AllRotationsSolver solver = new Player.AllRotationsSolver.Builder().withExit(exit)
				.startingAt(initialLocation).going(initialDirection).withRotations(rotations).build();

		assertThat(solver.getActionsToExit()).isNotEmpty();
		System.out.println(solver.getActionsToExit());
	}
	
	@Test
	public void solvesRocks() throws Exception {
		int[][] input ={
				{0,	0,	0,	0,	0,	0,	0,	0,	-3,	0},
				{0,	7,	-2,	3,	-2,	3,	-2,	3,	11,	0},
				{0,	-7,	-2,	2,	2,	2,	2,	2,	2,	-2},
				{0,	6,	-2,	2,	2,	2,	2,	2,	2,	-2},
				{0,	-7,	-2,	2,	2,	2,	2,	2,	2,	-2},
				{0,	8,	-2,	2,	2,	2,	2,	2,	2,	-2},
				{0,	-7,	-2,	2,	2,	2,	2,	2,	2,	-2},
				{0,	-3,	0,	0,	0,	0,	0,	0,	0,	0}};
		Map<Player.Location, Player.Rotation> rotations = buildRotations(input);
		Player.Location exit = new Player.Location(1, 7);
		Player.Direction initialDirection = Player.Direction.TOP;
		Player.Location initialLocation = new Player.Location(8, 1);
		Player.AllRotationsSolver solver = new Player.AllRotationsSolver.Builder().withExit(exit)
				.startingAt(initialLocation).going(initialDirection).withRotations(rotations).build();

		assertThat(solver.getActionsToExit()).isNotEmpty();
		List<Player.Action> actions = solver.getActionsToExit().iterator().next();
		for (Player.Action action : actions) {
			rotations = action.act(rotations);
		}
		System.out.println(actions);
		System.out.println(new Player.PathBuilder(exit, rotations, initialLocation, initialDirection).getPath());
		System.out.println(new Player.PathBuilder(exit, rotations, new Player.Location(9, 2), Player.Direction.RIGHT).getPath());
	}

}
