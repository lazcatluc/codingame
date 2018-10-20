import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BoardTest {
    private Player.Board board;

    @Before
    public void setUp() throws Exception {
        board = new Player.Board(3, 3);
    }

    @Test
    public void emptyCellMaintainsUpDirection() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'U');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(1, 0, 'U'));
    }

    @Test
    public void emptyCellMaintainsDownDirection() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'D');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(1, 2, 'D'));
    }

    @Test
    public void emptyCellMaintainsLeftDirection() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'L');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(0, 1, 'L'));
    }

    @Test
    public void emptyCellMaintainsRightDirection() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'R');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(2, 1, 'R'));
    }

    @Test
    public void emptyCellAtRightEdgeMovesToTheOtherSide() throws Exception {
        Player.Node node = new Player.Node(2, 1, 'R');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(0, 1, 'R'));
    }

    @Test
    public void emptyCellAtLeftEdgeMovesToTheOtherSide() throws Exception {
        Player.Node node = new Player.Node(0, 1, 'L');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(2, 1, 'L'));
    }

    @Test
    public void emptyCellAtTopEdgeMovesToTheOtherSide() throws Exception {
        Player.Node node = new Player.Node(1, 0, 'U');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(1, 2, 'U'));
    }

    @Test
    public void emptyCellAtBottomEdgeMovesToTheOtherSide() throws Exception {
        Player.Node node = new Player.Node(1, 2, 'D');
        emptyBoard();
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(1, 0, 'D'));
    }

    @Test
    public void cellWithUpArrowMovesPlayerUp() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'R');
        board.readLine(0, "...");
        board.readLine(1, ".U.");
        board.readLine(2, "...");
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(1, 0, 'U'));
    }

    @Test
    public void cellWithDownArrowMovesPlayerDown() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'R');
        board.readLine(0, "...");
        board.readLine(1, ".D.");
        board.readLine(2, "...");
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(1, 2, 'D'));
    }

    @Test
    public void cellWithLeftArrowMovesPlayerLeft() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'R');
        board.readLine(0, "...");
        board.readLine(1, ".L.");
        board.readLine(2, "...");
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(0, 1, 'L'));
    }

    @Test
    public void cellWithRightArrowMovesPlayerRight() throws Exception {
        Player.Node node = new Player.Node(1, 1, 'U');
        board.readLine(0, "...");
        board.readLine(1, ".R.");
        board.readLine(2, "...");
        assertThat(board.getNextNode(node)).isEqualTo(new Player.Node(2, 1, 'R'));
    }

    private void emptyBoard() {
        board.readLine(0, "...");
        board.readLine(1, "...");
        board.readLine(2, "...");
    }
}
