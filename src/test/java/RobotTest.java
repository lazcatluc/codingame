import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RobotTest {

    private Player.Board board;

    @Before
    public void setUp() throws Exception {
        board = new Player.Board(5, 3);
    }

    @Test
    public void findsSimplePathUntiFallingDown() throws Exception {
        board.readLine(0, "#####");
        board.readLine(1, "#...#");
        board.readLine(2, "#####");
        Player.Robot robot = new Player.Robot(board, new Player.Node(1, 1, 'R'));
        assertThat(robot.getPath().size()).isEqualTo(3);
    }

    @Test
    public void findsPathWithReturnUntiFallingDown() throws Exception {
        board.readLine(0, "#####");
        board.readLine(1, "#..L#");
        board.readLine(2, "#####");
        Player.Robot robot = new Player.Robot(board, new Player.Node(1, 1, 'R'));
        assertThat(robot.getPath().size()).isEqualTo(5);
    }

    @Test
    public void findsPathWithTwoReturnsUntilRepeating() throws Exception {
        board.readLine(0, "#####");
        board.readLine(1, "#R.L#");
        board.readLine(2, "#####");
        Player.Robot robot = new Player.Robot(board, new Player.Node(1, 1, 'R'));
        assertThat(robot.getPath().size()).isEqualTo(5);
    }

    @Test
    public void findsCircularPathUntilRepeating() throws Exception {
        board.readLine(0, "#####");
        board.readLine(1, ".....");
        board.readLine(2, "#####");
        Player.Robot robot = new Player.Robot(board, new Player.Node(1, 1, 'R'));
        assertThat(robot.getPath().size()).isEqualTo(5);
    }
}
