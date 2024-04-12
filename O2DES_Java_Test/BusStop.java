package O2DES_Java_Test;

import O2DES_Java.Action;
import O2DES_Java.Sandbox;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
// You need to import Logger and other necessary classes
// from their respective Java packages
class Bus
{
    int Idx;

    public Bus(int idx)
    {
        Idx = idx;
    }
    public String toString()
    {
        return String.format("Bus[%04d]", Idx);
    }
}

class GlobalConfig
    {
        public static int BusIdxStartFrom = 1;
        public static final String format = "%-22s %-27s %-10d %-10d %-10d";
        public static final String formatHeader = "%-22s %-27s %-10s %-10s %-10s";

        public static final int PlanedArrivalInterval = 10;          // Minutes
        public static final int PlanedStopDuration = 1;              // Minutes

        public static final int ArriveRandomnessLowerBound = 0;      // Minutes
        public static final int ArriveRandomnessUpperBound = 0;//10;     // Minutes

        public static final int BerthRandomnessLowerBound = 0;       // Minutes
        public static final int BerthRandomnessUpperBound = 0;//30;      // Minutes
    }

public class BusStop extends Sandbox {
    public int FixedArrivalInterval;
    public int FixedStopDuration;

    private List<Bus> _queue;
    private int _numberOfBusArrival;
    private int _numberOfBusDeparture;

    public List<Bus> GetQueue()
    {
        return _queue;
    }

    public double GetNumberOfBusArrival()
    {
        return _numberOfBusArrival;
    }

    public double GetNumberOfBusDeparture()
    {
        return _numberOfBusDeparture;
    }

    public BusStop()
    {
        super(0);
        Init();
    }

    public BusStop(int seed) 
    {
        super(seed);
        Init();
    }

    private void Init()
    {
        FixedArrivalInterval = GlobalConfig.PlanedArrivalInterval;
        FixedStopDuration = GlobalConfig.PlanedStopDuration;
        _queue = new ArrayList<Bus>();
        _numberOfBusArrival = 0;
        _numberOfBusDeparture = 0;
        this.schedule(() -> Arrive(new Bus(GlobalConfig.BusIdxStartFrom++)));
        OnArrive.register(this::NextBus);
    }

    private void NextBus()
    {
        int min = GlobalConfig.ArriveRandomnessLowerBound;
        int max = GlobalConfig.ArriveRandomnessUpperBound;
        Duration timeSpan = Duration.ofMinutes(FixedArrivalInterval + getDefaultRS().nextInt(max-min+1)+min);
        this.schedule(() -> Arrive(new Bus(GlobalConfig.BusIdxStartFrom++)), timeSpan);
    }

    void Arrive(Bus bus)
    {
        _queue.add(bus);
        _numberOfBusArrival += 1;
        String output = String.format(GlobalConfig.format, FDT(getClockTime()), new String(bus.toString() + " arrives."), _numberOfBusArrival, _queue.size(), _numberOfBusDeparture);
        System.out.println(output);
        OnArrive.invoke();
        if (_queue.size() > _numberOfBusArrival - _numberOfBusDeparture - 1)
        {
            Berth();
        }
    }

    private Action OnArrive = new Action();

    void Berth()
    {
        Bus firstBusInQueue = _queue.get(0);
        _queue.remove(firstBusInQueue);
        String output = String.format(GlobalConfig.format, FDT(getClockTime()), new String(firstBusInQueue.toString() + " berths."), _numberOfBusArrival, _queue.size(), _numberOfBusDeparture);
        System.out.println(output);
        
        int min = GlobalConfig.BerthRandomnessLowerBound;
        int max = GlobalConfig.BerthRandomnessUpperBound;
        Duration BerthingRandomness = Duration.ofMinutes(FixedStopDuration + getDefaultRS().nextInt(max-min+1)+min);
        schedule(() -> Depart(firstBusInQueue), BerthingRandomness);
    }

    void Depart(Bus bus)
    {
        _numberOfBusDeparture += 1;
        String output = String.format(GlobalConfig.format, FDT(getClockTime()), new String(bus.toString() + " departs."), _numberOfBusArrival, _queue.size(), _numberOfBusDeparture);
        System.out.println(output);
        if (_queue.size() > 0 ) 
        {
            Berth();
        }
    }

    private String FDT(LocalDateTime dt)
    {
        dt = dt.withYear(2024);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dt.format(formatter);
    }
    
    public static void main(String[] args) 
    {
        System.out.println("[A bus stop showcase]");
        int seed = 0; // Particant can change different seed for testing
        try (var model = new BusStop(seed)) {
            String output = String.format(GlobalConfig.formatHeader, "Clock Time", "Event", "Arrival#", "Queue Len", "Departure#");
            System.out.println(output);
            model.run(Duration.ofHours(24));
        }
        System.out.println();
    }
}
