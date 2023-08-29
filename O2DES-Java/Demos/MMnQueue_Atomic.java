package O2DESNet.Demos;

import O2DESNet.*;
import O2DESNet.Distributions.*;

public class MMnQueue_Atomic extends Sandbox implements IMMnQueue {
    // Static Properties
    private double hourlyArrivalRate;
    private double hourlyServiceRate;
    private int nServers;

    // Dynamic Properties / Methods
    private HourCounter HC_InServer;
    private HourCounter HC_InQueue;
    private HourCounter HC_InSystem;

    // Constructor
    public MMnQueue_Atomic(double hourlyArrivalRate, double hourlyServiceRate, int nServers, int seed) {
        super(seed);
        this.hourlyArrivalRate = hourlyArrivalRate;
        this.hourlyServiceRate = hourlyServiceRate;
        this.nServers = nServers;

        HC_InServer = AddHourCounter();
        HC_InQueue = AddHourCounter();
        HC_InSystem = AddHourCounter();

        // Initial event
        Arrive();
    }

    // Interface methods
    public double getHourlyArrivalRate() {
        return hourlyArrivalRate;
    }

    public double getHourlyServiceRate() {
        return hourlyServiceRate;
    }

    public int getNServers() {
        return nServers;
    }

    public double getAvgNQueueing() {
        return HC_InQueue.getAverageCount();
    }

    public double getAvgNServing() {
        return HC_InServer.getAverageCount();
    }

    public double getAvgHoursInSystem() {
        return HC_InSystem.getAverageDuration().toHours();
    }

    // Events
    private void Arrive() {
        Log("Arrive");
        HC_InSystem.observeChange(1, ClockTime());

        if (HC_InServer.getLastCount() < nServers) {
            Start();
        } else {
            Log("Enqueue");
            HC_InQueue.observeChange(1, ClockTime());
        }
        Schedule(this::Arrive, Exponential.Sample(DefaultRS, TimeSpan.FromHours(1 / hourlyArrivalRate)));
    }

    private void Start() {
        Log("Start");
        HC_InServer.observeChange(1, ClockTime());
        Schedule(this::Depart, Exponential.Sample(DefaultRS, TimeSpan.FromHours(1 / hourlyServiceRate)));
    }

    private void Depart() {
        Log("Depart");
        HC_InServer.observeChange(-1, ClockTime());
        HC_InSystem.observeChange(-1, ClockTime());

        if (HC_InQueue.getLastCount() > 0) {
            Log("Dequeue");
            HC_InQueue.observeChange(-1, ClockTime());
            Start();
        }
    }
}
