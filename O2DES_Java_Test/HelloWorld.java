package O2DES_Java_Test;

import O2DES_Java.Action;
import O2DES_Java.Sandbox;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class HelloWorld extends Sandbox {
    private int seed;
    private double hourlyArrivalRate;
    private int count;

    public HelloWorld(double hourlyArrivalRate, int seed) {
        super(seed);
        this.seed = seed;
        this.hourlyArrivalRate = hourlyArrivalRate;
        this.count = 0;

        this.schedule(new Action(this::arrive), Duration.ofSeconds(0));
    }

    private void arrive() {
        System.out.println(LocalDateTime.now() + "\tHello World #" + count + "!");
        count++;
        double nextArrivalDelay = -Math.log(1 - new Random(seed).nextDouble()) / hourlyArrivalRate;
        this.schedule(new Action(this::arrive), Duration.ofMillis(Math.round(nextArrivalDelay * 3600000)));
    }

    public static void main(String[] args) {
        // Demo 1
        System.out.println("Demo 1 - Hello world");
        HelloWorld sim1 = new HelloWorld(2, 1);
        sim1.warmUp(LocalDateTime.of(1, 1, 1, 0, 0, 0));
        sim1.run(Duration.ofHours(30));
    }
}
