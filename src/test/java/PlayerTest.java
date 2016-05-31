
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

public class PlayerTest {
	
	@After
	public void tearDown() {
		Player.GATEWAYS.clear();
		Player.Node.clearNodes();
	}
	
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
		
	}
	
	@Test
	public void getsShortestPathToGateway() throws Exception {
		Player.Node.get(0).addLink(Player.Node.get(1));
		Player.Node.get(1).addLink(Player.Node.get(2));
		Player.Node.get(1).addLink(Player.Node.get(3));
		Player.Node.get(3).addLink(Player.Node.get(4));
		
		Player.markGateway(2);
		Player.markGateway(4);
		
		assertThat(Player.findNodesWithMostGateways(Player.findPathsToGatewaysFrom(0)).get(0))
			.isEqualTo(Player.Node.get(1));
	}
	
	@Test
	public void printsLinkBetween0And1() throws Exception {
		Player.Node.get(0).addLink(Player.Node.get(1));
		
		assertThat(Player.printLink(Player.Node.get(0), Player.Node.get(1))).isEqualTo("0 1");
	}
	
	@Test
	public void longNetwork() throws Exception {
		String input = "37\n"+
				"81\n"+
				"4\n"+
				"2\n"+
				"5\n"+
				"14\n"+
				"13\n"+
				"16\n"+
				"13\n"+
				"19\n"+
				"21\n"+
				"13\n"+
				"7\n"+
				"16\n"+
				"8\n"+
				"35\n"+
				"5\n"+
				"2\n"+
				"35\n"+
				"10\n"+
				"0\n"+
				"8\n"+
				"3\n"+
				"23\n"+
				"16\n"+
				"0\n"+
				"1\n"+
				"31\n"+
				"17\n"+
				"19\n"+
				"22\n"+
				"12\n"+
				"11\n"+
				"1\n"+
				"2\n"+
				"1\n"+
				"4\n"+
				"14\n"+
				"9\n"+
				"17\n"+
				"16\n"+
				"30\n"+
				"29\n"+
				"32\n"+
				"22\n"+
				"28\n"+
				"26\n"+
				"24\n"+
				"23\n"+
				"20\n"+
				"19\n"+
				"15\n"+
				"13\n"+
				"18\n"+
				"17\n"+
				"6\n"+
				"1\n"+
				"29\n"+
				"28\n"+
				"15\n"+
				"14\n"+
				"9\n"+
				"13\n"+
				"32\n"+
				"18\n"+
				"25\n"+
				"26\n"+
				"1\n"+
				"7\n"+
				"34\n"+
				"35\n"+
				"33\n"+
				"34\n"+
				"27\n"+
				"16\n"+
				"27\n"+
				"26\n"+
				"23\n"+
				"25\n"+
				"33\n"+
				"3\n"+
				"16\n"+
				"30\n"+
				"25\n"+
				"24\n"+
				"3\n"+
				"2\n"+
				"5\n"+
				"4\n"+
				"31\n"+
				"32\n"+
				"27\n"+
				"25\n"+
				"19\n"+
				"3\n"+
				"17\n"+
				"8\n"+
				"4\n"+
				"2\n"+
				"32\n"+
				"17\n"+
				"10\n"+
				"11\n"+
				"29\n"+
				"27\n"+
				"30\n"+
				"27\n"+
				"6\n"+
				"4\n"+
				"24\n"+
				"15\n"+
				"9\n"+
				"10\n"+
				"34\n"+
				"2\n"+
				"9\n"+
				"7\n"+
				"11\n"+
				"6\n"+
				"33\n"+
				"2\n"+
				"14\n"+
				"10\n"+
				"12\n"+
				"6\n"+
				"0\n"+
				"6\n"+
				"19\n"+
				"17\n"+
				"20\n"+
				"3\n"+
				"21\n"+
				"20\n"+
				"21\n"+
				"32\n"+
				"15\n"+
				"16\n"+
				"0\n"+
				"9\n"+
				"23\n"+
				"27\n"+
				"11\n"+
				"0\n"+
				"28\n"+
				"27\n"+
				"22\n"+
				"18\n"+
				"3\n"+
				"1\n"+
				"23\n"+
				"15\n"+
				"18\n"+
				"19\n"+
				"7\n"+
				"0\n"+
				"19\n"+
				"8\n"+
				"21\n"+
				"22\n"+
				"7\n"+
				"36\n"+
				"13\n"+
				"36\n"+
				"8\n"+
				"36\n"+
				"0\n"+
				"16\n"+
				"18\n"+
				"26\n"+
				"2\n";
		Player.readData(new Scanner(new ByteArrayInputStream(input.getBytes())));
		Set<List<Player.Node>> paths = Player.findPathsToGatewaysFrom(2);	
		System.out.println(paths);
		assertThat(paths.size()).isEqualTo(1);
		assertThat(Player.findOtherNeighborsWithDirectConnections(Collections.singleton(Player.Node.get(1))).size())
			.isEqualTo(13);
		
		System.out.println(Player.findShortestPathToGatewaysFrom(2));
	}
}
