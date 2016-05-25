import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.OptionalDouble;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
	
	private static final int MAX_HORIZONTAL_SPEED = 18;
	private static final int MAX_ALLOWED_HORIZONTAL_SPEED = 20;
	private static final int MAX_VERTICAL_SPEED = 18;
	private static final int MAX_THRUST = 4;
	private static final int MAX_ANGLE = 30;
	private static final int ALTITUDE_MAINTAINED_ABOVE_FLAT_ELEVATION = 300;

	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int surfaceN = in.nextInt(); // the number of points used to draw the surface of Mars.
        FlatSurface.Builder builder = new FlatSurface.Builder();
        for (int i = 0; i < surfaceN; i++) {
            int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            builder.usingPoint(landX, landY);
        }
        FlatSurface flatSurface = builder.build();
        // game loop
        while (true) {
            int landX = in.nextInt();
            int landY = in.nextInt();
            int currentHorizontalSpeed = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            int currentVerticalSpeed = in.nextInt(); // the vertical speed (in m/s), can be negative.
            int fuel = in.nextInt(); // the quantity of remaining fuel in liters.
            int rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            int power = in.nextInt(); // the thrust power (0 to 4).

            LanderStrategy landerStrategy = new SumStrategy(
            				new HighGround(flatSurface, landY, landX),
            				new MoveTowardsFlatSurface(flatSurface, currentHorizontalSpeed, landX),
            				new BreakWhenMovingTooFast(currentHorizontalSpeed),
            				new KeepAltitudeVelocity(currentVerticalSpeed));

            System.out.println(landerStrategy.getAngle()+" "+landerStrategy.getThrust());
        }

    }
	
	interface LanderStrategy {
		int getAngle();
		int getThrust();
	}
	
	static class HighGround implements LanderStrategy {
		private final int flatSurfaceElevation;
		private final int currentElevation;
		private final int distanceToFlat;
		
		public HighGround(FlatSurface flatSurface, int currentElevation, int landX) {
			this.flatSurfaceElevation = flatSurface.getElevation();
			this.currentElevation = currentElevation;
			this.distanceToFlat = flatSurface.getDistanceFrom(landX);
		}

		@Override
		public int getAngle() {
			return 0;
		}

		@Override
		public int getThrust() {
			if (distanceToFlat != 0 && currentElevation < flatSurfaceElevation + ALTITUDE_MAINTAINED_ABOVE_FLAT_ELEVATION) {
				return MAX_THRUST;
			}
			return 0;
		}
		
	}
	
	static class SumStrategy implements LanderStrategy {
		private final Collection<LanderStrategy> strategies;
		
		public SumStrategy(LanderStrategy... strategies) {
			this.strategies = Arrays.asList(strategies);
		}

		private int sum(Function<LanderStrategy, Integer> function) {
			return strategies.stream().map(function).reduce(0, Integer::sum);
		}
		
		@Override
		public int getAngle() {
			int angle = sum(LanderStrategy::getAngle);
			if (angle > MAX_ANGLE) {
				return MAX_ANGLE;
			}
			if (angle < -MAX_ANGLE) {
				return -MAX_ANGLE;
			}
			return angle;
		}

		@Override
		public int getThrust() {
			int thrust = sum(LanderStrategy::getThrust);
			if (thrust > MAX_THRUST) {
				return MAX_THRUST;
			}
			return thrust;
		}
	}
	
	static class BreakWhenMovingTooFast implements LanderStrategy {
		private final int currentHorizontalVelocity;
		private final boolean movingTooFast;
		
		private BreakWhenMovingTooFast(int currentHorizontalVelocity) {
			this.currentHorizontalVelocity = currentHorizontalVelocity;
			movingTooFast = Math.abs(currentHorizontalVelocity) > MAX_ALLOWED_HORIZONTAL_SPEED;
		}

		@Override
		public int getAngle() {
			if (movingTooFast) {
				return Integer.signum(currentHorizontalVelocity) * MAX_ANGLE;
			}
			return 0;
		}

		@Override
		public int getThrust() {
			if (movingTooFast) {
				return MAX_THRUST;
			}
			return 0;
		}
	}
	
	static class MoveTowardsFlatSurface implements LanderStrategy {
		private final int currentHorizontalVelocity;
		private final boolean movingTowardsFlatSurface;
		private final int distanceFromFlat;
		
		public MoveTowardsFlatSurface(FlatSurface flatSurface, int currentHorizontalVelocity, int currentX) {
			this.currentHorizontalVelocity = currentHorizontalVelocity;
			distanceFromFlat = flatSurface.getDistanceFrom(currentX);
			movingTowardsFlatSurface = distanceFromFlat == 0 || distanceFromFlat * currentHorizontalVelocity > 0;
		}
		
		@Override
		public int getAngle() {
			if (movingTooSlowHorizontally()) {
				return -Integer.signum(distanceFromFlat) * MAX_ANGLE;
			}
			return 0;
		}

		@Override
		public int getThrust() {
			if (movingTooSlowHorizontally()) {
				return MAX_THRUST;
			}
			return 0;
		}

		private boolean movingTooSlowHorizontally() {
			return !movingTowardsFlatSurface || Math.abs(currentHorizontalVelocity) < MAX_HORIZONTAL_SPEED;
		}		
	}
	
	static class KeepAltitudeVelocity implements LanderStrategy {
		
		private final int currentVerticalVelocity;
		
		public KeepAltitudeVelocity(int currentVerticalVelocity) {
			this.currentVerticalVelocity = currentVerticalVelocity;
		}

		@Override
		public int getAngle() {
			return 0;
		}

		@Override
		public int getThrust() {
			if (currentVerticalVelocity < -MAX_VERTICAL_SPEED) {
				return MAX_THRUST;
			}
			return 0;
		}
		
	}
	
    static class FlatSurface {
    	private final int start;
    	private final int end;
    	private final int elevation;
    	
    	private FlatSurface(Builder builder) {
    		start = builder.start;
    		end = builder.end;
    		elevation = builder.elevation;
    	}

		public int getDistanceFrom(int x) {
			if (x < start) {
				return start - x;
			}
			if (x > end) {
				return end - x;
			}
			return 0;
		}
		
    	int getElevation() {
    		return elevation;
    	}
    	
    	static class Builder {
    		private int previousX = -1;
    		private int previousY = -1;
    		private int start;
    		private int end;
    		private int elevation;
    		
    		Builder usingPoint(int landX, int landY) {
    			if (landY == previousY)  {
    				start = previousX;
    				end = landX;
    				elevation = landY;
                }
                previousX = landX;
                previousY = landY;
                return this;
    		}
    		
    		FlatSurface build() {
    			return new FlatSurface(this);
    		}
    	}
    }	
}
