package RandomVariables.Discrete;

import java.util.Random;

public class Poisson implements IDiscreteRandomVariable {

    private double lambda = 1d;
    private double mean = 1d;
    private double std = 1d;

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double value) {
        if (value < 0)
            throw new IllegalArgumentException("A negative lambda (arrival rate) is not applicable");

        lambda = value;
        mean = value;
        std = Math.sqrt(value);
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        if (value < 0)
            throw new IllegalArgumentException("A negative mean value is not applicable");

        mean = value;
        lambda = value;
        std = Math.sqrt(value);
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        if (value < 0)
            throw new IllegalArgumentException("A negative standard deviation is not applicable");

        std = value;
        mean = value * value;
        lambda = value * value;
    }

    public int sample(Random rs) {
        return MathNet.Numerics.Distributions.Poisson.Sample(rs, lambda);
    }
}
