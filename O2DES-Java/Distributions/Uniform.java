package O2DESNet.Distributions;

import java.util.List;
import java.util.Random;

public class Uniform {
    public static double sample(Random rs, double lowerbound, double upperbound) {
        return lowerbound + (upperbound - lowerbound) * rs.nextDouble();
    }

    public static double sample(Random rs, double lowerbound, double upperbound) {
        return lowerbound + (upperbound - lowerbound) * rs.nextDouble();
    }

    public static <T> T sample(Random rs, List<T> candidates) {
        if (candidates.size() == 0) {
            return null;
        }
        return candidates.get(rs.nextInt(candidates.size()));
    }
}
