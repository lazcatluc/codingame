import static org.assertj.core.api.Assertions.assertThat;

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
	public void vandalism() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> input = Arrays.asList(
				"16",
				"12",
				"",
				"98",
				"10",
				"",
				"##..@...##@@@.@@",
				"##.....#.##..@..",
				"..#...#..#######",
				"...#@#...##@...#",
				"@..@.@..@#....#.",
				"...#@#####...#..",
				"..#...#...#@#...",
				".#....#@..@.@..@",
				"#...@.##..#@#...",
				"#######@##...#..",
				"......@.@#....##",
				"@.........#@..#.",
				"97",
				"10",
				"",
				"##.@....##@@@.@@",
				"##.....#.##.@...",
				"..#...#..#######",
				"...#@#..@##.@..#",
				"...@.@...#....#.",
				"@..#@#####...#..",
				"..#...#@..#@#...",
				".#....#...@.@...",
				"#..@..##..#@#..@",
				"#######@##...#..",
				"......@.@#....##",
				".@........#.@.#.",
				"96",
				"10",
				"",
				"##@.....##@@@.@@",
				"##.....#.##@....",
				"..#...#.@#######",
				"...#@#...##..@.#",
				"...@.@...#....#.",
				"...#@#####...#..",
				"@.#...#...#@#...",
				".#....#@..@.@...",
				"#.@...##..#@#...",
				"#######@##...#.@",
				"......@.@#....##",
				"..@.......#..@#.",
				"95",
				"10",
				"",
				"##.@....##@@@.@@",
				"##.....#@##.@...",
				"..#...#..#######",
				"...#@#...##...@#",
				"...@.@...#....#.",
				"...#@#####...#..",
				"..#...#@..#@#...",
				"@#....#...@.@...",
				"#@....##..#@#..@",
				"#######@##...#..",
				"......@.@#....##",
				"...@......#.@.#.",
				"94",
				"10",
				"",
				"##..@...##@@@.@@",
				"##.....#.##..@..",
				"..#...#.@#######",
				"...#@#...##..@.#",
				"...@.@...#....#.",
				"...#@#####...#..",
				"@.#...#...#@#...",
				".#....#@..@.@..@",
				"#.@...##..#@#...",
				"#######@##...#..",
				"......@.@#....##",
				"....@.....#@..#.",
				"93",
				"10",
				"",
				"##...@..##@@@.@@",
				"##.....#.##...@.",
				"..#...#..#######",
				"...#@#..@##.@..#",
				"...@.@...#....#.",
				"@..#@#####...#..",
				"..#...#@..#@#..@",
				".#....#...@.@...",
				"#..@..##..#@#...",
				"#######@##...#..",
				"......@.@#....##",
				".....@....#.@.#.");
		LinesScanner in = new LinesScanner(input);
		PrintStream out = new PrintStream(baos);
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        int initialWaits = 5;
        Player.GameRound[] positions = new Player.GameRound[initialWaits + 1];
        for (int i = 0; i <= initialWaits; i++) {
            positions[i] = new Player.GameRound(in, height);
        }
        Player.GameRound round = positions[initialWaits];
        System.err.println(in);
        @SuppressWarnings("unchecked")
		Set<Player.Location>[] subsequentNodes = new Set[initialWaits - 1];
        for (int i = 2; i <= initialWaits; i++) {
        	subsequentNodes[i - 2] = positions[i].getNodes(); 
        }
        Set<Player.Node> nodes = Player.Node.getNodes(round.map, positions[0].getNodes(), positions[1].getNodes(), subsequentNodes);

        assertThat(nodes.size()).isEqualTo(26);
        Player.Game game = new Player.Game(nodes, positions[0].getObstacles(), width, height, 10);
        Player.SubsetCoverage solver = new Player.SubsetCoverage(game, 90, 10);
        
        assertSolverHasReallySolved(game, solver);
	}
	
	

	@Test
	public void patience() throws Exception {
		Player.Game game = initPatience();
        
        Player.GreedyStrategyWaitingForSignificantFractionOfNodes solver = 
        		new Player.GreedyStrategyWaitingForSignificantFractionOfNodes(game, 3);
        
        assertSolverHasReallySolved(game, solver);
	}
	
	@Test
	public void patienceSubsetCoverage() throws Exception {
		Player.Game game = initPatience();
        
		Player.SubsetCoverage solver = new Player.SubsetCoverage(game, 90, 3);
        
		assertSolverHasReallySolved(game, solver);
	}

	protected Player.Game initPatience() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> input = Arrays.asList(
				"12",
				"9",
				"",
				"90",
				"3",
				"",
				"@...........",
				".....@....@.",
				"@...........",
				".....@......",
				"..@.@.@.@...",
				".....@......",
				"............",
				".@...@......",
				".........@.@",
				"89",
				"3",
				"",
				".@..........",
				".....@....@.",
				"............",
				"@....@......",
				"..@.@.@.@...",
				".....@......",
				"............",
				".@...@.....@",
				"........@...",
				"88",
				"3",
				"",
				"..@.........",
				".....@....@.",
				"............",
				".....@......",
				"@.@.@.@.@...",
				".....@......",
				"...........@",
				".@...@......",
				".......@....");
		LinesScanner in = new LinesScanner(input);
		PrintStream out = new PrintStream(baos);
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        Player.GameRound initialPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound secondPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound thirdPosition = new Player.GameRound(in, height);
        Set<Player.Node> nodes = Player.Node.getNodes(thirdPosition.map, initialPosition.getNodes(), 
        		secondPosition.getNodes(), thirdPosition.getNodes());
        
        assertThat(nodes.size()).isEqualTo(14);
        Player.Game game = new Player.Game(nodes, thirdPosition.getObstacles(), width, height, 4);
		return game;
	}
	
	@Test
	public void fourBombs() throws Exception {
		Player.Game game = initFourBombs();
        Player.GameSolverWithStrategyAndWait solver = new Player.GameSolverWithStrategyAndWait(game, 4);
        
        assertSolverHasReallySolved(game, solver);
	}
	
	@Test
	public void fourBombsWithGreedy() throws Exception {
		Player.Game game = initFourBombs();
        Player.GreedyStrategyWaitingForSignificantFractionOfNodes solver = 
        		new Player.GreedyStrategyWaitingForSignificantFractionOfNodes(game, 4);
        
        assertSolverHasReallySolved(game, solver);
	}
	
	@Test
	@Ignore
	public void fourBombsSetCoverage() throws Exception {
		Player.Game game = initFourBombs();
        Player.SubsetCoverage solver = new Player.SubsetCoverage(game, 60, 4);
        
        assertSolverHasReallySolved(game, solver);
	}


	protected Player.Game initFourBombs() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> input = Arrays.asList(
				"12",
				"9",
				"",
				"60",
				"4",
				"",
				"........@...",
				".......@....",
				".#.#.#@#.#.#",
				".....@......",
				"#.#.@#..#.#.",
				"...@........",
				"..@.........",
				".@..........",
				"@...........",
				"59",
				"4",
				"",
				".......@....",
				"........@...",
				".#.#.#.#.#.#",
				"....@.@.....",
				"#.#..#..#.#.",
				"..@.........",
				"...@........",
				"............",
				".@..........",
				"58",
				"4",
				"",
				"......@.....",
				".........@..",
				".#.#@#.#.#.#",
				".......@....",
				"#.#..#@.#.#.",
				".@..........",
				"....@.......",
				".@..........",
				"..@.........");
		
		LinesScanner in = new LinesScanner(input);
		PrintStream out = new PrintStream(baos);
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        Player.GameRound initialPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound secondPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound thirdPosition = new Player.GameRound(in, height);
        Set<Player.Node> nodes = Player.Node.getNodes(thirdPosition.map, initialPosition.getNodes(), 
        		secondPosition.getNodes(), thirdPosition.getNodes());
        
        assertThat(nodes.size()).isEqualTo(9);
        Player.Game game = new Player.Game(nodes, thirdPosition.getObstacles(), width, height);
		return game;
	}
	
	
	@Test
	public void endOfFourNodesOneBomb() throws Exception {
		Player.Game game = initEndOfFourNodes();
        
        Player.GameSolverWithStrategyAndWait solver = new Player.GameSolverWithStrategyAndWait(game, 1);
        
        assertSolverHasReallySolved(game, solver);
	}
	
	@Test
	public void endOfFourNodesOneBombGreedy() throws Exception {
		Player.Game game = initEndOfFourNodes();
        
        Player.GreedyStrategyWaitingForSignificantFractionOfNodes solver = new Player.GreedyStrategyWaitingForSignificantFractionOfNodes(game, 1);
        
        assertSolverHasReallySolved(game, solver);
	}

	protected Player.Game initEndOfFourNodes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> input = Arrays.asList(
				"12",
				"9",
				"",
				"55",
				"1",
				"",
				"#..........#",
				"............",
				"....#..#....",
				".@...@......",
				"...#....#...",
				"..........@.",
				"....#..#....",
				"......@.....",
				"#..........#",
				"54",
				"1",
				"",
				"#..........#",
				"............",
				"....#@.#....",
				"..@.........",
				"...#....#...",
				"...........@",
				"....#..#....",
				"............",
				"#.....@....#",
				"53",
				"1",
				"",
				"#..........#",
				".....@......",
				"....#..#....",
				"...@........",
				"...#....#...",
				"..........@.",
				"....#..#....",
				"......@.....",
				"#..........#");
		LinesScanner in = new LinesScanner(input);
		PrintStream out = new PrintStream(baos);
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        Player.GameRound initialPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound secondPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound round = new Player.GameRound(in, height);
        Set<Player.Node> nodes = Player.Node.getNodes(secondPosition.map, initialPosition.getNodes(), 
        		secondPosition.getNodes(), round.getNodes());
        Player.Game game = new Player.Game(nodes, round.getObstacles(), width, height);
		return game;
	}
	
	@Test
	public void fourNodesOneBomb() throws Exception {
		Player.Game game = initFourNodesOneBomb();
        
        Player.GameSolverWithStrategyAndWait solver = new Player.GameSolverWithStrategyAndWait(game, 1);
        
        assertSolverHasReallySolved(game, solver);
	}
	
	@Test
	public void fourNodesOneBombGreedy() throws Exception {
		Player.Game game = initFourNodesOneBomb();
        
        Player.GreedyStrategyWaitingForSignificantFractionOfNodes solver = 
        		new Player.GreedyStrategyWaitingForSignificantFractionOfNodes(game, 1);
        
        assertSolverHasReallySolved(game, solver);
	}
	
	@Test
	public void fourNodesOneBombSetCoverage() throws Exception {
		Player.Game game = initFourNodesOneBomb();
        
        Player.SubsetCoverage solver = 
        		new Player.SubsetCoverage(game, 55, 1);
        
        assertSolverHasReallySolved(game, solver);
	}

	protected Player.Game initFourNodesOneBomb() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> input = Arrays.asList(
				"12",
				"9",
				"",
				"55",
				"1",
				"",
				"#..........#",
				"............",
				"....#.@#....",
				"..@.........",
				"...#....#...",
				"...........@",
				"....#..#....",
				"............",
				"#....@.....#",
				"54",
				"1",
				"",
				"#..........#",
				"............",
				"....#..#....",
				"...@..@.....",
				"...#....#...",
				"..........@.",
				"....#..#....",
				".....@......",
				"#..........#");
		LinesScanner in = new LinesScanner(input);
		PrintStream out = new PrintStream(baos);
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        Player.GameRound initialPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound round = new Player.GameRound(in, height);
        Set<Player.Node> nodes = Player.Node.getNodes(round.map, initialPosition.getNodes(), round.getNodes());
        System.out.println(nodes);
        Player.Game game = new Player.Game(nodes, round.getObstacles(), width, height);
		return game;
	}
	
	@Test
	public void sixNodes() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> input = Arrays.asList(
				"12",
				"9",
				"",
				"50",
				"7",
				"",
				"..@....@....",
				"...........@",
				".@..........",
				".....@......",
				"@...........",
				"............",
				"............",
				".@..........",
				"............",
				"49",
				"7",
				"",
				"..@........@",
				".......@....",
				"@...........",
				"............",
				".@...@......",
				"............",
				"............",
				"..@.........",
				"............",
				"48",
				"6",
				"",
				"..@.........",
				"...........@",
				".@.....@....",
				"............",
				"..@.........",
				".....@......",
				"............",
				"...@........",
				"............");
		LinesScanner in = new LinesScanner(input);
		PrintStream out = new PrintStream(baos);
		int width = in.nextInt(); // width of the firewall grid
        int height = in.nextInt(); // height of the firewall grid
        in.nextLine();
        Player.GameRound initialPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound secondPosition = new Player.GameRound(in, height);
        out.println("WAIT");
        Player.GameRound round = new Player.GameRound(in, height);
        Set<Player.Node> nodes = Player.Node.getNodes(secondPosition.map, initialPosition.getNodes(), 
        		secondPosition.getNodes(), round.getNodes());
        
        Player.Game game = new Player.Game(nodes, round.getObstacles(), width, height);
        Player.GameSolverWithStrategyAndWait solver = new Player.GameSolverWithStrategyAndWait(game, round.bombs);
        assertSolverHasReallySolved(game, solver);
	}

	protected void assertSolverHasReallySolved(Player.Game game, Player.BombStrategy solver) {
		Queue<Player.Location> bombsToBePlaced = new LinkedList<>();
		assertThat(solver.isSolved()).isTrue();    
    	bombsToBePlaced.addAll(solver.getBombLocations());
        while (!bombsToBePlaced.isEmpty()) {
        	Player.Location bomb = bombsToBePlaced.poll();
        	if (bomb != null) {
        		game.placeBombAt(bomb);
        	}
        	//System.out.println(game);
        	game.nextRound();
        }
        
        assertThat(game.getNodeLocations()).isEmpty();
	}
	
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
		
		assertThat(game.getNodeLocations()).isEqualTo(Collections.singleton(new Player.Location(2, 0)));
		assertThat(simulated.getNodeLocations()).isEqualTo(Collections.singleton(new Player.Location(4, 0)));
	}
	
	@Test
	public void computesBombDamageInTheFuture() throws Exception {
		Player.Game game = new Player.Game(movingNode(), Collections.emptySet(), 12, 12);
		assertThat(game.newBombDamage(new Player.Location(1, 2))).isEqualTo(0);
		assertThat(game.newBombDamage(new Player.Location(5, 2))).isEqualTo(1);
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


