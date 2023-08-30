package Demos;

import Distributions.*;
import Standard.*;

public class MMnQueue_Modular extends Sandbox implements IMMnQueue {
    // Static Properties
    private double hourlyArrivalRate;
    private double hourlyServiceRate;

    // Dynamic Properties
    private IGenerator Generator;
    private IQueue Queue;
    private IServer Server;
    private HourCounter HC_InSystem;

    // Constructor
    public MMnQueue_Modular(double hourlyArrivalRate, double hourlyServiceRate, int nServers, int seed) {
        super(seed);
        this.hourlyArrivalRate = hourlyArrivalRate;
        this.hourlyServiceRate = hourlyServiceRate;

        Generator = AddChild(new Generator(new Generator.Statics() {
            InterArrivalTime = rs -> Exponential.Sample(rs, TimeSpan.FromHours(1 / hourlyArrivalRate))
        }, DefaultRS.Next()));

        Queue = AddChild(new Queue(Double.POSITIVE_INFINITY, DefaultRS.Next()));

        Server = AddChild(new Server(new Server.Statics() {
            Capacity = nServers,
            ServiceTime = (rs, load) -> Exponential.Sample(rs, TimeSpan.FromHours(1 / hourlyServiceRate))
        }, DefaultRS.Next()));

        Generator.OnArrive += () -> {
            Queue.RqstEnqueue(new Load());
        };
        Generator.OnArrive += this::Arrive;

        Queue.OnEnqueued += Server::RqstStart;
        Server.OnStarted += Queue::Dequeue;

        Server.OnReadyToDepart += load -> {
            Server.Depart(load);
            Depart();
        };

        HC_InSystem = AddHourCounter();

        // Initial event
        Generator.Start();
    }

    // Interface methods
    public double getHourlyArrivalRate() {
        return hourlyArrivalRate;
    }

    public double getHourlyServiceRate() {
        return hourlyServiceRate;
    }

    public int getNServers() {
        return (int)Server.getCapacity();
    }

    public double getAvgNQueueing() {
        return Queue.getAvgNQueueing();
    }

    public double getAvgNServing() {
        return Server.getAvgNServing();
    }

    public double getAvgHoursInSystem() {
        return HC_InSystem.getAverageDuration().toHours();
    }

    // Events / Methods
    private void Arrive() {
        Log("Arrive");
        HC_InSystem.observeChange(1, ClockTime());
    }

    private void Depart() {
        Log("Depart");
        HC_InSystem.observeChange(-1, ClockTime());
    }

    @Override
    public void Dispose() {
    }
}
