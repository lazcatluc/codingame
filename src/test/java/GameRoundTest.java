import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class GameRoundTest {

    private Player.Board board;
    private Player.GameRound gameRound;

    @Before
    public void setUp() throws Exception {
        board = new Player.Board(5, 3);
        board.readLine(0, "#####");
        board.readLine(1, "#...#");
        board.readLine(2, "#####");
        Player.Robot robot = new Player.Robot(board, new Player.Node(1, 1, 'R'));
        gameRound = new Player.GameRound(board, Collections.singleton(robot));

    }

    @Test
    public void improvesSimpleGame() throws Exception {
        Optional<Player.GameRound> improvedGameRound = gameRound.improveByAddingOneArrow();
        assertThat(improvedGameRound).isPresent();
        assertThat(improvedGameRound.get().getBoard().toString())
                .isEqualToIgnoringWhitespace(
                        "##### "+
                        "#..L# "+
                        "##### ");
        assertThat(gameRound.getAddedArrow()).isEqualTo(new Player.Node(3, 1, 'L'));
    }

    @Test
    public void cannotImproveSimpleGameTwice() throws Exception {
        assertThat(gameRound.improveByAddingOneArrow().get().improveByAddingOneArrow()).isEmpty();
    }
}
