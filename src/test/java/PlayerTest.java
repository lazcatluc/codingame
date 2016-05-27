
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void buildsDigits() throws Exception {
		String digitInput=".oo.o...oo..ooo.oooo....o...oo..ooo.oooo____o...oo..ooo.oooo____o...oo..ooo.oooo\n"+
						  "o..o................____________________________________________________________\n"+
						  ".oo.........................................____________________________________\n"+
					  	  "................................................................________________\n";
		List<List<String>> digits = Player.buildDigits(new Scanner(new ByteArrayInputStream(digitInput.getBytes())),4,4);
		
		assertThat(digits.get(5)).isEqualTo(Arrays.asList("....","____","....","...."));
	}
}
