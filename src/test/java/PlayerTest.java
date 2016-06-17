import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {
	
	@Test
	public void oneNode() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> input = Arrays.asList("12",
											"9",
											"",
											"6",
											"1",
											"",
											"............",
											"............",
											"............",
											"............",
											".@..........",
											"............",
											"............",
											"............",
											"............",
											"5",
											"1",
											"",
											"............",
											"............",
											"............",
											"............",
											"..@.........",
											"............",
											"............",
											"............",
											"............",
											"4",
											"1",
											"",
											"............",
											"............",
											"............",
											"............",
											"...@........",
											"............",
											"............",
											"............",
											"............",
											"3",
											"1",
											"",
											"............",
											"............",
											"............",
											"............",
											"....@.......",
											"............",
											"............",
											"............",
											"............",
											"2",
											"1",
											"",
											"............",
											"............",
											"............",
											"............",
											".....@......",
											"............",
											"............",
											"............",
											"............",
											"1",
											"1",
											"",
											"............",
											"............",
											"............",
											"............",
											"......@.....",
											"............",
											"............",
											"............",
											"............");
		Player.run(new LinesScanner(input), new PrintStream(baos));
		assertThat(baos.toString()).startsWith("WAIT");
	}
	
	@Test
	public void fixedLocationGeneratesFixedTrajector() throws Exception {
		assertThat(Player.buildTrajectory(new ArrayList<>(), new Player.Location(0, 0), new Player.Location(0, 0)))
				.isEqualTo(Collections.singletonList(new Player.Location(0, 0)));
	}
	
	@Test
	public void movingToTheLeft() throws Exception {
		assertThat(Player.buildTrajectory(emptyMap(), new Player.Location(1, 0), new Player.Location(0, 0)))
			.startsWith(new Player.Location(1, 0), new Player.Location(0, 0));
	}
	
	@Test
	public void movingMoreToTheLeft() throws Exception {
		assertThat(Player.buildTrajectory(emptyMap(), new Player.Location(2, 0), new Player.Location(1, 0)))
			.startsWith(new Player.Location(2, 0), new Player.Location(1, 0), new Player.Location(0, 0));
	}
	
	@Test
	public void movingMoreUp() throws Exception {
		assertThat(Player.buildTrajectory(emptyMap(), new Player.Location(0, 2), new Player.Location(0, 1)))
			.startsWith(new Player.Location(0, 2), new Player.Location(0, 1), new Player.Location(0, 0));
	}
	
	@Test
	public void movingMoreUpWithBounce() throws Exception {
		assertThat(Player.buildTrajectory(emptyMap(), new Player.Location(0, 2), new Player.Location(0, 1)))
			.startsWith(new Player.Location(0, 2), new Player.Location(0, 1), new Player.Location(0, 0), new Player.Location(0, 1));
	}
	
	@Test
	public void movingUpFully() throws Exception {
		assertThat(Player.buildTrajectory(emptyMap(), new Player.Location(0, 2), new Player.Location(0, 1)))
				.isEqualTo(Arrays.asList(new Player.Location(0, 2), new Player.Location(0, 1),
						new Player.Location(0, 0), new Player.Location(0, 1), new Player.Location(0, 2),
						new Player.Location(0, 3), new Player.Location(0, 4), new Player.Location(0, 5),
						new Player.Location(0, 6), new Player.Location(0, 7), new Player.Location(0, 8),
						new Player.Location(0, 9), new Player.Location(0, 10), new Player.Location(0, 11),
						new Player.Location(0, 10), new Player.Location(0, 9), new Player.Location(0, 8),
						new Player.Location(0, 7), new Player.Location(0, 6), new Player.Location(0, 5),
						new Player.Location(0, 4), new Player.Location(0, 3)));
	}
	
	@Test
	public void getUpNeighbor() throws Exception {
		assertThat(new Player.Location(1, 1).moveTo(Player.Direction.UP)).isEqualTo(new Player.Location(1,  0));
	}
	
	@Test
	public void getDownNeighbor() throws Exception {
		assertThat(new Player.Location(1, 1).moveTo(Player.Direction.DOWN)).isEqualTo(new Player.Location(1,  2));
	}
	
	@Test
	public void getLeftNeighbor() throws Exception {
		assertThat(new Player.Location(1, 1).moveTo(Player.Direction.LEFT)).isEqualTo(new Player.Location(0,  1));
	}
	
	@Test
	public void getRightNeighbor() throws Exception {
		assertThat(new Player.Location(1, 1).moveTo(Player.Direction.RIGHT)).isEqualTo(new Player.Location(2,  1));
	}
	
	@Test
	public void zeroZeroOnTheMap() throws Exception {
		assertThat(new Player.Location(0, 0).isOnTheMapAndNotBlocked(emptyMap())).isTrue();
	}
	
	@Test
	public void zeroMinusNotOnTheMap() throws Exception {
		assertThat(new Player.Location(0, -1).isOnTheMapAndNotBlocked(emptyMap())).isFalse();
	}
	
	@Test
	public void minusZeroNotOnTheMap() throws Exception {
		assertThat(new Player.Location(-1, 0).isOnTheMapAndNotBlocked(emptyMap())).isFalse();
	}
	
	@Test
	public void zero100NotOnTheMap() throws Exception {
		assertThat(new Player.Location(0, 100).isOnTheMapAndNotBlocked(emptyMap())).isFalse();
	}
	
	@Test
	public void hundredZeroNotOnTheMap() throws Exception {
		assertThat(new Player.Location(100, 0).isOnTheMapAndNotBlocked(emptyMap())).isFalse();
	}

	protected List<String> emptyMap() {
		String lane = "............";
		List<String> map = Collections.nCopies(lane.length(), lane);
		return map;
	}
	
	@Test
	public void goesUp() throws Exception {
		assertThat(Player.getDirectionFromTo(new Player.Location(1, 1) , new Player.Location(1, 0))).isEqualTo(Player.Direction.UP);
	}
	
	@Test
	public void goesDown() throws Exception {
		assertThat(Player.getDirectionFromTo(new Player.Location(1, 1) , new Player.Location(1, 2))).isEqualTo(Player.Direction.DOWN);
	}
	
	@Test
	public void goesRight() throws Exception {
		assertThat(Player.getDirectionFromTo(new Player.Location(1, 1) , new Player.Location(2, 1))).isEqualTo(Player.Direction.RIGHT);
	}
	
	@Test
	public void goesLeft() throws Exception {
		assertThat(Player.getDirectionFromTo(new Player.Location(1, 1) , new Player.Location(0, 1))).isEqualTo(Player.Direction.LEFT);
	}
	
	@Test
	public void getsNeighbors() throws Exception {
		assertThat(new Player.Location(1, 1).getNeighbors()).isEqualTo(new HashSet<>(Arrays.asList(new Player.Location(1, 0),
				new Player.Location(1, 2), new Player.Location(2, 1), new Player.Location(0, 1))));
	}
	
	@Test
	public void buildsNodeConnectingNeighbors() throws Exception {
		assertThat(Player.Node.getNodes(emptyMap(), Collections.singleton(new Player.Location(1, 0)), 
				Collections.singleton(new Player.Location(1, 1))))
		.isEqualTo(Collections.singleton(new Player.Node(emptyMap(), new Player.Location(1, 0), new Player.Location(1, 1))));
	}
	
	@Test
	public void buildsFixedNode() throws Exception {
		assertThat(fixedNode())
		.isEqualTo(Collections.singleton(new Player.Node(emptyMap(), new Player.Location(1, 0), new Player.Location(1, 0))));
	}

	protected Set<Player.Node> fixedNode() {
		return Player.Node.getNodes(emptyMap(), Collections.singleton(new Player.Location(1, 0)), 
				Collections.singleton(new Player.Location(1, 0)));
	}
	
	@Test
	public void buildsOverlappingNodes() throws Exception {
		assertThat(Player.Node.getNodes(emptyMap(), new HashSet<>(Arrays.asList(new Player.Location(1, 0), new Player.Location(0, 1))), 
				Collections.singleton(new Player.Location(0, 0))))
		.isEqualTo(new HashSet<>(Arrays.asList(new Player.Node(emptyMap(), new Player.Location(1, 0), new Player.Location(0, 0)),
				new Player.Node(emptyMap(), new Player.Location(0, 1), new Player.Location(0, 0)))));
	}
	
	@Test
	public void buildsNodesStartingFromOverlapping() throws Exception {
		assertThat(nodesStartingFromOverlapping())
		.isEqualTo(new HashSet<>(Arrays.asList(new Player.Node(emptyMap(), new Player.Location(0, 0), new Player.Location(1, 0)),
				new Player.Node(emptyMap(), new Player.Location(0, 0), new Player.Location(0, 1)))));
	}

	protected Set<Player.Node> nodesStartingFromOverlapping() {
		return Player.Node.getNodes(emptyMap(), 
				Collections.singleton(new Player.Location(0, 0)), 
				new HashSet<>(Arrays.asList(new Player.Location(1, 0), new Player.Location(0, 1))));
	}
	
	@Test
	public void computesNodesLocationsAtALaterRound() throws Exception {
		Player.Game game = new Player.Game(nodesStartingFromOverlapping(), Collections.emptySet(), 12, 12);
		game.nextRound();
		game.nextRound();
		assertThat(game.getNodeLocations()).isEqualTo(
				new HashSet<>(Arrays.asList(new Player.Location(3, 0), new Player.Location(0, 3))));
	}
	
	@Test
	public void movingDownWithBounceOnObstacle() throws Exception {
		assertThat(Player.buildTrajectory(borderedMap(), new Player.Location(2, 2), new Player.Location(2, 1)))
		.isEqualTo(Arrays.asList(new Player.Location(2, 2), new Player.Location(2, 1),
				new Player.Location(2, 2), new Player.Location(2, 3)));
	}
	
	@Test
	public void addedBombBlowsAfterThreeRoundsDestroyingAdiacentNode() throws Exception {
		Player.Game game = new Player.Game(fixedNode(), Collections.emptySet(), 5, 5);
		game.placeBombAt(new Player.Location(0,0));
		game.nextRound();
		game.nextRound();
		
		assertThat(game.getNodeLocations()).isNotEmpty();
		game.nextRound();
		
		assertThat(game.getNodeLocations()).isEmpty();
	}
	
	@Test
	public void addedBombCanBeTriggeredEarlyByAPreviousBomb() throws Exception {
		Player.Game game = new Player.Game(fixedNode(), Collections.emptySet(), 5, 5);
		game.placeBombAt(new Player.Location(0,2));
		game.nextRound();
		game.placeBombAt(new Player.Location(0,0));
		game.nextRound();
		
		assertThat(game.getNodeLocations()).isNotEmpty();
		game.nextRound();
		
		assertThat(game.getNodeLocations()).isEmpty();
	}
	
	@Test
	public void bombableLocationsIncludes11() throws Exception {
		Player.Game game = new Player.Game(fixedNode(), Collections.emptySet(), 5, 5);
		assertThat(game.getBombableLocations()).contains(new Player.Location(1,1));
	}
	
	@Test
	public void bombableLocationsExcludesWalls() throws Exception {
		Player.Game game = new Player.Game(fixedNode(), Collections.singleton(new Player.Location(0,0)), 5, 5);
		assertThat(game.getBombableLocations()).doesNotContain(new Player.Location(0,0));
	}
	
	@Test
	public void bombableLocationsExcludesCurrentNodeLocations() throws Exception {
		Player.Game game = new Player.Game(fixedNode(), Collections.emptySet(), 5, 5);
		assertThat(game.getBombableLocations()).doesNotContain(new Player.Location(1, 0));
	}
	
	@Test
	public void bombableLocationsExcludesCurrentBombs() throws Exception {
		Player.Game game = new Player.Game(fixedNode(), Collections.emptySet(), 5, 5);
		game.placeBombAt(new Player.Location(1, 1));
		
		assertThat(game.getBombableLocations()).doesNotContain(new Player.Location(1,1));
	}
	
	@Test
	public void simulatesGameFromCurrentGame() throws Exception {
		Player.Game game = new Player.Game(movingNode(), Collections.emptySet(), 12, 12);
		Player.Game simulated = game.simulateRounds(2);
		
		assertThat(game.getNodeLocations()).isEqualTo(Collections.singleton(new Player.Location(1, 0)));
		assertThat(simulated.getNodeLocations()).isEqualTo(Collections.singleton(new Player.Location(3, 0)));
	}
	
	@Test
	public void computesBombDamageInTheFuture() throws Exception {
		Player.Game game = new Player.Game(movingNode(), Collections.emptySet(), 12, 12);
		assertThat(game.newBombDamage(new Player.Location(1, 2))).isEqualTo(0);
		assertThat(game.newBombDamage(new Player.Location(4, 2))).isEqualTo(1);
	}
	
	@Test
	public void bombWithHighestDamageDestroysEverything() throws Exception {
		Set<Player.Location> locationsWithEmptyCenter = new HashSet<>(Arrays.asList(new Player.Location(0, 1), 
				new Player.Location(1, 0), new Player.Location(1, 2), new Player.Location(2, 1)));
		Set<Player.Node> nodes = Player.Node.getNodes(emptyMap(), locationsWithEmptyCenter, locationsWithEmptyCenter);
		Player.Game game = new Player.Game(nodes, Collections.emptySet(), 12, 12);
		
		assertThat(new Player.BombWithHighestDamage(game).getBombLocations()).startsWith(new Player.Location(1, 1));
	}
	
	@Test
	public void retrievesBorderObstacles() throws Exception {
		List<String> in = new ArrayList<>();
		in.add("1");
		in.add("1");
		in.add("");
		in.addAll(borderedMap());
		assertThat(new Player.GameRound(new LinesScanner(in), 5).getObstacles()).contains(new Player.Location(0, 0));
	}

	protected Set<Player.Node> movingNode() {
		return Player.Node.getNodes(emptyMap(), Collections.singleton(new Player.Location(0, 0)), 
				Collections.singleton(new Player.Location(1, 0)));
	}

	private List<String> borderedMap() {
		return Arrays.asList("#####",
							 "#...#", 
							 "#...#", 
							 "#...#", 
							 "#####");
	}
}


