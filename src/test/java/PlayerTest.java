import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

public class PlayerTest {

    @Test
    public void solvesInputOutput() throws Exception {
        solvesInputOutput("");
    }

    private void solvesInputOutput(String which) throws Exception {
        Player.MyScanner in = new LinesScanner(Files.readAllLines(Paths.get("src", "main", "resources",
                "input" + which + ".txt")));
        String output = Files.lines(Paths.get("src", "main", "resources", "output" + which + ".txt"))
                .reduce(new StringBuilder(), (sb, s) -> sb.append(s).append("\n"), StringBuilder::append).toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        Player.run(in, out);
        assertThat(new String(baos.toByteArray(), Charset.forName("UTF-8"))).isEqualToIgnoringWhitespace(output);
    }

}
