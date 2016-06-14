import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void testName() throws Exception {
		new Player.Parser("'users'=(('id'=10;\n"+
							"'name'='Serge';"+
							"'roles'=('visitor';"+
							"'moderator'"+
							"));"+
							"('id'=11;"+
							"'name'='Biales'"+
							");"+
							"true"+
							")").parse();
	}
	
	@Test
	public void blockWithSingleValue() throws Exception {
		new Player.Parser("(0;1;2)").parse();
	}
	
	@Test
	public void emptyBlock() throws Exception {
		new Player.Parser("()").parse();
	}
}

