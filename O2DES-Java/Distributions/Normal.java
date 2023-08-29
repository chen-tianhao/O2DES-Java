package O2DESNet.Distributions;

import java.util.Random;

public class Normal {
    public static double sample(Random rs, double mean, double cv) {
        if (mean < 0) throw new RuntimeException("Negative mean not applicable");
        if (cv < 0) throw new RuntimeException("Negative coefficient of variation not applicable for normal distribution");
        if (mean == 0) return 0;
        if (cv == 0) return mean;
        double stddev = cv * mean;
        return org.apache.commons.math3.distribution.NormalDistribution.inverseCumulativeProbability(
            rs.nextDouble(), mean, stddev);
    }

    public static double cdf(double mean, double cv, double x) {
        if (mean <= 0) throw new RuntimeException("Zero or negative mean not applicable");
        if (cv <= 0) throw new RuntimeException("Zero or negative coefficient of variation not applicable for normal distribution");
        double stddev = cv * mean;
        return org.apache.commons.math3.distribution.NormalDistribution.cumulativeProbability(x, mean, stddev);
    }

    public static double invCdf(double mean, double cv, double p) {
        if (mean <= 0) throw new RuntimeException("Zero or negative mean not applicable");
        if (cv <= 0) throw new RuntimeException("Zero or negative coefficient of variation not applicable for normal distribution");
        double stddev = cv * mean;
        return org.apache.commons.math3.distribution.NormalDistribution.inverseCumulativeProbability(p, mean, stddev);
    }
}
