import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class BoxTest {
    @Test
    public void findsLinesThatWereAdded() {
        List<String> lines = Arrays.asList(
                ".................",
                ".................",
                "...##...###..#...",
                ".####..#####.###.",
                "######.##########");
        Player.Box box = new Player.Box(5, 17, lines);
        assertThat(box.getLines()).isEqualTo(lines);
    }
}
