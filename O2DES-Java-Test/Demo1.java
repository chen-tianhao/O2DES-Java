import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

abstract class Sandbox {
    protected LocalDateTime clockTime;

    protected void schedule(Runnable task, Duration delay) {
        // Implement scheduling logic here
    }

    protected void advanceClock(Duration time) {
        // Implement clock advancement logic here
    }
}

public class HelloWorld extends Sandbox {
    private int seed;
    private double hourlyArrivalRate;
    private int count;

    public HelloWorld(double hourlyArrivalRate, int seed) {
        super();
        this.seed = seed;
        this.hourlyArrivalRate = hourlyArrivalRate;
        this.count = 0;

        this.schedule(this::arrive, Duration.ofSeconds(0));
    }

    private void arrive() {
        System.out.println(clockTime + "\tHello World #" + count + "!");
        count++;
        double nextArrivalDelay = -Math.log(1 - new Random(seed).nextDouble()) / hourlyArrivalRate;
        this.schedule(this::arrive, Duration.ofMillis(Math.round(nextArrivalDelay * 3600000)));
    }

    public static void main(String[] args) {
        // Demo 1
        System.out.println("Demo 1 - Hello world");
        HelloWorld sim1 = new HelloWorld(2, 1);
        sim1.warmup(LocalDateTime.of(1, 1, 1, 0, 0, 0));
        sim1.run(Duration.ofHours(30));
    }
}
