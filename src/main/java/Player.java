import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        MyScanner in = new MyCustomScanner(new Scanner(System.in));
        PrintStream out = System.out;
        run(in, out);
    }

    static void run(MyScanner in, PrintStream out) {
        Fleet fleet = new Fleet(100, Comparator.comparing(Truck::getCurrentWeight));
        int boxes = in.nextInt();
        int[] boxTruks = new int[boxes];
        IntStream.range(0, boxes).mapToObj(i -> new Box(i, new BigDecimal(in.next()),
                new BigDecimal(in.next())))
                .sorted(Comparator.comparing(Box::getVolume).reversed())
                .forEach(box -> boxTruks[box.getId()] = fleet.load(box));
        Arrays.stream(boxTruks).forEach(truck -> out.print(truck + " "));
        out.println();
        in.print(System.err);
        fleet.printFirstAndLast(System.err);
    }

    interface MyScanner {
        int nextInt();

        float nextFloat();

        String nextLine();

        String next();

        void print(PrintStream printStream);
    }

    static class Box {
        private final int id;
        private final BigDecimal weight;
        private final BigDecimal volume;

        Box(int id, BigDecimal weight, BigDecimal volume) {
            this.id = id;
            this.weight = weight;
            this.volume = volume;
        }

        public int getId() {
            return id;
        }

        BigDecimal getVolume() {
            return volume;
        }

        BigDecimal getWeight() {
            return weight;
        }

        BigDecimal getDensity() {
            return weight.divide(volume, MathContext.DECIMAL32);
        }
    }

    static class Truck {

        private final int id;
        private final BigDecimal availableVolume;
        private final BigDecimal currentWeight;
        private final BigDecimal density;

        private Truck(int id, BigDecimal availableVolume, BigDecimal currentWeight, BigDecimal density) {
            this.id = id;
            this.availableVolume = availableVolume;
            this.currentWeight = currentWeight;
            this.density = density;
        }

        static Truck empty(int id) {
            return new Truck(id, BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO);
        }

        boolean hasSpaceFor(Box box) {
            return availableVolume.compareTo(box.getVolume()) >= 0;
        }

        Truck load(Box box) {
            BigDecimal newWeight = currentWeight.add(box.getWeight());
            BigDecimal currentVolume = currentWeight.multiply(density);
            BigDecimal newDensity = newWeight.divide(currentVolume.add(box.getVolume()), MathContext.DECIMAL32);
            return new Truck(id, availableVolume.subtract(box.getVolume()), newWeight, newDensity);
        }

        BigDecimal getDensity() {
            return density;
        }

        public BigDecimal getCurrentWeight() {
            return currentWeight;
        }

        int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Truck{" + "id=" + id +
                    ", availableVolume=" + availableVolume +
                    ", currentWeight=" + currentWeight +
                    ", density=" + density +
                    '}';
        }
    }

    static class Fleet {
        private final PriorityQueue<Truck> trucks;

        Fleet(int truckCount, Comparator<Truck> truckComparator) {
            trucks = new PriorityQueue<>(truckComparator);
            IntStream.range(0, truckCount).forEach(i -> trucks.offer(Truck.empty(i)));
        }

        int load(Box box) {
            Iterator<Truck> truckIterator = trucks.iterator();
            while (true) {
                Truck truck = truckIterator.next();
                if (truck.hasSpaceFor(box)) {
                    truckIterator.remove();
                    trucks.offer(truck.load(box));
                    return truck.getId();
                }
            }
        }

        void printFirstAndLast(PrintStream out) {
            List<Truck> currentTrucks = new ArrayList<>(trucks);
            out.print(currentTrucks.get(0) + " -> " + currentTrucks.get(currentTrucks.size() - 1));
        }
    }

    static class MyCustomScanner implements MyScanner {
        private final List<String> lines = new ArrayList<>();
        private final Scanner scanner;

        MyCustomScanner(Scanner scanner) {
            this.scanner = scanner;
        }

        public int nextInt() {
            int nextInt = scanner.nextInt();
            lines.add(String.valueOf(nextInt));
            return nextInt;
        }

        public float nextFloat() {
            float nextFloat = scanner.nextFloat();
            lines.add(String.valueOf(nextFloat));
            return nextFloat;
        }

        public String nextLine() {
            String nextLine = scanner.nextLine();
            lines.add(nextLine);
            return nextLine;
        }

        public String next() {
            String next = scanner.next();
            lines.add(next);
            return next;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            lines.forEach(line -> sb.append(line).append('\n'));
            return sb.toString();
        }

        public void print(PrintStream out) {
            lines.forEach(out::println);
        }
    }

}
