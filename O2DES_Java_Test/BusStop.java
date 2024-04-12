package O2DES_Java_Test;

import O2DES_Java.Action;
import O2DES_Java.Sandbox;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
// You need to import Logger and other necessary classes
// from their respective Java packages
class Bus
{
    int Idx;

    public Bus(int idx)
    {
        Idx = idx;
    }
    public String ToString()
    {
        return String.format("Bus[%04d]", Idx);
    }
}

class GlobalConfig
    {
        public static int BusIdxStartFrom = 1;
        public static final String format = "%22d %27d %10d %10d %10d";

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
    private double _numberOfBusArrival;
    private double _numberOfBusDeparture;

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
        // Init();
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
        //Schedule(() => Arrive(new Bus(GlobalConfig.BusIdxStartFrom++)));
        Bus bus = new Bus(1000);
        this.schedule(() -> Arrive(bus), Duration.ofSeconds(1));
        
        // OnArrive += NextBus;
    }

    void Arrive(Bus bus)
    {
        _queue.add(bus);
        _numberOfBusArrival += 1;
        String output = String.format(GlobalConfig.format, FDT(getClockTime()), new String(bus.toString() + " arrives."), _numberOfBusArrival, _queue.size(), _numberOfBusDeparture);
        System.out.println(output);
        // OnArrive.Invoke();
        if (_queue.size() > _numberOfBusArrival - _numberOfBusDeparture - 1)
        {
            // Berth();
        }
    }

    private String FDT(LocalDateTime dt)
    {
        dt = dt.withYear(2024);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dt.format(formatter);
    }

    /*
    private void NextBus()
    {
        int min = GlobalConfig.ArriveRandomnessLowerBound;
        int max = GlobalConfig.ArriveRandomnessUpperBound;
        Duration ArrivalRandomness = Duration.ofMinutes(getDefaultRS().nextInt(max-min)+min);
        Schedule(() => Arrive(new Bus(GlobalConfig.BusIdxStartFrom++)), TimeSpan.FromMinutes(FixedArrivalInterval).Add(ArrivalRandomness));
    }
    */
    public static void main(String[] args) {
        // Demo 2
        System.out.println("Demo 2 - Birth Death Process");
        BusStop sim2 = new BusStop(20);
        sim2.warmUp(LocalDateTime.of(1, 1, 1, 0, 0, 0));
        sim2.run(Duration.ofHours(30));
    }
    
    /*
    public static void main(String[] args) 
    {
        // 创建一个LocalDateTime对象
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println("Before: " + dateTime);

        // 设置年份为2023
        LocalDateTime newDateTime = dateTime.withYear(2023);
        System.out.println("After: " + newDateTime);
    }
    */
}
