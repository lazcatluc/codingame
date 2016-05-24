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
		int n = in.nextInt(); // the number of cards for player 1
		Queue<Card> player1 = new LinkedList<>();
		for (int i = 0; i < n; i++) {
			player1.offer(getCard(in.next())); // the n cards of player 1
		}
		int m = in.nextInt(); // the number of cards for player 2
		Queue<Card> player2 = new LinkedList<>();
		for (int i = 0; i < m; i++) {
			player2.offer(getCard(in.next())); // the m cards of player 2
		}

		// Write an action using System.out.println()
		// To debug: System.err.println("Debug messages...");

		System.out.println(new Battle(player1, player2).getResult());
	}
	
	static class Battle {
		private final Queue<Card> player1;
		private final Queue<Card> player2;
		private final Queue<Card> player1PreviousBattleCards;
		private final Queue<Card> player2PreviousBattleCards;
		private final Card player1BattleCard;
		private final Card player2BattleCard;
		private final int round;
		
		public Battle(Collection<Card> player1, Collection<Card> player2) {
			this(player1, player2, new LinkedList<>(), new LinkedList<>(), 0);
		}
		
		private Battle(Collection<Card> player1, Collection<Card> player2, Collection<Card> player1PreviousBattleCards,
				Collection<Card> player2PreviousBattleCards, int previousRound) {
			this.player1 = new LinkedList<>(player1);
			this.player2 = new LinkedList<>(player2);
			this.player1PreviousBattleCards = new LinkedList<>(player1PreviousBattleCards);
			this.player2PreviousBattleCards = new LinkedList<>(player2PreviousBattleCards);
			player1BattleCard = this.player1.poll();
			this.player1PreviousBattleCards.offer(player1BattleCard);
			player2BattleCard = this.player2.poll();
			this.player2PreviousBattleCards.offer(player2BattleCard);
			this.round = previousRound + 1;
		}
		
		public Result getResult() {
			if (player1BattleCard == null || player2BattleCard == null) {
				return pat();
			}
			int cardsBattleScore = new CardComparator().compare(player1BattleCard, player2BattleCard);
			if (cardsBattleScore == 0) {
				if (moveWarCards(player1, player1PreviousBattleCards) &&
					moveWarCards(player2, player2PreviousBattleCards)) {
					return new Battle(player1, player2, player1PreviousBattleCards,
							player2PreviousBattleCards, round - 1).getResult();
				}
				return pat();
			}
			if (cardsBattleScore < 0) {
				if (player1.isEmpty()) {
					return new Result("2 "+round);
				}
				player2.addAll(player1PreviousBattleCards);
				player2.addAll(player2PreviousBattleCards);
				
			}
			if (cardsBattleScore > 0) {
				if (player2.isEmpty()) {
					return new Result("1 "+round);
				}
				player1.addAll(player1PreviousBattleCards);
				player1.addAll(player2PreviousBattleCards);
			}
			player1PreviousBattleCards.clear();
			player2PreviousBattleCards.clear();
			
			return new Battle(player1, player2, player1PreviousBattleCards,
					player2PreviousBattleCards, round).getResult();
		}


		private boolean moveWarCards(Queue<Card> player, Queue<Card> playerPreviousBattleCards) {
			for (int i = 0; i < 3; i++) {
				Card card = player.poll();
				if (card == null) {
					return false;
				}
				playerPreviousBattleCards.add(card);
			}
			return true;
		}
		
		private static Result pat() {
			return new Result("PAT");
		}
	}
	
	static class Result {
		private final String result;

		public Result(String result) {
			this.result = result;
		}

		@Override
		public String toString() {
			return result;
		}
	}

	static Card getCard(String description) {
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
