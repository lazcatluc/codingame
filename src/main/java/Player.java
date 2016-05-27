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
	
	private static final Map<Character, Integer> SCRABBLE_VALUE = new HashMap<>();
	static {
		fillScrabbleValue(1, new char[] {'e','a','i','o','n','r','t','l','s','u'});
		fillScrabbleValue(2, new char[] {'d','g'});
		fillScrabbleValue(3, new char[] {'b','c', 'm','p'});
		fillScrabbleValue(4, new char[] {'f','h','v','w','y'});
		SCRABBLE_VALUE.put('k', 5);
		fillScrabbleValue(8, new char[] {'j','x'});
		fillScrabbleValue(8, new char[] {'q','z'});
	}
	
	static void fillScrabbleValue(Integer value, char[] cs) {
		for (char ch : cs) {
			SCRABBLE_VALUE.put(ch, value);
		}
	}
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int dictionarySize = in.nextInt();
        Dictionary dictionary = new Dictionary();
        in.nextLine();
        
        for (int i = 0; i < dictionarySize; i++) {
            String word = in.nextLine();
            dictionary.add(word);
            
        }
        String letters = in.nextLine();

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(dictionary.findBiggestWord(letters));
    }
	
	static int getScrabbleValue(String word) {
		int value = 0;
		for (char ch : word.toCharArray()) {
			value += SCRABBLE_VALUE.get(ch);
		}
		return value;
	}
	
	static String sortedLetters(String letters) {
		char[] chars = letters.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	
	static boolean matches(String sortedLetters, String entryKey) {
		int i = 0, j = 0;
		while (i < sortedLetters.length() && j < entryKey.length()) {
			if (sortedLetters.charAt(i) < entryKey.charAt(j)) {
				i++;
				continue;
			}
			if (sortedLetters.charAt(i) > entryKey.charAt(j)) {
				return false;
			}
			i++;
			j++;
		}
		return j == entryKey.length();
	}
	
	static class Dictionary {
		private final Map<String, String> dictionary = new LinkedHashMap<>();
		
		public void add(String word) {
			if(word.length() > 7) {
				return;
			}
			
			String sortedLetters = sortedLetters(word);
			if (!dictionary.containsKey(sortedLetters)) {
				dictionary.put(sortedLetters, word);
			}
		}
		
		public String findBiggestWord(String letters) {
			String sortedLetters = sortedLetters(letters);
			String firstMatching = "";
			int max = 0;
			for (Map.Entry<String, String> entry : dictionary.entrySet()) {
				if (matches(sortedLetters, entry.getKey())) {
					int scrabbleValue = getScrabbleValue(entry.getValue());
					if (scrabbleValue > max) {
						max = scrabbleValue;
						firstMatching = entry.getValue();
					}
				}
			}
			return firstMatching;
		}
		
	}
}

