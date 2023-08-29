import java.util.*;
import java.util.function.Function;

public class ReadOnlyExtensions {

    public static <T> List<T> asReadOnly(HashSet<T> hashSet) {
        return new ArrayList<>(hashSet);
    }

    public static <T1, T2> List<T1> asReadOnly(ICollection<T2> collection, Function<T2, T1> asReadOnly) {
        List<T1> result = new ArrayList<>();
        for (T2 item : collection) {
            result.add(asReadOnly.apply(item));
        }
        return result;
    }

    public static <T1, T2> Map<T1, T2> asReadOnly(Map<T1, T2> dict) {
        return asReadOnly(dict, i -> i);
    }

    public static <T1, T2> Map<T1, List<T2>> asReadOnly(Map<T1, List<T2>> dict) {
        return asReadOnly(dict, list -> Collections.unmodifiableList(list));
    }

    public static <T1, T2> Map<T1, List<T2>> asReadOnly(Map<T1, HashSet<T2>> dict) {
        return asReadOnly(dict, hashSet -> Collections.unmodifiableList(new ArrayList<>(hashSet)));
    }

    public static <T1, T2, T3> Map<T1, T2> asReadOnly(Map<T1, T3> dict, Function<T3, T2> asReadOnly) {
        Map<T1, T2> result = new HashMap<>();
        for (Map.Entry<T1, T3> entry : dict.entrySet()) {
            result.put(entry.getKey(), asReadOnly.apply(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    public static <T1, T2, T3, T4> Map<T1, T2> asReadOnly(Map<T3, T4> dict, Function<T3, T1> keyAsReadOnly, Function<T4, T2> valueAsReadOnly) {
        Map<T1, T2> result = new HashMap<>();
        for (Map.Entry<T3, T4> entry : dict.entrySet()) {
            result.put(keyAsReadOnly.apply(entry.getKey()), valueAsReadOnly.apply(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    public static <T, TKey, TValue> Map<TKey, TValue> toReadOnlyDictionary(Iterable<T> iterable, Function<T, TKey> keySelector, Function<T, TValue> valueSelector) {
        Map<TKey, TValue> result = new HashMap<>();
        for (T item : iterable) {
            result.put(keySelector.apply(item), valueSelector.apply(item));
        }
        return Collections.unmodifiableMap(result);
    }
}
