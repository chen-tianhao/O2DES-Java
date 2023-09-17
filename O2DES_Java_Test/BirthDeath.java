package O2DES_Java_Test;

import O2DES_Java.Action;
import O2DES_Java.Sandbox;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

// You need to import Logger and other necessary classes
// from their respective Java packages

public class BirthDeath extends Sandbox {
    private int seed;
    private double hourlyBirthRate;
    private double hourlyDeathRate;
    private int population;

    public BirthDeath(double hourlyBirthRate, double hourlyDeathRate, int seed) {
        super(seed);
        this.seed = seed;
        this.hourlyBirthRate = hourlyBirthRate;
        this.hourlyDeathRate = hourlyDeathRate;
        this.population = 0;

        this.schedule(new Action(this::birth), Duration.ofSeconds(0));
    }

    private void birth() {
        population++;
        System.out.println(LocalDateTime.now() + "\tBirth (Population: #" + population + "!)");
        // Logger.info(clockTime + "\tBirth (Population: #" + population + "!)");

        double nextBirthDelay = -Math.log(1 - new Random(seed).nextDouble()) / hourlyBirthRate;
        double nextDeathDelay = -Math.log(1 - new Random(seed).nextDouble()) / hourlyDeathRate;

        this.schedule(new Action(this::birth), Duration.ofMillis(Math.round(nextBirthDelay * 3600000)));
        this.schedule(new Action(this::death), Duration.ofMillis(Math.round(nextDeathDelay * 3600000)));
    }

    private void death() {
        population--;
        System.out.println(LocalDateTime.now() + "\tDeath (Population: #" + population + "!)");
        // Logger.info(clockTime + "\tDeath (Population: #" + population + "!)");
    }

    public static void main(String[] args) {
        // Demo 2
        System.out.println("Demo 2 - Birth Death Process");
        BirthDeath sim2 = new BirthDeath(20, 1, 1);
        sim2.warmUp(LocalDateTime.of(1, 1, 1, 0, 0, 0));
        sim2.run(Duration.ofHours(30));
    }
}
