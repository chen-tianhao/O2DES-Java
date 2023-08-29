package O2DESNet.RandomVariables.Continuous;

import java.util.Random;

public class LogNormal implements IContinuousRandomVariable {

    private double mean = Math.exp(0.5d);
    private double std = Math.sqrt(Math.E * Math.E - Math.E);
    private double cv = Math.sqrt(Math.E - 1);
    private double mu = 0d;
    private double sigma = 1d;

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        if (value <= 0d)
            throw new IllegalArgumentException("None positive mean value is not applicable for beta distribution");

        mean = value;
        mu = Math.log(mean) - 0.5d * Math.log(1d + std * std / mean / mean);
        sigma = Math.sqrt(Math.log(1d + std * std / mean / mean));

        if (value == 0d)
            cv = Double.MAX_VALUE;
        else
            cv = std / mean;
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        if (value < 0d)
            throw new IllegalArgumentException("A negative standard deviation is not applicable");

        std = value;
        mu = Math.log(mean) - 0.5d * Math.log(1d + std * std / mean / mean);
        sigma = Math.sqrt(Math.log(1d + std * std / mean / mean));

        if (mean != 0d)
            cv = std / mean;
    }

    public double getCV() {
        return cv;
    }

    public void setCV(double value) {
        if (value < 0d)
            throw new IllegalArgumentException("A negative coefficient of variation is not applicable for log normal distribution");

        cv = value;
        std = cv * mean;
    }

    public double getMu() {
        return mu;
    }

    public void setMu(double value) {
        mu = value;
        mean = Math.exp(mu + sigma * sigma / 2d);
        std = Math.sqrt((Math.exp(sigma * sigma) - 1) * Math.exp(2d * mu + sigma * sigma));
        cv = std / mean;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double value) {
        if (value < 0)
            throw new IllegalArgumentException("A negative shape parameter is not applicable");

        sigma = value;
        mean = Math.exp(mu + sigma * sigma / 2d);
        std = Math.sqrt((Math.exp(sigma * sigma) - 1d) * Math.exp(2d * mu + sigma * sigma));
        cv = std / mean;
    }

    public double sample(Random rs) {
        return MathNet.Numerics.Distributions.LogNormal.sample(rs, getMu(), getSigma());
    }
}
