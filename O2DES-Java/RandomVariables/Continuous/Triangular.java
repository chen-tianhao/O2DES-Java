package RandomVariables.Continuous;

import java.util.Random;

public class Triangular implements IContinuousRandomVariable {

    private double lowerBound = 0d;
    private double upperBound = 1d;
    private double mode = 0.5d;
    private double mean = 0.5d;
    private double std = Math.sqrt(0.75d / 18d);

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double value) {
        if (value > upperBound) upperBound = value;
        if (value > mode) mode = value;
        lowerBound = value;
        mean = (lowerBound + upperBound + mode) / 3d;
        std = Math.sqrt(((lowerBound) * (lowerBound) +
                          (upperBound) * (upperBound) +
                          (mode) * (mode) -
                          (lowerBound) * (upperBound) -
                          (lowerBound) * (mode) -
                          (upperBound) * (mode)) / 18d);
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double value) {
        if (value < lowerBound) lowerBound = value;
        if (value < mode) mode = value;
        upperBound = value;
        mean = (lowerBound + upperBound + mode) / 3d;
        std = Math.sqrt(((lowerBound) * (lowerBound) +
                          (upperBound) * (upperBound) +
                          (mode) * (mode) -
                          (lowerBound) * (upperBound) -
                          (lowerBound) * (mode) -
                          (upperBound) * (mode)) / 18d);
    }

    public double getMode() {
        return mode;
    }

    public void setMode(double value) {
        if (value < lowerBound) lowerBound = value;
        if (value > upperBound) upperBound = value;
        mode = value;
        mean = (lowerBound + upperBound + mode) / 3d;
        std = Math.sqrt(((lowerBound) * (lowerBound) +
                          (upperBound) * (upperBound) +
                          (mode) * (mode) -
                          (lowerBound) * (upperBound) -
                          (lowerBound) * (mode) -
                          (upperBound) * (mode)) / 18d);
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        throw new IllegalArgumentException("Users not allowed to define triangular random variable by setting mean value");
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        throw new IllegalArgumentException("Users not allowed to define triangular random variable by setting standard deviation value");
    }

    public double sample(Random rs) {
        return MathNet.Numerics.Distributions.Triangular.sample(rs, lowerBound, upperBound, mode);
    }
}
