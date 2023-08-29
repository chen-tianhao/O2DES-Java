package O2DESNet.RandomVariables.Continuous;

import java.util.Random;

public class Uniform implements IContinuousRandomVariable {

    private double lowerBound = 0d;
    private double upperBound = 1d;
    private double mean = 0.5d;
    private double std = 0.5d;

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double value) {
        if (value > upperBound) upperBound = value;
        lowerBound = value;
        mean = (lowerBound + upperBound) / 2d;
        std = (upperBound - lowerBound) * (upperBound - lowerBound) / 12d;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double value) {
        if (value < lowerBound) lowerBound = value;
        upperBound = value;
        mean = (lowerBound + upperBound) / 2d;
        std = (upperBound - lowerBound) * (upperBound - lowerBound) / 12d;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        throw new IllegalArgumentException("Users not allowed to define continuous uniform random variable by setting mean value");
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        throw new IllegalArgumentException("Users not allowed to define continuous uniform random variable by setting standard deviation value");
    }

    public double sample(Random rs) {
        return lowerBound + (upperBound - lowerBound) * rs.nextDouble();
    }
}
