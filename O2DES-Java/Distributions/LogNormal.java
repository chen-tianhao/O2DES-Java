package O2DESNet.Distributions;

import java.util.Random;

public class LogNormal {
    public static double sample(Random rs, double mean, double cv) {
        if (mean < 0) throw new RuntimeException("Negative mean not applicable");
        if (cv < 0) throw new RuntimeException("Negative coefficient of variation not applicable for log normal distribution");
        if (mean == 0) return 0;
        if (cv == 0) return mean;
        double stddev = cv * mean;
        return org.apache.commons.math3.distribution.LogNormalDistribution.inverseCumulativeProbability(
            rs.nextDouble(), Math.log(mean), Math.log(1 + stddev * stddev / (mean * mean)));
    }

    public static double cdf(double mean, double cv, double x) {
        if (cv == 0) return x >= mean ? 1 : 0;
        if (mean <= 0) throw new RuntimeException("Zero or negative mean not applicable");
        double stddev = cv * mean;
        return org.apache.commons.math3.distribution.LogNormalDistribution.cumulativeProbability(
            x, Math.log(mean), Math.log(1 + stddev * stddev / (mean * mean)));
    }

    public static double invCdf(double mean, double cv, double p) {
        if (cv == 0) return mean;
        if (mean <= 0) throw new RuntimeException("Zero or negative mean not applicable");
        double stddev = cv * mean;
        return org.apache.commons.math3.distribution.LogNormalDistribution.
            inverseCumulativeProbability(p, Math.log(mean), Math.log(1 + stddev * stddev / (mean * mean)));
    }
}
