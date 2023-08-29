package O2DESNet.Distributions;

import java.util.Random;

public class Poisson {
    public static int sample(Random rs, double lambda) {
        return org.apache.commons.math3.distribution.PoissonDistribution.sample(lambda, rs);
    }

    public static double cdf(double lambda, double x) {
        return org.apache.commons.math3.distribution.PoissonDistribution.cumulativeProbability((int) x, lambda);
    }
}
