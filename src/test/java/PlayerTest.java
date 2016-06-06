
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import org.junit.Test;

public class PlayerTest {
	private String in = 
			"...0......\n"+
			"...00.....\n"+
			"...0..0...\n"+
			"...0......\n";
	
	private String heavyIn = 
			"................000000000........00000........000.............00.\n"+
			".0.0..................000....000......0.0..................00000.\n"+
			"....000.........0.0...000................000............000000.0.\n"+
			"............0.000000...........0000...............0.0.....000000.\n";

	private Player.Runway newRunway() {
		return newRunway(in);
	}
	
	private Player.Runway newRunway(String in) {
		return new Player.Runway(new Scanner(new ByteArrayInputStream(in.getBytes())));
	}
	
	private Player.MotorcyleActingOnRunway newMotorcycleActingOnRunway(Player.Motorcycle motorcycle, Player.Action action) {
		return new Player.MotorcyleActingOnRunway(motorcycle, newRunway(), action);
	}

	@Test
	public void zeroZeroIsClear() throws Exception {
		assertThat(newRunway().isClear(0, 0)).isTrue();
	}

	@Test
	public void zeroThreeIsNotClear() throws Exception {
		assertThat(newRunway().isClear(3, 0)).isFalse();
	}

	@Test
	public void outsideYIsClear() throws Exception {
		assertThat(newRunway().isClear(1000, 0)).isTrue();
	}

	@Test
	public void downFrom42IsClear() throws Exception {
		assertThat(newRunway().isClear(4, 2)).isTrue();
		assertThat(newRunway().isClear(6, 3)).isTrue();
		assertThat(newRunway().isClear(5, 2)).isTrue();
		assertThat(newRunway().isClear(5, 3)).isTrue();
		Player.Motorcycle motorcycle = builderAt42().goingAt(2).build();
		Player.Action action = Player.Action.DOWN;
		assertThat(motorcycle.act(action).getX()).isEqualTo(6);
		assertThat(motorcycle.act(action).getY()).isEqualTo(3);
		assertThat(newMotorcycleActingOnRunway(motorcycle, action).isPathClear()).isTrue();
		motorcycle = builderAt42().goingAt(3).build();
		action = Player.Action.UP;
		assertThat(newMotorcycleActingOnRunway(motorcycle, action).isPathClear()).isFalse();
	}

	protected Player.Motorcycle.Builder builderAt42() {
		return new Player.Motorcycle.Builder().atX(4).atY(2);
	}
	
	@Test
	public void motorcycleCanJumpOverObstacle() throws Exception {
		assertThat(newMotorcycleActingOnRunway(builderAt42().goingAt(3).build(), Player.Action.JUMP).isPathClear()).isTrue();
	}

	@Test
	public void inactiveMotorcycleChangesSpeed() throws Exception {
		Player.Motorcycle inactiveMotorcycle = new Player.Motorcycle.Builder().active(false).build();
		assertThat(inactiveMotorcycle.act(Player.Action.SPEED).getSpeed()).isEqualTo(inactiveMotorcycle.getSpeed() + 1);
	}

	@Test
	public void motorcycleSlowsDown() throws Exception {
		Player.Motorcycle motorcycle = new Player.Motorcycle.Builder().goingAt(1).build();
		assertThat(motorcycle.act(Player.Action.SLOW).getSpeed()).isEqualTo(0);
	}

	@Test
	public void activeMotorcycleChangesXWhenSpeedIs1() throws Exception {
		Player.Motorcycle motorcycle = new Player.Motorcycle.Builder().goingAt(1).build();

		assertThat(motorcycle.act(Player.Action.WAIT).getX()).isEqualTo(motorcycle.getX() + 1);
	}

	@Test
	public void inactiveMotorcycleDoesntChangeXWhenSpeedIs1() throws Exception {
		Player.Motorcycle motorcycle = new Player.Motorcycle.Builder().goingAt(1).active(false).build();

		assertThat(motorcycle.act(Player.Action.JUMP).getX()).isEqualTo(motorcycle.getX());
	}

	@Test
	public void activeMotorcycleDecreasesYWhenGoingUP() throws Exception {
		Player.Motorcycle motorcycle = new Player.Motorcycle.Builder().goingAt(1).atY(1).build();

		assertThat(motorcycle.act(Player.Action.UP).getY()).isEqualTo(0);
	}

	@Test
	public void activeMotorcycleIncreasesYWhenGoingDOWN() throws Exception {
		Player.Motorcycle motorcycle = new Player.Motorcycle.Builder().goingAt(1).atY(1).build();

		assertThat(motorcycle.act(Player.Action.DOWN).getY()).isEqualTo(2);
	}
	
	@Test
	public void goingUpNotAllowedIfMotorcycleOnTheFirstLane() throws Exception {
		assertThat(new Player.MotorcyclesOnRunway(Collections.singleton(
				new Player.Motorcycle.Builder().build()), newRunway(), 2).allowedActions()).doesNotContain(Player.Action.UP);
	}
	
	@Test
	public void goingUpAllowedIfMotorcycleOnTheFirstLaneIsInactive() throws Exception {
		assertThat(new Player.MotorcyclesOnRunway(Collections.singleton(
				new Player.Motorcycle.Builder().active(false).build()), 
				newRunway(), 2).allowedActions()).contains(Player.Action.UP);
	}
	
	@Test
	public void goingDownNotAllowedIfMotorcycleOnTheLastLane() throws Exception {
		assertThat(new Player.MotorcyclesOnRunway(Collections.singleton(
				new Player.Motorcycle.Builder().atY(3).build()), newRunway(), 2).allowedActions())
					.doesNotContain(Player.Action.DOWN);
	}
	
	@Test
	public void solvesSimpleProblem() throws Exception {
		assertThat(new Player.SearchMotorcyclesPath(
				Arrays.asList(new Player.Motorcycle.Builder().goingAt(1).atY(1).build(),
						new Player.Motorcycle.Builder().goingAt(1).atY(2).build()),
				newRunway(), 1).getActionsToGoal().get(0)).isEqualTo(Player.Action.SPEED);
	}
	
	@Test
	public void solvesHardProblem() throws Exception {
		System.out.println(new Player.SearchMotorcyclesPath(
				Arrays.asList(new Player.Motorcycle.Builder().goingAt(1).atY(1).build(),
						new Player.Motorcycle.Builder().goingAt(1).atY(2).build()),
				newRunway(heavyIn), 1).getActionsToGoal());
	}
}

