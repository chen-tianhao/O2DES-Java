package RandomVariables.Discrete;

import java.util.Random;

public class Uniform implements IDiscreteRandomVariable {

    private int lowerBound = 0;
    private int upperBound = 1;
    private double mean = 0.5d;
    private double std = 0.5d;
    private boolean includeBound = true;

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int value) {
        lowerBound = value;
        if (value > upperBound) {
            upperBound = value;
            mean = value;
            std = 0d;
        } else {
            double tempSquareSum = 0d;
            mean = (lowerBound + upperBound) / 2d;
            double n = upperBound - lowerBound + 1d;

            if (includeBound) {
                for (int i = lowerBound; i <= upperBound; i++)
                    tempSquareSum += (i - mean) * (i - mean);
                std = Math.sqrt(tempSquareSum / n);
            } else {
                if (upperBound - lowerBound <= 1)
                    throw new IllegalArgumentException("Nothing between lower bound and upper bound if IncludeBound property is set to 'false'");

                for (int i = lowerBound + 1; i <= upperBound - 1; i++)
                    tempSquareSum += (i - mean) * (i - mean);
                std = Math.sqrt(tempSquareSum / n);
            }
        }
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int value) {
        upperBound = value;
        if (value < lowerBound) {
            lowerBound = value;
            mean = value;
            std = 0d;
        } else {
            mean = (lowerBound + upperBound) / 2d;
            double tempSquareSum = 0d;
            double n = upperBound - lowerBound + 1d;

            if (includeBound) {
                for (int i = lowerBound; i <= upperBound; i++)
                    tempSquareSum += (i - mean) * (i - mean);
                std = Math.sqrt(tempSquareSum / n);
            } else {
                if (upperBound - lowerBound <= 1)
                    throw new IllegalArgumentException("Nothing between lower bound and upper bound if IncludeBound property is set to 'false'");

                for (int i = lowerBound + 1; i <= upperBound - 1; i++)
                    tempSquareSum += (i - mean) * (i - mean);
                std = Math.sqrt(tempSquareSum / n);
            }
        }
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double value) {
        throw new IllegalArgumentException("Users not allowed to define discrete uniform random variable by setting mean value");
    }

    public double getStandardDeviation() {
        return std;
    }

    public void setStandardDeviation(double value) {
        throw new IllegalArgumentException("Users not allowed to define discrete uniform random variable by setting standard deviation value");
    }

    public boolean isIncludeBound() {
        return includeBound;
    }

    public void setIncludeBound(boolean value) {
        includeBound = value;
    }

    public int sample(Random rs) {
        int temp;
        if (includeBound) {
            int dummyLowerBound = lowerBound - 1;
            int dummyUpperBound = upperBound + 1;
            temp = dummyLowerBound;
            while (temp == dummyLowerBound || temp == dummyUpperBound) {
                temp = Math.toIntExact(Math.round(dummyLowerBound + (dummyUpperBound - dummyLowerBound) * rs.nextDouble()));
            }
        } else {
            temp = lowerBound;
            while (temp == lowerBound || temp == upperBound) {
                temp = Math.toIntExact(Math.round(lowerBound + (upperBound - lowerBound) * rs.nextDouble()));
            }
        }
        return temp;
    }
}
