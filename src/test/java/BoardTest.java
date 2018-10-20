import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    @Test
    public void printsEmptyBoard() throws Exception {
        emptyBoard();
        assertThat(board.toString()).isEqualToIgnoringWhitespace("...\n...\n...\n");
    }

    @Test
    public void expandsBoardBoard() throws Exception {
        emptyBoard();
        Map<Player.Node, Player.Board> boards = board.expand(new Player.Node(1, 1, 'U'));
        Set<String> boardsToString = boards.values().stream().map(Player.Board::toString).collect(Collectors.toSet());
        assertThat(boardsToString).isEqualTo(new HashSet<>(Arrays.asList(
                "...\n.D.\n...\n", "...\n.R.\n...\n", "...\n.L.\n...\n")));
    }

    private void emptyBoard() {
        board.readLine(0, "...");
        board.readLine(1, "...");
        board.readLine(2, "...");
    }
}
