package Distributions;

import org.apache.commons.math3.distribution.BetaDistribution;
import java.util.Random;

public class Beta {
    public static double sample(Random rs, double mean, double cv) {
        if (mean < 0) throw new IllegalArgumentException("Negative mean not applicable");
        if (cv < 0) throw new IllegalArgumentException("Negative coefficient of variation not applicable for beta distribution");
        if (mean == 0) return 0;
        if (cv == 0) return mean;

        double stddev = cv * mean;
        double a = mean * mean * (1 - mean) / (stddev * stddev) - mean;
        double b = (1 - mean) * (1 - mean) * mean / (stddev * stddev) + mean - 1;

        BetaDistribution betaDistribution = new BetaDistribution(a, b);
        return betaDistribution.sample(rs);
    }

    public static double cdf(double mean, double cv, double x) {
        if (mean <= 0) throw new IllegalArgumentException("Zero or negative mean not applicable");
        if (cv <= 0) throw new IllegalArgumentException("Zero or negative coefficient of variation not applicable for beta distribution");

        double sigma = cv * mean;
        double a = mean * mean * (1 - mean) / (sigma * sigma) - mean;
        double b = (1 - mean) * (1 - mean) * mean / (sigma * sigma) + mean - 1;

        BetaDistribution betaDistribution = new BetaDistribution(a, b);
        return betaDistribution.cumulativeProbability(x);
    }

    public static double invCdf(double mean, double cv, double p) {
        if (mean <= 0) throw new IllegalArgumentException("Zero or negative mean not applicable");
        if (cv <= 0) throw new IllegalArgumentException("Zero or negative coefficient of variation not applicable for beta distribution");

        double sigma = cv * mean;
        double a = mean * mean * (1 - mean) / (sigma * sigma) - mean;
        double b = (1 - mean) * (1 - mean) * mean / (sigma * sigma) + mean - 1;

        BetaDistribution betaDistribution = new BetaDistribution(a, b);
        return betaDistribution.inverseCumulativeProbability(p);
    }
}
