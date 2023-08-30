package Distributions;

import java.time.Duration;
import java.util.Random;

public class Gamma {
    public static double sample(Random rs, double mean, double cv) {
        if (mean == 0) return 0;
        if (cv == 0) return mean;
        double k = 1 / (cv * cv);
        double lambda = k / mean;
        return org.apache.commons.math3.distribution.GammaDistribution.inverseCumulativeProbability(
            rs.nextDouble(), k, lambda);
    }

    public static double cdf(double mean, double cv, double x) {
        if (cv == 0) return x >= mean ? 1 : 0;
        double k = 1 / (cv * cv);
        double lambda = k / mean;
        return org.apache.commons.math3.distribution.GammaDistribution.cumulativeProbability(
            x, k, lambda);
    }

    public static double invCdf(double mean, double cv, double p) {
        if (cv == 0) return mean;
        double k = 1 / (cv * cv);
        double lambda = k / mean;
        return org.apache.commons.math3.distribution.GammaDistribution.
            inverseCumulativeProbability(p, k, lambda);
    }

    public static Duration sample(Random rs, Duration mean, double cv) {
        return Duration.ofMillis(Math.round(sample(rs, mean.toMillis(), cv)));
    }
}
