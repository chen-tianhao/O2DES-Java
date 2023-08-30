package Distributions;

import java.util.Map;
import java.util.Random;
import java.util.List;

public class Empirical {
    public static int sample(Random rs, List<Double> ratios) {
        double threshold = rs.nextDouble() * ratios.stream().mapToDouble(Double::doubleValue).sum();
        for (int i = 0; i < ratios.size(); i++) {
            double v = ratios.get(i);
            if (threshold < v) {
                return i;
            }
            threshold -= v;
        }
        return -1;
    }

    public static <T> T sample(Random rs, Map<T, Double> ratios) {
        int index = sample(rs, ratios.values());
        return ratios.keySet().stream().skip(index).findFirst().orElse(null);
    }
}
