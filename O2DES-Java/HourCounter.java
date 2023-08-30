import java.io.*;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;

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
}

interface IHourCounter extends IReadOnlyHourCounter {
    void observeCount(double count, LocalDateTime clockTime);
    void observeChange(double count, LocalDateTime clockTime);
    void pause();
    void pause(LocalDateTime clockTime);
    void resume(LocalDateTime clockTime);
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
    public void close() {
    }
}

public class HourCounter implements IHourCounter, AutoCloseable {
    private ISandbox _sandbox;
    private LocalDateTime _initialTime;
    public LocalDateTime LastTime;
    public double LastCount;
    public double TotalIncrement;
    public double TotalDecrement;
    public double TotalHours;
    private void UpdateToClockTime() {
        if (!LastTime.equals(_sandbox.getClockTime())) {
            ObserveCount(LastCount);
        }
    }
    public double getWorkingTimeRatio() {
        UpdateToClockTime();
        if (LastTime.equals(_initialTime)) {
            return 0;
        }
        return TotalHours / Duration.between(_initialTime, LastTime).toHours();
    }
    public double CumValue;
    public double getAverageCount() {
        UpdateToClockTime();
        if (TotalHours == 0) {
            return LastCount;
        }
        return CumValue / TotalHours;
    }
    public Duration getAverageDuration() {
        UpdateToClockTime();
        double hours = getAverageCount() / DecrementRate;
        if (Double.isNaN(hours) || Double.isInfinite(hours)) {
            hours = 0;
        }
        return Duration.ofHours((long) hours);
    }
    public boolean Paused;

    private HashMap<LocalDateTime, Double> _history;
    public List<Pair<Double, Double>> getHistory() {
        if (!KeepHistory) {
            return null;
        }
        List<Pair<Double, Double>> history = new ArrayList<>();
        for (Map.Entry<LocalDateTime, Double> entry : _history.entrySet()) {
            double timeDiffHours = Duration.between(_initialTime, entry.getKey()).toHours();
            history.add(new Pair<>(timeDiffHours, entry.getValue()));
        }
        history.sort((a, b) -> a.getKey().compareTo(b.getKey()));
        return history;
    }

    public boolean KeepHistory;
    public HourCounter(ISandbox sandbox, boolean keepHistory) {
        Init(sandbox, LocalDateTime.MIN, keepHistory);
    }
    public HourCounter(ISandbox sandbox, LocalDateTime initialTime, boolean keepHistory) {
        Init(sandbox, initialTime, keepHistory);
    }
    private void Init(ISandbox sandbox, LocalDateTime initialTime, boolean keepHistory) {
        _sandbox = sandbox;
        _initialTime = initialTime;
        LastTime = initialTime;
        LastCount = 0;
        TotalIncrement = 0;
        TotalDecrement = 0;
        TotalHours = 0;
        CumValue = 0;
        KeepHistory = keepHistory;
        if (KeepHistory) {
            _history = new HashMap<>();
        }
    }
    public void ObserveCount(double count) {
        LocalDateTime clockTime = _sandbox.getClockTime();
        if (clockTime.isBefore(LastTime)) {
            throw new RuntimeException("Time of new count cannot be earlier than current time.");
        }
        if (!Paused) {
            double hours = Duration.between(LastTime, clockTime).toHours();
            TotalHours += hours;
            CumValue += hours * LastCount;
            if (count > LastCount) {
                TotalIncrement += count - LastCount;
            } else {
                TotalDecrement += LastCount - count;
            }
        }
        LastTime = clockTime;
        LastCount = count;
        if (KeepHistory) {
            _history.put(clockTime, count);
        }
    }
    public void Pause() {
        LocalDateTime clockTime = _sandbox.getClockTime();
        if (Paused) {
            return;
        }
        ObserveCount(LastCount, clockTime);
        Paused = true;
    }
    public void Resume() {
        if (!Paused) {
            return;
        }
        LastTime = _sandbox.getClockTime();
        Paused = false;
    }
    private void CheckClockTime(LocalDateTime clockTime) {
        if (!clockTime.equals(_sandbox.getClockTime())) {
            throw new RuntimeException("ClockTime is not consistent with the Sandbox.");
        }
    }
    public double getIncrementRate() {
        UpdateToClockTime();
        return TotalIncrement / TotalHours;
    }
    public double getDecrementRate() {
        UpdateToClockTime();
        return TotalDecrement / TotalHours;
    }
    private void WarmedUp() {
        _initialTime = _sandbox.getClockTime();
        LastTime = _sandbox.getClockTime();
        TotalIncrement = 0;
        TotalDecrement = 0;
        TotalHours = 0;
        CumValue = 0;
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
                sw.write(String.format("%f,%f\n", TotalHours, LastCount));
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
}
