package O2DESNet.Distributions;

import java.time.Duration;
import java.util.Random;

public class Exponential {
    public static double sample(Random rs, double mean) {
        return org.apache.commons.math3.distribution.ExponentialDistribution.
            inverseCumulativeProbability(1 - rs.nextDouble()) / mean;
    }

    public static double cdf(double mean, double x) {
        return 1 - Math.exp(-x / mean);
    }

    public static double invCdf(double mean, double p) {
        return -mean * Math.log(1 - p);
    }

    public static Duration sample(Random rs, Duration mean) {
        return Duration.ofMillis(Math.round(sample(rs, mean.toMillis())));
    }
}
