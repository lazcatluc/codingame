import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int surfaceN = in.nextInt(); // the number of points used to draw the surface of Mars.
        int previousX = -1;
        int previousY = -1;
        int flatStart = -1;
        int flatEnd = -1;
        for (int i = 0; i < surfaceN; i++) {
            int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            if (landY == previousY)  {
            	flatStart = previousX;
            	flatEnd = landX;
            }
            previousX = landX;
            previousY = landY;
        }

        // game loop
        while (true) {
            int X = in.nextInt();
            int Y = in.nextInt();
            int hSpeed = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            int vSpeed = in.nextInt(); // the vertical speed (in m/s), can be negative.
            int fuel = in.nextInt(); // the quantity of remaining fuel in liters.
            int rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            int power = in.nextInt(); // the thrust power (0 to 4).

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // rotate power. rotate is the desired rotation angle. power is the desired thrust power.
            System.out.println("-20 3");
        }

    }
	
    static class FlatSurface {
    	private final int start;
    	private final int end;
    	
    	private FlatSurface(Builder builder) {
    		start = builder.start;
    		end = builder.end;
    	}
    	
    	int getStart() {
    		return start;
    	}
    	
    	int getEnd() {
    		return end;
    	}
    	
    	static class Builder {
    		private int previousX = -1;
    		private int previousY = -1;
    		private int start;
    		private int end;
    		
    		Builder usingPoint(int landX, int landY) {
    			if (landY == previousY)  {
    				start = previousX;
    				end = landX;
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
