import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        in.print(System.err);
    }

    static class Speed {
        private static final double KPH_DIVIDED_BY_MPS = 10.0 / 36.0;
        private final double metersPerSecond;

        Speed(int kilometersPerHour) {
            this.metersPerSecond = kilometersPerHour * KPH_DIVIDED_BY_MPS;
        }

        double meters(int second) {
            return metersPerSecond * second;
        }

        double seconds(int meters) {
            return meters / metersPerSecond;
        }
    }

    static class TrafficLight {
        private final int metersFromOrigin;
        private final int secondsGreen;

        TrafficLight(int metersFromOrigin, int secondsGreen) {
            this.metersFromOrigin = metersFromOrigin;
            this.secondsGreen = secondsGreen;
        }

        boolean isGreen(Speed speed) {
            return isGreen((int)Math.floor(speed.seconds(metersFromOrigin)));
        }

        boolean isGreen(int second) {
            return (second / secondsGreen) % 2 == 0;
        }
    }

    interface MyScanner {
        int nextInt();
        float nextFloat();
        String nextLine();
        String next();
        void print(PrintStream printStream);
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
