
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Test;

public class PlayerTest {
	@Test
	public void addingALinkAddsTheOppositeLink() throws Exception {
		Player.Node.get(0).addLink(Player.Node.get(1));
		
		assertThat(Player.Node.get(1).getLinks()).contains(Player.Node.get(0));
	}
	
	@Test
	public void severingALinkResultsInLinkBeingRemoved() throws Exception {
		Player.Node.get(0).addLink(Player.Node.get(1));
		
		Player.severLinkBetween(0, 1);
		
		assertThat(Player.Node.get(1).getLinks()).doesNotContain(Player.Node.get(0));
	}
	
	@Test
	public void generatesPathsFrom0ToGateway() throws Exception {
		Player.Node.get(0).addLink(Player.Node.get(1));
		Player.Node.get(1).addLink(Player.Node.get(2));
		Player.Node.get(1).addLink(Player.Node.get(3));
		
		Player.markGateway(2);
		Player.markGateway(3);
		
		assertThat(Player.findPathsToGatewaysFrom(0)).isEqualTo(new LinkedHashSet<>(Arrays.asList(Arrays.asList(
				Player.Node.get(0), Player.Node.get(1), Player.Node.get(2)),Arrays.asList(
						Player.Node.get(0), Player.Node.get(1), Player.Node.get(3)))));
		
		Player.GATEWAYS.clear();
	}
	
	@Test
	public void getsShortestPathToGateway() throws Exception {
		Player.Node.get(0).addLink(Player.Node.get(1));
		Player.Node.get(1).addLink(Player.Node.get(2));
		Player.Node.get(1).addLink(Player.Node.get(3));
		Player.Node.get(3).addLink(Player.Node.get(4));
		
		Player.markGateway(2);
		Player.markGateway(4);
		
		assertThat(Player.findShortestPathToGatewaysFrom(0)).isEqualTo(Arrays.asList(
				Player.Node.get(0), Player.Node.get(1), Player.Node.get(2)));
	}
	
	@Test
	public void printsLinkBetween0And1() throws Exception {
		Player.Node.get(0).addLink(Player.Node.get(1));
		
		assertThat(Player.printLink(Player.Node.get(0), Player.Node.get(1))).isEqualTo("0 1");
	}
}
