import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		PhoneEntry<Character> phoneBook = PhoneEntry.top();
		for (int i = 0; i < N; i++) {
			String telephone = in.next();
			List<Character> list = new ArrayList<>();
			for (char ch : telephone.toCharArray()) {
				list.add(ch);
			}
			phoneBook.add(list);
		}

		// Write an action using System.out.println()
		// To debug: System.err.println("Debug messages...");

		// The number of elements (referencing a number) stored in the
		// structure.
		System.out.println(phoneBook.getTotalEntries());
	}

	static class PhoneEntry<T> {
		public static <T> PhoneEntry<T> top() {
			return new PhoneEntry<T>() {
				@Override
				public int getTotalEntries() {
					return super.getTotalEntries() - 1;
				}
			};
		}

		private final Map<T, PhoneEntry<T>> suffixEntries = new HashMap<>();

		private PhoneEntry() {

		}

		public void add(Collection<T> suffixEntry) {
			Queue<T> entries = new LinkedList<>(suffixEntry);
			T first = entries.poll();
			if (first == null) {
				return;
			}
			PhoneEntry<T> entry = suffixEntries.get(first);
			if (entry == null) {
				entry = new PhoneEntry<>();
				suffixEntries.put(first, entry);
			}
			entry.add(entries);
		}

		public int getTotalEntries() {
			int totalSubEntries = suffixEntries.values().stream().map(PhoneEntry::getTotalEntries).reduce(Integer::sum)
					.orElse(0);
			return totalSubEntries + 1;
		}
	}

}
