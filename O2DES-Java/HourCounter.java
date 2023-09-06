import java.io.*;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.*;
import java.util.stream.Collectors;

interface IReadOnlyHourCounter {
    LocalDateTime getLastTime();
    double getLastCount();
    boolean isPaused();
    double getTotalIncrement();
    double getTotalDecrement();
    double getIncrementRate();
    double getDecrementRate();
    double getTotalHours();
    double getWorkingTimeRatio();
    double getCumValue();
    double getAverageCount();
    Duration getAverageDuration();
    String getLogFile();
    void setLogFile(String logFile);
}

interface IHourCounter extends IReadOnlyHourCounter {
    void ObserveCount(double count, LocalDateTime clockTime);
    void ObserveChange(double count, LocalDateTime clockTime);
    void Pause();
    void Pause(LocalDateTime clockTime);
    void Resume(LocalDateTime clockTime);
}

class ReadOnlyHourCounter implements IReadOnlyHourCounter, AutoCloseable {
    private final HourCounter hourCounter;

    ReadOnlyHourCounter(HourCounter hourCounter) {
        this.hourCounter = hourCounter;
    }

    @Override
    public LocalDateTime getLastTime() {
        return hourCounter.getLastTime();
    }

    @Override
    public double getLastCount() {
        return hourCounter.getLastCount();
    }

    @Override
    public boolean isPaused() {
        return hourCounter.isPaused();
    }

    @Override
    public double getTotalIncrement() {
        return hourCounter.getTotalIncrement();
    }

    @Override
    public double getTotalDecrement() {
        return hourCounter.getTotalDecrement();
    }

    @Override
    public double getIncrementRate() {
        return hourCounter.getIncrementRate();
    }

    @Override
    public double getDecrementRate() {
        return hourCounter.getDecrementRate();
    }

    @Override
    public double getTotalHours() {
        return hourCounter.getTotalHours();
    }

    @Override
    public double getWorkingTimeRatio() {
        return hourCounter.getWorkingTimeRatio();
    }

    @Override
    public double getCumValue() {
        return hourCounter.getCumValue();
    }

    @Override
    public double getAverageCount() {
        return hourCounter.getAverageCount();
    }

    @Override
    public Duration getAverageDuration() {
        return hourCounter.getAverageDuration();
    }

    @Override
    public String getLogFile() {
        return hourCounter.getLogFile();
    }

    @Override
    public void setLogFile(String logFile) {
         hourCounter.setLogFile(logFile);
    }

    @Override
    public void close() {
    }
}

public class HourCounter implements IHourCounter, AutoCloseable {
    private ISandbox _sandbox;
    private LocalDateTime _initialTime;
    private LocalDateTime lastTime;
    private double lastCount;
    public boolean Paused;
    private double totalIncrement;
    private double totalDecrement;
    private double totalHours;
    private double cumValue;
    private boolean keepHistory;

    @Override
    public LocalDateTime getLastTime() {
        return lastTime;
    }

    @Override
    public double getLastCount() {
        return lastCount;
    }

    @Override
    public boolean isPaused() {
        return Paused;
    }

    @Override
    public double getTotalIncrement() {
        return totalIncrement;
    }

    @Override
    public double getTotalDecrement() {
        return totalDecrement;
    }

    @Override
    public double getTotalHours() {
        return totalHours;
    }

    public double getIncrementRate() {
        UpdateToClockTime();
        return totalIncrement / totalHours;
    }
    public double getDecrementRate() {
        UpdateToClockTime();
        return totalDecrement / totalHours;
    }

    private void UpdateToClockTime() {
        if (!lastTime.equals(_sandbox.getClockTime())) {
            ObserveCount(lastCount);
        }
    }
    public double getWorkingTimeRatio() {
        UpdateToClockTime();
        if (lastTime.equals(_initialTime)) {
            return 0;
        }
        return totalHours / Duration.between(_initialTime, lastTime).toHours();
    }

    @Override
    public double getCumValue() {
        return cumValue;
    }

    public double getAverageCount() {
        UpdateToClockTime();
        if (totalHours == 0) {
            return lastCount;
        }
        return cumValue / totalHours;
    }
    public Duration getAverageDuration() {
        UpdateToClockTime();
        double hours = getAverageCount() / getDecrementRate();
        if (Double.isNaN(hours) || Double.isInfinite(hours)) {
            hours = 0;
        }
        return Duration.ofHours((long) hours);
    }

