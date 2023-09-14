import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.io.*;


interface ISandbox extends AutoCloseable {
    int getIndex();
    String getId();
    Pointer getPointer();
    int getSeed();
    ISandbox getParent();
    List<ISandbox> getChildren();
    LocalDateTime getClockTime();
    LocalDateTime getHeadEventTime();

    String ToString();

    String getLogFile();
    void setLogFile(String logFile);
    boolean getDebugMode();
    boolean setDebugMode(boolean debugMode);
    boolean run();
    boolean run(int eventCount);
    boolean run(LocalDateTime terminate);
    boolean run(Duration duration);
    boolean run(double speed);
    boolean warmUp(LocalDateTime till);
    boolean warmUp(Duration period);

}


public abstract class Sandbox implements ISandbox {
    private static int count = 0;
    /**
     * Unique index in sequence for all module instances
     */
    private int index;
    /**
     * Tag of the instance of the module
     */
    private String id;
    private Pointer pointer;
    private Random defaultRS;
    private int seed;
    SortedSet<Event> futureEventList = new TreeSet<Event>(EventComparer.getInstance());


    public Random getDefaultRS() {
        return defaultRS;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        defaultRS = new Random(seed);
    }

    protected void schedule(Runnable action, LocalDateTime clockTime, String tag)
    {
        futureEventList.add(new Event(this, action, clockTime, tag));
    }

    protected void schedule(Runnable action, LocalDateTime clockTime)
    {
        futureEventList.add(new Event(this, action, clockTime, null));
    }

    protected void schedule(Runnable action, Duration delay, String tag)
    {
        futureEventList.add(new Event(this, action, clockTime.plus(delay), tag));
    }

    protected void schedule(Runnable action, Duration delay)
    {
        futureEventList.add(new Event(this, action, clockTime.plus(delay), null));
    }

    protected void schedule(Runnable action, String tag)
    {
        futureEventList.add(new Event(this, action, clockTime, tag));
    }

    protected void schedule(Runnable action)
    {
        futureEventList.add(new Event(this, action, clockTime, null));
    }





    Event getHeadEvent() {
        Event headEvent = futureEventList.isEmpty() ? null : futureEventList.first();
        for (ISandbox child : childrenList) {
            Event childHeadEvent = ((Sandbox) child).getHeadEvent();
            if (headEvent == null || (childHeadEvent != null &&
                EventComparer.getInstance().compare(childHeadEvent, headEvent) < 0))
            {
                headEvent = childHeadEvent;
            }
        }
        return headEvent;
    }
    private LocalDateTime clockTime = LocalDateTime.MIN;
    public LocalDateTime getClockTime()
    {
        if (getParent() == null) return clockTime;
        return getParent().getClockTime();
    }


    public LocalDateTime getHeadEventTime()
    {
        Event head = getHeadEvent();
        if (head == null) return null;
        return head.getScheduledTime();
    }

    public boolean run() {
        if (getParent() != null)
        {
            return getParent().run();
        }
        Event head = getHeadEvent();
        if (head == null)
        {
            return false;
        }
        head.getOwner().futureEventList.remove(head);
        clockTime = head.getScheduledTime();
        head.invoke();
        return true;
    }

    public boolean run(Duration duration) {
        if (getParent() != null) {
            return getParent().run(duration);
        }
        return run(getClockTime().plusNanos(duration.toMillis()* 1_000_000));
    }

    public boolean run(LocalDateTime terminate) {
        if (getParent() != null)
        {
            return getParent().run(terminate);
        }
        while (true) {
            Event head = getHeadEvent();
            if (head != null && head.getScheduledTime().compareTo(terminate) <= 0)
            {
                run();
            }
            else
            {
                clockTime = terminate;
                return head != null;
            }
        }
    }

    public boolean run(int eventCount) {
        if (getParent() != null)
        {
            return getParent().run(eventCount);
        }
        while (eventCount-- > 0)
        {
            if (!run())
            {
                return false;
            }
        }
        return true;
    }

    private LocalDateTime realTimeForLastRun = null;

    public boolean run(double speed) {
        if (getParent() != null)
        {
            return getParent().run(speed);
        }
        boolean result = true;
        if (realTimeForLastRun != null)
        {
            result = run(LocalDateTime.now().plusSeconds(
                    (long)(Duration.between(LocalDateTime.now(), realTimeForLastRun).getSeconds() * speed)
            ));
        }
        realTimeForLastRun = LocalDateTime.now();
        return result;
    }


    private String logFile;

    private ISandbox parent = null;
    public ISandbox getParent() { return parent; }
    private final List<ISandbox> childrenList = new ArrayList<ISandbox>();

    private List<ISandbox> children = Collections.unmodifiableList(childrenList);
    public List<ISandbox> getChildren() { return children; }

    protected Sandbox AddChild(Sandbox child)
    {
        childrenList.add(child);
        child.parent = this;
        onWarmedUp += child.onWarmedUp;
        return child;
    }

    private final List<HourCounter> hourCountersList = new ArrayList<HourCounter>();

    private List<HourCounter> hourCounters = Collections.unmodifiableList(hourCountersList);
    public List<HourCounter> getHourCounters() { return hourCounters; }

    protected HourCounter AddHourCounter(boolean keepHistory)
    {
        HourCounter hc = new HourCounter(this, keepHistory);
        hourCountersList.add(hc);
        onWarmedUp += () => hc.warmedUp();
        return hc;
    }

    protected HourCounter AddHourCounter()
    {
        return AddHourCounter(false);
    }

    public Sandbox(int seed, String id, Pointer pointer) {
        this.seed = seed;
        this.id = id;
        this.pointer = pointer;
        this.index = ++count;
        this.defaultRS = new Random(seed);
    }

    public String ToString()
    {
        String str = id;
        if (str == null || str.length() == 0) { str = getClass().getName(); }
        str += "#" + index;
        return str;
    }

    public boolean warmUp(Duration period)
    {
        if (parent != null) return parent.warmUp(period);
        return warmUp(LocalDateTime.now().plus(period));
    }
    public boolean warmUp(LocalDateTime till)
    {
        if (parent != null) return parent.warmUp(till);
        var result = run(till);
        onWarmedUp.invoke();
        return result; // to be continued
    }
    private Runnable onWarmedUp;

    protected void warmedUpHandler() { }


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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timeStr = dateFormat.format(getClockTime());
        if (logFile != null) {
            try {
                FileWriter writer = new FileWriter(logFile, true);
                writer.write(timeStr + "\t" + getId() + "\t");
                for (Object arg : args) { writer.write(arg + "\t"); }
                writer.write("\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean debugMode = false;


    // Dispose 方法
    @Override
    public void close() {
        for (ISandbox child : childrenList) {
            if (child instanceof Sandbox) { ((Sandbox) child).close(); }
        }
        for (HourCounter hc : hourCountersList) { hc.close(); }
    }

}
