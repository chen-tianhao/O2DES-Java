package O2DESNet.RandomVariables;

import java.util.Random;

public interface ICategoricalRandomVariable<T> extends IRandomVariable<T> { }

public interface IContinuousRandomVariable extends IRandomVariable<Double> { }

public interface IDiscreteRandomVariable extends IRandomVariable<Integer> { }

public interface IRandomVariable<T> {
    double getMean();
    void setMean(double value);

    double getStandardDeviation();
    void setStandardDeviation(double value);

    T sample(Random rs);
}
