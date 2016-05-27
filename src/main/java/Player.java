import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int giftBearers = in.nextInt();
        int giftPrice = in.nextInt();
        int[] budgets = new int[giftBearers];
        for (int i = 0; i < giftBearers; i++) {
            budgets[i] = in.nextInt();
        }
        int totalBudget = Arrays.stream(budgets).sum();
        if (totalBudget < giftPrice) {
        	System.out.println("IMPOSSIBLE");
        	return;
        }
        Arrays.sort(budgets);
        
        for (int i = 0; i < giftBearers; i++) {
        	int contributors = giftBearers - i;
        	int fairBudget = giftPrice / contributors;
			if (budgets[i] > fairBudget) {
        		System.out.println(fairBudget);
        		giftPrice -= fairBudget;
        		continue;
        	}
			System.out.println(budgets[i]);
			giftPrice -= budgets[i];
        }
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

    }
}

