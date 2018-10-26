import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

public class PlayerTest {

    @Test
    public void solvesInputOutput() throws Exception {
        solvesInputOutput("");
    }

    @Test
    public void solvesInputOutput03() throws Exception {
        solvesInputOutput("03");
    }

    private void solvesInputOutput(String which) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src", "main", "resources",
                "input" + which + ".txt"));
        Player.MyScanner in = new LinesScanner(lines);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        Player.run(in, out);
        assertThat(new String(baos.toByteArray(), Charset.forName("UTF-8")).split("\\s+").length)
                .isEqualTo(Integer.parseInt(lines.get(0)));
    }

}
