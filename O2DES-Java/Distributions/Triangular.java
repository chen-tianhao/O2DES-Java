package Distributions;

import java.util.Random;
import org.apache.commons.math3.distribution.TriangularDistribution;

public class Triangular {
    public static double sample(Random rs, double lower, double upper, double mode) {
        TriangularDistribution distribution = new TriangularDistribution(rs, lower, upper, mode);
        return distribution.sample();
    }

    public static double cdf(double lower, double upper, double mode, double x) {
        TriangularDistribution distribution = new TriangularDistribution(null, lower, upper, mode);
        return distribution.cumulativeProbability(x);
    }

    public static double invCdf(double lower, double upper, double mode, double p) {
        TriangularDistribution distribution = new TriangularDistribution(null, lower, upper, mode);
        return distribution.inverseCumulativeProbability(p);
    }
}
