import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
	
	public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numberOfLines = in.nextInt();
        StringBuilder sb = new StringBuilder();
        in.nextLine();
        for (int i = 0; i < numberOfLines; i++) {
            sb.append(in.nextLine()).append(' ');
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        new Parser(sb.toString()).parse();
    }
	
	interface Element {
		String IDENT = "    ";
		void printWithIdentLevel(int identLevel);
	}
	
	interface PrimitiveOrBlock extends Element {
		
	}
	
	static class Primitive implements PrimitiveOrBlock {
		private final Object object;
		
		private Primitive(Object object) {
			this.object = object;
		}

		@Override
		public String toString() {
			return object + "";
		}

		@Override
		public void printWithIdentLevel(int identLevel) {
			for (int i = 0; i < identLevel; i++) {
				System.out.print(IDENT);
			}
			System.out.print(toString());
		}
		
		
	}
	
	static class KeyValue implements Element {
		private final Element key;
		private final Element element;
		
		public KeyValue(Element key, Element element) {
			this.key = key;
			this.element = element;
		}

		@Override
		public String toString() {
			return key + "=" + element;
		}

		@Override
		public void printWithIdentLevel(int identLevel) {
			key.printWithIdentLevel(identLevel);
			System.out.print("=");
			if (element instanceof Primitive) {
				element.printWithIdentLevel(0);
			}
			else {
				System.out.println();
				element.printWithIdentLevel(identLevel);
			}
		}
		
		
	}

	static class Block implements PrimitiveOrBlock {
		private final List<Element> elements;

		public Block(Collection<Element> elements) {
			this.elements = new ArrayList<>(elements);
		}

		@Override
		public void printWithIdentLevel(int identLevel) {
			for (int i = 0; i < identLevel; i++) {
				System.out.print(IDENT);
			}
			System.out.println("(");
			if (!elements.isEmpty()) {
				for (int i = 0; i < elements.size() - 1; i++) {
					elements.get(i).printWithIdentLevel(identLevel + 1);
					System.out.println(";");
				}
				elements.get(elements.size() - 1).printWithIdentLevel(identLevel + 1);
				System.out.println();
			}
			for (int i = 0; i < identLevel; i++) {
				System.out.print(IDENT);
			}
			System.out.print(")");
		}
		
	}
	
	static class Parser {

		private String stringRepresentation;
		private Map<String, Element> elements = new HashMap<>();
		private final AtomicInteger elementsCount = new AtomicInteger(0);
		
		public Parser(String charSequence) {
			this.stringRepresentation = charSequence;
		}

		public void parse() {
			getTopElement().printWithIdentLevel(0);
		}
		
		private Element getTopElement() {
			stringsToBlocks();
			deleteSpaces();
			fillPrimitives();
			while (fillKeyValue() || fillBlock());
			return getElement(stringRepresentation);
		}
		
		private boolean fillBlock() {
			Matcher blockMatcher = Pattern.compile("\\(((P[0-9]+;)*(P[0-9]+)*)\\)").matcher(stringRepresentation);
			boolean found = false;
			StringBuffer sb = new StringBuffer();
			while (blockMatcher.find()) {
				found = true;
				String[] elementRepresentations = blockMatcher.group(1).split(";");
				List<Element> realElements = Arrays.stream(elementRepresentations).map(this::getElement)
						.filter(element -> !element.toString().isEmpty()).collect(Collectors.toList());
				Block block = new Block(realElements);
				String elementName = "P"+elementsCount.getAndIncrement();
				blockMatcher.appendReplacement(sb, elementName);
				elements.put(elementName, block);
			}
			blockMatcher.appendTail(sb);
			stringRepresentation = sb.toString();
			return found;
		}

		private boolean fillKeyValue() {
			Matcher keyValueMatcher = Pattern.compile("(P[0-9]+)=(P[0-9]+)").matcher(stringRepresentation);
			boolean found = false;
			StringBuffer sb = new StringBuffer();
			while (keyValueMatcher.find()) {
				found = true;
				Element key = getElement(keyValueMatcher.group(1));
				Element value = getElement(keyValueMatcher.group(2));
				KeyValue kv = new KeyValue(key, value);
				String elementName = "P"+elementsCount.getAndIncrement();
				keyValueMatcher.appendReplacement(sb, elementName);
				elements.put(elementName, kv);
			}
			keyValueMatcher.appendTail(sb);
			stringRepresentation = sb.toString();
			return found;
		}

		private void fillPrimitives() {
			Matcher primitiveMatcher = Pattern.compile("([=\\(;])([^P=\\(;\\)]+)").matcher(stringRepresentation);
			StringBuffer sb = new StringBuffer();
			while (primitiveMatcher.find()) {
				String primitive = primitiveMatcher.group(2);
				String elementName = "P"+elementsCount.getAndIncrement();
				Element element = getElement(primitive);
				primitiveMatcher.appendReplacement(sb, primitiveMatcher.group(1) + elementName);
				elements.put(elementName, element);
			}
			primitiveMatcher.appendTail(sb);
			stringRepresentation = sb.toString();
		}

		public void deleteSpaces() {
			stringRepresentation = stringRepresentation.replaceAll("(\\r|\\n|\\s)+", "");
		}
		
		public void stringsToBlocks() {
			StringBuilder sb = new StringBuilder(stringRepresentation);
			do {
				int stringStart = sb.indexOf("'");
				if (stringStart == -1) {
					stringRepresentation = sb.toString();
					return;
				}
				int stringEnd = sb.indexOf("'", stringStart + 1);
				String quotedString = sb.substring(stringStart, stringEnd + 1);
				String elementName = "P"+elementsCount.getAndIncrement();
				Element element = getElement(quotedString);
				sb.replace(stringStart, stringEnd + 1, elementName);
				elements.put(elementName, element);
			}
			while(true);
		}
		
		public Element getElement(String representation) {
			if (representation.equalsIgnoreCase("null")) {
				return new Primitive(null);
			}
			if (representation.equalsIgnoreCase("true")) {
				return new Primitive(Boolean.TRUE);
			}
			if (representation.equalsIgnoreCase("false")) {
				return new Primitive(Boolean.FALSE);
			}
			try {
				long x = Long.parseLong(representation);
				return new Primitive(x);
			}
			catch (NumberFormatException nfe) {
				// expected
			}
			try {
				double d = Double.parseDouble(representation);
				return new Primitive(d);
			}
			catch (NumberFormatException nfe) {
				// expected
			}
			if (representation.isEmpty()) {
				return new Primitive("");
			}
			if (representation.charAt(0) == '\'' && representation.charAt(representation.length() - 1) == '\'') {
				return new Primitive(representation);
			}
			return elements.get(representation);
		}
	}
}

