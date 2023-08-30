package RandomVariables.Categorical;

import java.util.List;
import java.util.Random;

public class Uniform<T> implements ICategoricalRandomVariable<T> {

    private final String notSupported = "Categorical random variable (mean and standard deviation) for Uniform<T> not available";

    private Iterable<T> candidates;

    public Iterable<T> getCandidates() {
        return candidates;
    }

    public void setCandidates(Iterable<T> candidates) {
        this.candidates = candidates;
    }

    @Override
    public double getMean() {
        throw new UnsupportedOperationException(notSupported);
    }

    @Override
    public void setMean(double value) {
        throw new UnsupportedOperationException(notSupported);
    }

    @Override
    public double getStandardDeviation() {
        throw new UnsupportedOperationException(notSupported);
    }

    @Override
    public void setStandardDeviation(double value) {
        throw new UnsupportedOperationException(notSupported);
    }

    @Override
    public T sample(Random rs) {
        if (candidates.iterator().hasNext()) {
            List<T> candidateList = (List<T>) candidates;
            return candidateList.get(rs.nextInt(candidateList.size()));
        }
        return null;
    }
}
