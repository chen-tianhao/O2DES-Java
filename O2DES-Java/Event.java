package O2DES-Java.Standard;

import java.util.Comparator;

public class Event implements AutoCloseable {
    private static int _count = 0;
    private int Index = _count++;
    private String Tag;
    private Sandbox Owner;
    private DateTime ScheduledTime;
    private Runnable Action;

    public Event(Sandbox owner, Runnable action, DateTime scheduledTime, String tag) {
        Owner = owner;
        Action = action;
        ScheduledTime = scheduledTime;
        Tag = tag;
    }

    public void invoke() {
        Action.run();
    }

    @Override
    public String toString() {
        return String.format("%s#%d", Tag, Index);
    }

    @Override
    public void close() {
    }
}

class EventComparer implements Comparator<Event> {
    private static final EventComparer _instance = new EventComparer();

    private EventComparer() {
    }

    public static EventComparer getInstance() {
        return _instance;
    }

    @Override
    public int compare(Event x, Event y) {
        int compare = x.getScheduledTime().compareTo(y.getScheduledTime());
        if (compare == 0) {
            return Integer.compare(x.getIndex(), y.getIndex());
        }
        return compare;
    }
}

public class Event : IDisposable
{
    private static int _count = 0;
    internal int Index { get; private set; } = _count++;
    internal string Tag { get; private set; }
    internal Sandbox Owner { get; private set; }
    internal DateTime ScheduledTime { get; private set; }
    internal Action Action { get; private set; }

    internal Event(Sandbox owner, Action action, DateTime scheduledTime, string tag = null)
    {
        Owner = owner;
        Action = action;
        ScheduledTime = scheduledTime;
        Tag = tag;
    }
    internal void Invoke() { Action.Invoke(); }
    public override string ToString()
    {
        return string.Format("{0}#{1}", Tag, Index);
    }

    public void Dispose()
    {
    }
}

internal sealed class EventComparer : IComparer<Event>
{
    private static readonly EventComparer _instance = new EventComparer();
    private EventComparer() { }
    public static EventComparer Instance { get { return _instance; } }
    public int Compare(Event x, Event y)
    {
        int compare = x.ScheduledTime.CompareTo(y.ScheduledTime);
        if (compare == 0) return x.Index.CompareTo(y.Index);
        return compare;
    }
}
