import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PieceTest {
    @Test
    public void readsPiece() {
        String input = "0 c1";
        assertThat(new Player.Piece(input).toString()).isEqualTo("Piece{color=WHITE, x=c, y=1}");
    }
}
