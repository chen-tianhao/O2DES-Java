﻿import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

interface Callback {
    void callback();
}

public class Event implements AutoCloseable {
    private static int count = 0;
    int index = count++;
    String tag;
    Sandbox owner;
    LocalDateTime scheduledTime;
    Action action;

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

    Event(Sandbox owner, Action action, LocalDateTime scheduledTime, String tag) {
        this.owner = owner;
        this.action = action;
        this.scheduledTime = scheduledTime;
        this.tag = tag;
    }

    Event(Sandbox owner, Action action, LocalDateTime scheduledTime) {
        this(owner, action, scheduledTime, null);
    }

    public void invoke() {
        action.invoke();
    }

    @Override
    public String toString() {
        return String.format("%s#%d", tag, index);
    }

    @Override
    public void close() { }

    // for multicast delegation
    private List<Callback> callbacks = new ArrayList<>();

    public void register(Callback callback) {
        callbacks.add(callback);
    }

    public void trigger() {
        System.out.println("Event occurred!");
        callbacks.forEach(Callback::callback);
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
