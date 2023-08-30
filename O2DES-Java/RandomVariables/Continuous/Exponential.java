package RandomVariables.Continuous;

import java.util.Random;

public class Exponential implements IContinuousRandomVariable {

    private double lambda = 1d;
    private double mean = 1d;
    private double std = 1d;

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double value) {
        if (value <= 0d)
            throw new IllegalArgumentException("negative or zero arrival rate not applicable");

        lambda = value;
        mean = 1d / lambda;
        std = mean;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        if (value <= 0d)
            throw new IllegalArgumentException("negative or zero mean value not applicable");

        mean = value;
        std = mean;
        lambda = 1d / mean;
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        if (value <= 0d)
            throw new IllegalArgumentException("negative or zero standard deviation not applicable");

        std = value;
        mean = std;
        lambda = 1d / mean;
    }

    public double sample(Random rs) {
        return O2DESNet.Distributions.Exponential.sample(rs, lambda);
    }
}
