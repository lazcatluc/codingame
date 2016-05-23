import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
    	Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // width of the building.
        int height = in.nextInt(); // height of the building.
        BombLocator bombLocator = new BombLocator(width, height);
        int turns = in.nextInt(); // maximum number of turns before game over.
        int startX = in.nextInt();
        int startY = in.nextInt();
        BombLocator.Jumper jumper = bombLocator.from(startX, startY);
        // game loop
        while (true) {
            String bombDir = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)
            bombLocator = jumper.to(bombDir);
            jumper = bombLocator.jumpToCenter();
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // the location of the next window Batman should jump to.
            System.out.println(bombLocator.getCenter());
        }
    }
    
    static class BombLocator {
    	
    	private final CoordinateLocator x;
    	private final CoordinateLocator y;
    	
    	private final Map<String, BiFunction<Integer, Integer, BombLocator>> bombLocators = new HashMap<>();
    	{
    		bombLocators.put("U", this::up);
    		bombLocators.put("D", this::down);
    		bombLocators.put("L", this::left);
    		bombLocators.put("R", this::right);
    		
    		bombLocators.put("UL", this::upLeft);
    		bombLocators.put("DL", this::downLeft);
    		bombLocators.put("UR", this::upRight);
    		bombLocators.put("DR", this::downRight);
    	}
    	
    	public BombLocator(int width, int height) {
    		x = new CoordinateLocator(0, width);
    		y = new CoordinateLocator(0, height);		
    	}

    	private BombLocator(CoordinateLocator x, CoordinateLocator y) {
    		this.x = x;
    		this.y = y;
    	}
    	
    	private BombLocator right(int newX, int newY) {
    		return new BombLocator(right(newX), stable(newY));
    	}
    	
    	private BombLocator left(int newX, int newY) {
    		return new BombLocator(left(newX), stable(newY));
    	}

    	private BombLocator up(int newX, int newY) {
    		return new BombLocator(stable(newX), up(newY));
    	}
    	
    	private BombLocator down(int newX, int newY) {
    		return new BombLocator(stable(newX), down(newY));
    	}
    	
    	private BombLocator upRight(int newX, int newY) {
    		return new BombLocator(right(newX), up(newY));
    	}
    	
    	private BombLocator upLeft(int newX, int newY) {
    		return new BombLocator(left(newX), up(newY));
    	}
    	
    	private BombLocator downRight(int newX, int newY) {
    		return new BombLocator(right(newX), down(newY));
    	}
    	
    	private BombLocator downLeft(int newX, int newY) {
    		return new BombLocator(left(newX), down(newY));
    	}
    	
    	private CoordinateLocator down(int newY) {
    		return new CoordinateLocator(newY + 1, y.max);
    	}

		private CoordinateLocator right(int newX) {
			return new CoordinateLocator(newX + 1, x.max);
		}    	

		private CoordinateLocator left(int newX) {
			return new CoordinateLocator(x.min, newX - 1);
		}

		private CoordinateLocator up(int newY) {
			return new CoordinateLocator(y.min, newY - 1);
		}		

		private CoordinateLocator stable(int newY) {
			return new CoordinateLocator(newY, newY);
		}	
		
    	public Jumper from(int x0, int y0) {
    		return new Jumper(x0, y0);
    	}
    	
    	public Jumper jumpToCenter() {
    		return new Jumper(x.getCenter(), y.getCenter());
    	}
    	
    	public String getCenter() {
    		return x.getCenter() + " " + y.getCenter();
    	}
    	
    	class Jumper {
    		private final int fromX;
    		private final int fromY;
    		
    		public Jumper(int fromX, int fromY) {
    			this.fromX = fromX;
    			this.fromY = fromY;
    		}
    		
    		public BombLocator to(String direction) {
    			return bombLocators.get(direction).apply(fromX, fromY);
    		}
    	}
    			
    	static class CoordinateLocator {
    		private final int min;
    		private final int max;
    		
    		public CoordinateLocator(int min, int max) {
    			this.min = min;
    			this.max = max;
    		}

			public int getCenter() {
				return min + max >>> 1;
			}
    	}
    }
}
