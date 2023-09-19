package O2DES_Java;

import java.io.*;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private boolean Paused;
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

    /**
     * @return Total number of increment observed
     */
    @Override
    public double getTotalIncrement() {
        return totalIncrement;
    }

    /**
     * @return Total number of decrement observed
     */
    @Override
    public double getTotalDecrement() {
        return totalDecrement;
    }

    /**
     * @return Total number of hours since the initial time.
     */
    @Override
    public double getTotalHours() {
        return totalHours;
    }

    /**
     * @return The cumulative count value (integral) on time in unit of hours
     */
    @Override
    public double getCumValue() {
        return cumValue;
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
            try {
                observeCount(lastCount);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public double getWorkingTimeRatio() {
        UpdateToClockTime();
        if (lastTime.equals(_initialTime)) {
            return 0;
        }
        return totalHours / Duration.between(_initialTime, lastTime).toHours();
    }

    /**
     * @return The average count on observation period
     */
    public double getAverageCount() {
        UpdateToClockTime();
        if (totalHours == 0) {
            return lastCount;
        }
        return cumValue / totalHours;
    }

    /**
     * @return Average timespan that a load stays in the activity, if it is a stationary process,
     *         i.e., decrement rate == increment rate
     *         It is 0 at the initial status, i.e., decrement rate is NaN (no decrement observed).
     */
    public Duration getAverageDuration() {
        UpdateToClockTime();
        double hours = getAverageCount() / getDecrementRate();
        if (Double.isNaN(hours) || Double.isInfinite(hours)) {
            hours = 0;
        }
        return Duration.ofHours((long) hours);
    }

    private HashMap<LocalDateTime, Double> _history;

    /**
     * @return Scatter points of (time in hours, count)
     */
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
        init(sandbox, LocalDateTime.MIN, keepHistory);
    }
    public HourCounter(ISandbox sandbox, LocalDateTime initialTime, boolean keepHistory) {
        init(sandbox, initialTime, keepHistory);
    }
    private void init(ISandbox sandbox, LocalDateTime initialTime, boolean keepHistory) {
        _sandbox = sandbox;
        _initialTime = initialTime;
        lastTime = initialTime;
        lastCount = 0;
        totalIncrement = 0;
        totalDecrement = 0;
        totalHours = 0;
        cumValue = 0;
        this.keepHistory = keepHistory;
        if (this.keepHistory) { _history = new HashMap<>(); }
    }

    public void observeCount(double count) throws Exception
    {
        LocalDateTime clockTime = _sandbox.getClockTime();

        if (clockTime.compareTo(lastTime) < 0) {
            throw new Exception("Time of new count cannot be earlier than current time.");
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

            if (!HoursForCount.containsKey(lastCount)) {
                HoursForCount.put(lastCount, 0.0);
            }
            HoursForCount.put(lastCount, HoursForCount.get(lastCount) + hours);
        }

        if (_logFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(_logFile, true))) {
                writer.write(String.format("%f,%f", totalHours, lastCount));
                if (Paused) {
                    writer.write(",Paused");
                }
                writer.newLine();

                if (count != lastCount) {
                    writer.write(String.format("%f,%f", totalHours, count));
                    if (Paused) {
                        writer.write(",Paused");
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lastTime = clockTime;
        lastCount = count;

        if (keepHistory) {
            _history.put(clockTime, count);
        }
    }

    /***
     * Remove parameter clockTime as since Version 3.6, according to Issue 1
     * @param count
     * @param clockTime
     */
    @Override
    public void observeCount(double count, LocalDateTime clockTime)
    {
        checkClockTime(clockTime);
        try
        {
            observeCount(count);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void observeChange(double change)
    {
        try
        {
            observeCount(lastCount + change);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /***
     * Remove parameter clockTime as since Version 3.6, according to Issue 1
     * @param change
     * @param clockTime
     */
    public void observeChange(double change, LocalDateTime clockTime)
    {
        checkClockTime(clockTime);
        observeChange(change);
    }

    public void pause() {
        LocalDateTime clockTime = _sandbox.getClockTime();
        if (Paused) {
            return;
        }
        observeCount(lastCount, clockTime);
        Paused = true;
        if (_logFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(_logFile, true))) {
                writer.write(String.format("%f,%f,Paused", totalHours, lastCount));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Remove parameter clockTime as since Version 3.6, according to Issue 1
     * @param clockTime
     */
    public void pause(LocalDateTime clockTime) {
        checkClockTime(clockTime);
        pause();
    }

    public void resume() {
        if (!Paused) return;
        lastTime = _sandbox.getClockTime();
        Paused = false;

        if (_logFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(_logFile, true))) {
                writer.write(String.format("%f,%f,Paused", totalHours, lastCount));
                writer.newLine();
                writer.write(String.format("%f,%f", totalHours, lastCount));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Remove parameter clockTime as since Version 3.6, according to Issue 1
     * @param clockTime
     */
    public void resume(LocalDateTime clockTime) {
        checkClockTime(clockTime);
        resume();
    }
    private void checkClockTime(LocalDateTime clockTime) {
        if (!clockTime.equals(_sandbox.getClockTime())) {
            throw new RuntimeException("ClockTime is not consistent with the Sandbox.");
        }
    }

    public void warmedUp() {
        _initialTime = _sandbox.getClockTime();
        lastTime = _sandbox.getClockTime();
        totalIncrement = 0;
        totalDecrement = 0;
        totalHours = 0;
        cumValue = 0;
        HoursForCount = new HashMap<>();
    }

    public Map<Double, Double> HoursForCount = new TreeMap<>();

    private void sortHoursForCount() {
        HoursForCount = HoursForCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(
                        java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                java.util.LinkedHashMap::new
                        )
                );
    }

    /***
     * @param ratio values between 0 and 100
     * @return Get the percentile of count values on time, i.e., the count value that with x-percent of time the observation is not higher than it.
     */
    public double percentile(double ratio) {
        sortHoursForCount();
        double threshold = HoursForCount.values().stream().mapToDouble(Double::doubleValue).sum() * ratio / 100;
        for (Map.Entry<Double, Double> entry : HoursForCount.entrySet()) {
            threshold -= entry.getValue();
            if (threshold <= 0) {
                return entry.getKey();
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    /***
     * Statistics for the amount of time spent at each range of count values
     * @param countInterval width of the count value interval
     * @return A dictionary map from [the lowerbound value of each interval] to the array of [total hours observed], [probability], [cumulated probability]
     */
    public HashMap<Double, double[]> histogram(double countInterval) {
        sortHoursForCount();
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
    public ReadOnlyHourCounter asReadOnly() {
        if (ReadOnly == null) {
            ReadOnly = new ReadOnlyHourCounter(this);
        }
        return ReadOnly;
    }

    @Override
    public void close() { }
}
