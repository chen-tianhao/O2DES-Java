package RandomVariables.Continuous;

import java.util.Random;

public class Gamma implements IContinuousRandomVariable {

    private double mean = 1;
    private double std = 1;
    private double cv = 1;
    private double alpha = 1;
    private double beta = 1;

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("None positive mean value not applicable for beta distribution");

        mean = value;
        cv = std / mean;
        alpha = mean * mean / std / std;
        beta = mean / std / std;
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        if (value < 0)
            throw new IllegalArgumentException("A negative standard deviation not applicable");

        std = value;
        cv = std / mean;
        alpha = mean * mean / std / std;
        beta = mean / std / std;
    }

    private double getCV() {
        return cv;
    }

    private void setCV(double value) {
        if (value < 0)
            throw new IllegalArgumentException("A negative coefficient variation not applicable");

        cv = value;
        std = cv * mean;
        alpha = 1 / cv / cv;
        beta = mean / std / std;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("A negative or zero alpha value not applicable");

        alpha = value;
        mean = alpha / beta;
        std = Math.sqrt(alpha / (beta * beta));
        cv = std / mean;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("A negative or zero beta value not applicable");

        beta = value;
        mean = alpha / beta;
        std = Math.sqrt(alpha / (beta * beta));
        cv = std / mean;
    }

    public double sample(Random rs) {
        if (getMean() == 0) return 0;
        if (getCV() == 0) return getMean();
        return O2DESNet.Distributions.Gamma.sample(rs, getAlpha(), getBeta());
    }
}
