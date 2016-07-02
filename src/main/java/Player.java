import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int sequencesCount = in.nextInt();
		Set<String> sequences = new HashSet<>();
		for (int i = 0; i < sequencesCount; i++) {
			sequences.add(in.next());
		}

		// Write an action using System.out.println()
		// To debug: System.err.println("Debug messages...");

		System.out.println(getSmallestSequenceLength(sequences));
    }

	public static int getSmallestSequenceLength(Set<String> sequences) {
		OverlapperCache cache = new OverlapperCache();
		int maxOverlap = generatePermutations(sequences).stream().map(list -> getOverlapCount(list, cache))
				.max(Integer::compare).get();
		int lengthSum = sequences.stream().map(String::length).reduce(0, Integer::sum);

		return lengthSum - maxOverlap;
	}

	public static int getOverlapCount(List<String> sequence, OverlapperCache cache) {
		int overlapCount = 0;
		for (int i = 0; i < sequence.size() - 1; i++) {
			overlapCount += cache.get(sequence.get(i), sequence.get(i + 1)).getOverlapCount();
		}
		return overlapCount;
	}

	public static Set<List<String>> generatePermutations(Set<String> originalSet) {
		if (originalSet.isEmpty()) {
			return Collections.singleton(new ArrayList<>());
		}
		return originalSet.stream().flatMap(element -> {
			Set<String> withoutElement = new HashSet<>(originalSet);
			withoutElement.remove(element);
			Set<List<String>> permutations = generatePermutations(withoutElement);
			permutations.forEach(list -> list.add(element));
			return permutations.stream();
		}).collect(Collectors.toSet());
	}

	static class OverlapperCache {
		private Map<String, Map<String, Overlapper>> overlappers = new HashMap<>();

		public Overlapper get(String first, String second) {
			Map<String, Overlapper> cache = overlappers.get(first);
			if (cache == null) {
				cache = new HashMap<>();
				overlappers.put(first, cache);
			}
			Overlapper overlapper = cache.get(second);
			if (overlapper == null) {
				overlapper = new Overlapper(first, second);
				cache.put(second, overlapper);
			}
			return overlapper;
		}
	}

	static class Overlapper {
		private final String first;
		private final String second;
		private final int overlapCount;

		private Overlapper(String first, String second) {
			this.first = first;
			this.second = second;
			this.overlapCount = overlapCount();
		}

		public int getOverlapCount() {
			return overlapCount;
		}

		private int overlapCount() {
			for (int i = first.length(); i > 0; i--) {
				String substring = first.substring(first.length() - i, first.length());
				if (second.startsWith(substring)) {
					return i;
				}
				if (substring.startsWith(second)) {
					return second.length();
				}
			}
			return 0;
		}
	}

}
