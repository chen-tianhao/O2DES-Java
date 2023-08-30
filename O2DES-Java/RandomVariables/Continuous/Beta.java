package RandomVariables.Continuous;

import java.util.Random;

public class Beta implements IContinuousRandomVariable {

    private double mean = 0.5d;
    private double cv = Math.sqrt(3d) / 3d;   // value: 0.57735026918962573105...
    private double std = Math.sqrt(1d / 12d); // value: 0.28867513459481286552...
    private double alpha = 1d;
    private double beta = 1d;

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("None positive mean value not applicable for beta distribution");

        if (value > 1)
            throw new IllegalArgumentException("Mean value of beta distribution should not exceed 1 (one)");

        mean = value;
        cv = std / mean;
        double alphaTemp = mean * mean * (1d - mean) / std / std - mean;
        double betaTemp = (1d - mean) * (1d - mean) * mean / std / std + mean - 1d;

        if (alphaTemp > 0d)
            alpha = alphaTemp;
        else
            throw new IllegalArgumentException("The setting of mean and standard deviation will derive illegal alpha value");

        if (betaTemp > 0d)
            beta = betaTemp;
        else
            throw new IllegalArgumentException("The setting of mean and standard deviation will derive illegal alpha value");
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        if (value < 0d)
            throw new IllegalArgumentException("Negative standard deviation not applicable");

        std = value;
        cv = std / mean;
        double alphaTemp = mean * mean * (1d - mean) / std / std - mean;
        double betaTemp = (1 - mean) * (1d - mean) * mean / std / std + mean - 1d;

        if (alphaTemp > 0d)
            alpha = alphaTemp;
        else
            throw new IllegalArgumentException("The setting of mean and standard deviation will derive illegal alpha value");

        if (betaTemp > 0d)
            beta = betaTemp;
        else
            throw new IllegalArgumentException("The setting of mean and standard deviation will derive illegal alpha value");
    }

    public double getCV() {
        return cv;
    }

    public void setCV(double value) {
        if (value < 0d)
            throw new IllegalArgumentException("Negative coefficient variation not applicable");

        cv = value;
        std = cv * mean;
    }

    public double getAlphaValue() {
        return alpha;
    }

    public void setAlphaValue(double value) {
        if (value <= 0d)
            throw new IllegalArgumentException("A negative or zero alpha value not applicable");

        alpha = value;
        mean = alpha / (alpha + beta);
        std = Math.sqrt(alpha * beta / (alpha + beta) * (alpha + beta) / (alpha + beta + 1));
        cv = std / mean;
    }

    public double getBetaValue() {
        return beta;
    }

    public void setBetaValue(double value) {
        if (value <= 0d)
            throw new IllegalArgumentException("Negative or zero beta value not applicable");

        beta = value;
        mean = alpha / (alpha + beta);
        std = Math.sqrt(alpha * beta / (alpha + beta) * (alpha + beta) / (alpha + beta + 1));
        cv = std / mean;
    }

    public double sample(Random rs) {
        if (cv == 0d) return mean;
        return O2DESNet.Distributions.Beta.sample(rs, alpha, beta);
    }
}
