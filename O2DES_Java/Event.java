package O2DES_Java;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Event implements AutoCloseable {
    private static int count = 0;
    int index = count++;
    String tag;
    Sandbox owner;
    LocalDateTime scheduledTime;
    Callback callback;

    public Sandbox getOwner()
    {
        return owner;
    }

    public int getIndex() {
        return index;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    Event(Sandbox owner, Callback callback, LocalDateTime scheduledTime, String tag) {
        this.owner = owner;
        this.callback = callback;
        this.scheduledTime = scheduledTime;
        this.tag = tag;
    }

    Event(Sandbox owner, Callback callback, LocalDateTime scheduledTime) {
        this(owner, callback, scheduledTime, null);
    }
    
    public void invoke() {
        callback.callback();
    }

    @Override
    public String toString() {
        return String.format("%s#%d", tag, index);
    }

    @Override
    public void close() { }
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
