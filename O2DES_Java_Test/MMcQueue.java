package O2DES_Java_Test;

import O2DES_Java.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

// You need to import Logger and other necessary classes
// from their respective Java packages

public class MMcQueue extends Sandbox {
    private int seed;
    private double hourlyArrivalRate;
    private double hourlyServiceRate;
    private int capacity;
    private int inQueue;
    private int inService;

    public MMcQueue(double hourlyArrivalRate, double hourlyServiceRate, int capacity, int seed) {
        super(seed);
        this.seed = seed;
        this.hourlyArrivalRate = hourlyArrivalRate;
        this.hourlyServiceRate = hourlyServiceRate;
        this.capacity = capacity;
        this.inQueue = 0;
        this.inService = 0;

        this.schedule(this::arrive, Duration.ofSeconds(0));
    }

    private void arrive() {
        if (inService < capacity) {
            inService++;
            System.out.println(this.getClockTime() + "\tArrive and Start Service (In-Queue: " + inQueue + ", In-Service: " + inService + ")");
            this.schedule(this::depart, Duration.ofMillis(Math.round(-Math.log(1 - new Random(seed).nextDouble()) / hourlyServiceRate * 3600000)));
        } else {
            inQueue++;
            System.out.println(this.getClockTime() + "\tArrive and Join Queue (In-Queue: " + inQueue + ", In-Service: " + inService + ")");
        }
        this.schedule(this::arrive, Duration.ofMillis(Math.round(-Math.log(1 - new Random(seed).nextDouble()) / hourlyArrivalRate * 3600000)));
    }

    private void depart() {
        if (inQueue > 0) {
            inQueue--;
            System.out.println(this.getClockTime() + "\tDepart and Start Service (In-Queue: " + inQueue + ", In-Service: " + inService + ")");
            this.schedule(this::depart, Duration.ofMillis(Math.round(-Math.log(1 - new Random(seed).nextDouble()) / hourlyServiceRate * 3600000)));
        } else {
            inService--;
            System.out.println(this.getClockTime() + "\tDepart (In-Queue: " + inQueue + ", In-Service: " + inService + ")");
        }
    }

    public static void main(String[] args) {
        // Demo 3
        System.out.println("Demo 3 - MMcQueue");
        try (MMcQueue sim1 = new MMcQueue(1, 2, 2, 0)) {
            sim1.run(Duration.ofHours(30));
        }
    }
}
