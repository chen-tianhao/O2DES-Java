import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public class Event implements AutoCloseable {
    private static int count = 0;
    private int index = count++;
    private String tag;
    private Sandbox owner;
    private LocalDateTime scheduledTime;
    private Runnable action;

    public int getIndex() {
        return index;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public Event(Sandbox owner, Runnable action, LocalDateTime scheduledTime, String tag) {
        this.owner = owner;
        this.action = action;
        this.scheduledTime = scheduledTime;
        this.tag = tag;
    }

    public Event(Sandbox owner, Runnable action, LocalDateTime scheduledTime) {
        this(owner, action, scheduledTime, null);
    }

    public void invoke() {
        action.run();
    }

    @Override
    public String toString() {
        return String.format("%s#%d", tag, index);
    }

    @Override
    public void close() {
    }
}

class EventComparer implements Comparator<Event> {
    private static final EventComparer instance = new EventComparer();

    private EventComparer() { }

    public static EventComparer getInstance() {
        return instance;
    }

    @Override
    public int compare(Event x, Event y) {
        int compare = x.getScheduledTime().compareTo(y.getScheduledTime());
        if (compare == 0) return Integer.compare(x.getIndex(), y.getIndex());
        return compare;
    }
}
