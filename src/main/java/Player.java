import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int n = in.nextInt(); // the number of cards for player 1
		for (int i = 0; i < n; i++) {
			String cardp1 = in.next(); // the n cards of player 1
		}
		int m = in.nextInt(); // the number of cards for player 2
		for (int i = 0; i < m; i++) {
			String cardp2 = in.next(); // the m cards of player 2
		}

		// Write an action using System.out.println()
		// To debug: System.err.println("Debug messages...");

		System.out.println("PAT");
	}

	static Card get(String description) {
		int lastCharacterIndex = description.length() - 1;
		Card.Suit suit = getEnumFromFirstChar(Card.Suit.class, description.charAt(lastCharacterIndex));
		
		try {
			int rank = Integer.parseInt(description.substring(0, lastCharacterIndex));
			return new LowCard(suit, rank);
		}
		catch (NumberFormatException nfe) {
			return new HighCard(suit, getEnumFromFirstChar(Card.Honour.class, description.charAt(0)));
		}
	}
	
	static <T extends Enum<T>> T getEnumFromFirstChar(Class<T> clazz, char firstChar) {
		return Arrays.stream(clazz.getEnumConstants()).filter(
				enumConstant -> enumConstant.toString().charAt(0) == firstChar).findFirst().get();
	}
	
	interface Card {
		enum Suit {
			SPADES, DIAMONDS, CLUBS, HEARTS;
		}
		
		enum Honour {
			JACK, QUEEN, KING, ACE;
		}

		Suit getSuit();
	}

	static class LowCard implements Card {

		private final Suit suit;
		private final int rank;

		public LowCard(Suit suit, int rank) {
			this.suit = suit;
			this.rank = rank;
		}

		@Override
		public Suit getSuit() {
			return suit;
		}

		public int getRank() {
			return rank;
		}

	}
	
	static class HighCard implements Card {
		private final Suit suit;
		private final Honour rank;
		
		public HighCard(Suit suit, Honour rank) {
			this.suit = suit;
			this.rank = rank;
		}

		@Override
		public Suit getSuit() {
			return suit;
		}
		
		public Honour getRank() {
			return rank;
		}
	}
	
	static class CardComparator implements Comparator<Card> {

		@Override
		public int compare(Card o1, Card o2) {
			if (o1 instanceof LowCard) {
				if (o2 instanceof LowCard) {
					return ((LowCard) o1).getRank() - ((LowCard) o2).getRank();
				}
				return -1;
			}
			if (o2 instanceof LowCard) {
				return 1;
			}
			return ((HighCard) o1).getRank().compareTo(((HighCard) o2).getRank());
		}
		
	}
}
