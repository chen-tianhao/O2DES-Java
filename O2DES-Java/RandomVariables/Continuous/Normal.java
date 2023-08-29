package O2DESNet.RandomVariables.Continuous;

import java.util.Random;

public class Normal implements IContinuousRandomVariable {

    private double mean = 1d;
    private double std = 1d;
    private double cv = 1d;

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        mean = value;

        if (value == 0d)
            cv = Double.MAX_VALUE;
        else
            cv = std / Math.abs(mean);
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        if (value < 0d)
            throw new IllegalArgumentException("A negative standard deviation is not applicable");

        std = value;
        if (mean != 0d)
            cv = std / Math.abs(mean);
    }

    public double getCV() {
        return cv;
    }

    public void setCV(double value) {
        if (value < 0d)
            throw new IllegalArgumentException("A negative coefficient of variation is not applicable");

        cv = value;
        std = cv * Math.abs(mean);
    }

    public double sample(Random rs) {
        if (cv == 0d) return mean;
        return MathNet.Numerics.Distributions.Normal.sample(rs, mean, std);
    }
}