    private HashMap<LocalDateTime, Double> _history;
    public List<Map.Entry<Double, Double>> getHistory() {
        if (!keepHistory) {
            return null;
        }
        List<Map.Entry<Double, Double>> history = _history.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    double hoursDifference = Duration.between(_initialTime, entry.getKey()).toHours();
                    return new AbstractMap.SimpleEntry<>(hoursDifference, entry.getValue());
                })
                .collect(Collectors.toList());
        return history;
    }

    public HourCounter(ISandbox sandbox, boolean keepHistory) {
        Init(sandbox, LocalDateTime.MIN, keepHistory);
    }
    public HourCounter(ISandbox sandbox, LocalDateTime initialTime, boolean keepHistory) {
        Init(sandbox, initialTime, keepHistory);
    }
    private void Init(ISandbox sandbox, LocalDateTime initialTime, boolean keepHistory) {
        _sandbox = sandbox;
        _initialTime = initialTime;
        lastTime = initialTime;
        lastCount = 0;
        totalIncrement = 0;
        totalDecrement = 0;
        totalHours = 0;
        cumValue = 0;
        this.keepHistory = keepHistory;
        if (this.keepHistory) {
            _history = new HashMap<>();
        }
    }
    public void ObserveCount(double count) {
        LocalDateTime clockTime = _sandbox.getClockTime();
        if (clockTime.isBefore(lastTime)) {
            throw new RuntimeException("Time of new count cannot be earlier than current time.");
        }
        if (!Paused) {
            double hours = Duration.between(lastTime, clockTime).toHours();
            totalHours += hours;
            cumValue += hours * lastCount;
            if (count > lastCount) {
                totalIncrement += count - lastCount;
            } else {
                totalDecrement += lastCount - count;
            }
        }
        lastTime = clockTime;
        lastCount = count;
        if (keepHistory) {
            _history.put(clockTime, count);
        }
    }
    public void Pause() {
        LocalDateTime clockTime = _sandbox.getClockTime();
        if (Paused) {
            return;
        }
        ObserveCount(lastCount, clockTime);
        Paused = true;
    }

    @Override
    public void Pause(LocalDateTime clockTime) {

    }

    @Override
    public void Resume(LocalDateTime clockTime) {

    }

    public void Resume() {
        if (!Paused) {
            return;
        }
        lastTime = _sandbox.getClockTime();
        Paused = false;
    }
    private void CheckClockTime(LocalDateTime clockTime) {
        if (!clockTime.equals(_sandbox.getClockTime())) {
            throw new RuntimeException("ClockTime is not consistent with the Sandbox.");
        }
    }

    void warmedUp() {
        _initialTime = _sandbox.getClockTime();
        lastTime = _sandbox.getClockTime();
        totalIncrement = 0;
        totalDecrement = 0;
        totalHours = 0;
        cumValue = 0;
        HoursForCount = new HashMap<>();
    }
    public HashMap<Double, Double> HoursForCount = new HashMap<>();
    private void SortHoursForCount() {
        List<Map.Entry<Double, Double>> list = new ArrayList<>(HoursForCount.entrySet());
        list.sort(Map.Entry.comparingByKey());
        HoursForCount.clear();
        for (Map.Entry<Double, Double> entry : list) {
            HoursForCount.put(entry.getKey(), entry.getValue());
        }
    }
    public double Percentile(double ratio) {
        SortHoursForCount();
        double threshold = HoursForCount.values().stream().mapToDouble(Double::doubleValue).sum() * ratio / 100;
        for (Map.Entry<Double, Double> entry : HoursForCount.entrySet()) {
            threshold -= entry.getValue();
            if (threshold <= 0) {
                return entry.getKey();
            }
        }
        return Double.POSITIVE_INFINITY;
    }
    public HashMap<Double, double[]> Histogram(double countInterval) {
        SortHoursForCount();
        HashMap<Double, double[]> histogram = new HashMap<>();
        if (!HoursForCount.isEmpty()) {
            double countLb = 0;
            double cumHours = 0;
            for (Map.Entry<Double, Double> entry : HoursForCount.entrySet()) {
                if (entry.getKey() > countLb + countInterval || entry.equals(HoursForCount.entrySet().stream().reduce((first, second) -> second).orElse(null))) {
                    if (cumHours > 0) {
                        histogram.put(countLb, new double[]{cumHours, 0, 0});
                    }
                    countLb += countInterval;
                    cumHours = entry.getValue();
                } else {
                    cumHours += entry.getValue();
                }
            }
        }
        double sum = histogram.values().stream().mapToDouble(arr -> arr[0]).sum();
        double cum = 0;
        for (Map.Entry<Double, double[]> entry : histogram.entrySet()) {
            cum += entry.getValue()[0];
            entry.getValue()[1] = entry.getValue()[0] / sum; // probability
            entry.getValue()[2] = cum / sum; // cumulative probability
        }
        return histogram;
    }

    private String _logFile;
    public String getLogFile() {
        return _logFile;
    }
    public void setLogFile(String value) {
        _logFile = value;
        if (_logFile != null) {
            try (BufferedWriter sw = new BufferedWriter(new FileWriter(_logFile))) {
                sw.write("Hours,Count,Remark\n");
                sw.write(String.format("%f,%f\n", totalHours, lastCount));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private ReadOnlyHourCounter ReadOnly = null;
    public ReadOnlyHourCounter AsReadOnly() {
        if (ReadOnly == null) {
            ReadOnly = new ReadOnlyHourCounter(this);
        }
        return ReadOnly;
    }

    @Override
    public void close() { }

    @Override
    public void ObserveCount(double count, LocalDateTime clockTime) {

    }

    @Override
    public void ObserveChange(double count, LocalDateTime clockTime) {

    }

    @Override
    public void Oause() {

    }

    @Override
    public void Oause(LocalDateTime clockTime) {

    }

    @Override
    public void Oesume(LocalDateTime clockTime) {

    }
}
