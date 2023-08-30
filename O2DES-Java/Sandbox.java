import java.time.Duration;
import java.util.*;
import java.io.*;

public interface ISandbox extends AutoCloseable {
    int getIndex();
    String getId();
    Pointer getPointer();
    int getSeed();
    ISandbox getParent();
    List<ISandbox> getChildren();
    Date getClockTime();
    Date getHeadEventTime();
    String getLogFile();
    void setLogFile(String logFile);
    boolean isDebugMode();
    boolean run();
    boolean run(int eventCount);
    boolean run(Date terminate);
    boolean run(Duration duration);
    boolean run(double speed);
    boolean warmUp(Date till);
    boolean warmUp(Duration period);
}

abstract class Sandbox<TAssets extends IAssets> implements ISandbox {
    private final TAssets assets;
    private final int seed;
    private final String id;
    private final Pointer pointer;

    public Sandbox(TAssets assets, int seed, String id, Pointer pointer) {
        this.assets = assets;
        this.seed = seed;
        this.id = id;
        this.pointer = pointer;
    }
}

abstract class Sandbox implements ISandbox {
    private static int count = 0;
    private int index;
    private String id;
    private Pointer pointer;
    private Random defaultRS;
    private int seed;
    private SortedSet<Event> futureEventList = new TreeSet<>(new EventComparator());
    private List<ISandbox> childrenList = new ArrayList<>();
    private Date clockTime = new Date(0);

    public Sandbox(int seed, String id, Pointer pointer) {
        this.seed = seed;
        this.id = id;
        this.pointer = pointer;
        this.index = ++count;
        this.defaultRS = new Random(seed);
    }

    private Event getHeadEvent() {
        Event headEvent = futureEventList.isEmpty() ? null : futureEventList.first();
        for (ISandbox child : childrenList) {
            Event childHeadEvent = ((Sandbox) child).getHeadEvent();
            if (headEvent == null || (childHeadEvent != null && childHeadEvent.compareTo(headEvent) < 0)) {
                headEvent = childHeadEvent;
            }
        }
        return headEvent;
    }

    public boolean run() {
        if (getParent() != null) {
            return getParent().run();
        }
        Event head = getHeadEvent();
        if (head == null) {
            return false;
        }
        futureEventList.remove(head);
        clockTime = head.getScheduledTime();
        head.invoke();
        return true;
    }

    public boolean run(Duration duration) {
        if (getParent() != null) {
            return getParent().run(duration);
        }
        return run(new Date(getClockTime().getTime() + duration.toMillis()));
    }

    public boolean run(Date terminate) {
        if (getParent() != null) {
            return getParent().run(terminate);
        }
        while (true) {
            Event head = getHeadEvent();
            if (head != null && head.getScheduledTime().compareTo(terminate) <= 0) {
                run();
            } else {
                clockTime = terminate;
                return head != null;
            }
        }
    }

    public boolean run(int eventCount) {
        if (getParent() != null) {
            return getParent().run(eventCount);
        }
        while (eventCount-- > 0) {
            if (!run()) {
                return false;
            }
        }
        return true;
    }

    private Date realTimeForLastRun = null;

    public boolean run(double speed) {
        if (getParent() != null) {
            return getParent().run(speed);
        }
        boolean result = true;
        if (realTimeForLastRun != null) {
            result = run(new Date((long) (getClockTime().getTime() +
                                  (System.currentTimeMillis() - realTimeForLastRun.getTime()) * speed)));
        }
        realTimeForLastRun = new Date();
        return result;
    }

    private String logFile;

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
        if (this.logFile != null) {
            try {
                FileWriter writer = new FileWriter(this.logFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void log(Object... args) {
        String timeStr = String.format("%1$tY/%1$tm/%1$td %1$tH:%1$tM:%1$tS.%1$tL", getClockTime());
        if (logFile != null) {
            try {
                FileWriter writer = new FileWriter(logFile, true);
                writer.write(timeStr + "\t" + getId() + "\t");
                for (Object arg : args) {
                    writer.write(arg + "\t");
                }
                writer.write("\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    private Action onWarmedUp = () -> {};

    protected void warmedUpHandler() {
        onWarmedUp.run();
    }

    // Dispose 方法
    @Override
    public void close() {
        for (ISandbox child : childrenList) {
            if (child instanceof Sandbox) {
                ((Sandbox) child).close();
            }
        }
        for (HourCounter hc : hourCountersList) {
            hc.close();
        }
    }

}
