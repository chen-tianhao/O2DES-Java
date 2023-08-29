import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhaseTracer {
    private LocalDateTime initialTime;
    private int lastPhaseIndex;
    private Map<String, Integer> indices = new HashMap<>();
    private List<String> allPhases = new ArrayList<>();
    private List<Tuple> history = new ArrayList<>();
    private boolean historyOn;
    private List<Duration> timeSpans = new ArrayList<>();

    public class Tuple {
        LocalDateTime dateTime;
        int phaseIndex;

        Tuple(LocalDateTime dateTime, int phaseIndex) {
            this.dateTime = dateTime;
            this.phaseIndex = phaseIndex;
        }
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public List<String> getAllPhases() {
        return allPhases;
    }

    public String getLastPhase() {
        return allPhases.get(lastPhaseIndex);
    }

    public List<Tuple> getHistory() {
        return history;
    }

    public boolean isHistoryOn() {
        return historyOn;
    }

    public List<Duration> getTimeSpans() {
        return timeSpans;
    }

    private LocalDateTime lastTime;

    public PhaseTracer(String initPhase, LocalDateTime initialTime, boolean historyOn) {
        this.initialTime = initialTime;
        this.lastTime = initialTime;
        this.lastPhaseIndex = getPhaseIndex(initPhase);
        this.historyOn = historyOn;
        if (historyOn) {
            history.add(new Tuple(lastTime, lastPhaseIndex));
        }
    }

    private int getPhaseIndex(String phase) {
        if (!indices.containsKey(phase)) {
            indices.put(phase, allPhases.size());
            allPhases.add(phase);
            timeSpans.add(Duration.ZERO);
        }
        return indices.get(phase);
    }

    public void updPhase(String phase, LocalDateTime clockTime) {
        Duration duration = Duration.between(lastTime, clockTime);
        timeSpans.set(lastPhaseIndex, timeSpans.get(lastPhaseIndex).plus(duration));
        if (historyOn) {
            history.add(new Tuple(clockTime, getPhaseIndex(phase)));
        }
        lastPhaseIndex = getPhaseIndex(phase);
        lastTime = clockTime;
    }

    public void warmedUp(LocalDateTime clockTime) {
        initialTime = clockTime;
        lastTime = clockTime;
        if (historyOn) {
            history = new ArrayList<>();
            history.add(new Tuple(clockTime, lastPhaseIndex));
        }
        timeSpans = new ArrayList<>();
        for (int i = 0; i < allPhases.size(); i++) {
            timeSpans.add(Duration.ZERO);
        }
    }

    public double getProportion(String phase, LocalDateTime clockTime) {
        if (!indices.containsKey(phase)) {
            return 0;
        }
        Duration timeSpan = timeSpans.get(indices.get(phase));
        if (phase.equals(getLastPhase())) {
            timeSpan = timeSpan.plus(Duration.between(lastTime, clockTime));
        }
        Duration sum = Duration.between(initialTime, clockTime);
        return (double) timeSpan.toMillis() / sum.toMillis();
    }
}
