package O2DESNet.Demos;

import O2DESNet.*;
import O2DESNet.Distributions.*;
import O2DESNet.Standard.*;

public class TandemQueue extends Sandbox {
    // Static Properties
    private double hourlyArrivalRate;
    private double hourlyServiceRate1;
    private double hourlyServiceRate2;

    // Dynamic Properties
    private final IGenerator Generator;
    private final IQueue Queue1;
    private final IServer Server1;
    private final IQueue Queue2;
    private final IServer Server2;
    private final HourCounter HcInSystem;

    // Constructor
    public TandemQueue(double arrRate, double svcRate1, double svcRate2, int bufferQSize, int seed) {
        super(seed);
        hourlyArrivalRate = arrRate;
        hourlyServiceRate1 = svcRate1;
        hourlyServiceRate2 = svcRate2;

        Generator = AddChild(new Generator(new Generator.Statics() {
            InterArrivalTime = rs -> Exponential.Sample(rs, TimeSpan.FromHours(1 / hourlyArrivalRate))
        }, DefaultRS.Next()));

        Queue1 = AddChild(new Queue(Double.POSITIVE_INFINITY, DefaultRS.Next(), "Queue1"));

        Server1 = AddChild(new Server(new Server.Statics() {
            Capacity = 1,
            ServiceTime = (rs, load) -> Exponential.Sample(rs, TimeSpan.FromHours(1 / hourlyServiceRate1))
        }, DefaultRS.Next(), "Server1"));

        Queue2 = AddChild(new Queue(bufferQSize, DefaultRS.Next(), "Queue2"));

        Server2 = AddChild(new Server(new Server.Statics() {
            Capacity = 1,
            ServiceTime = (rs, load) -> Exponential.Sample(rs, TimeSpan.FromHours(1 / hourlyServiceRate2))
        }, DefaultRS.Next(), "Server2"));

        Generator.OnArrive += () -> {
            Queue1.RqstEnqueue(new Load());
        };
        Generator.OnArrive += this::Arrive;

        Queue1.OnEnqueued += Server1::RqstStart;
        Server1.OnStarted += Queue1::Dequeue;

        Server1.OnReadyToDepart += load -> {
            Queue2.RqstEnqueue(load);
        };
        Queue2.OnEnqueued += Server1::Depart;

        Queue2.OnEnqueued += Server2::RqstStart;
        Server2.OnStarted += Queue2::Dequeue;

        Server2.OnReadyToDepart += load -> {
            Server2.Depart(load);
            Depart();
        };

        HcInSystem = AddHourCounter();

        // Initial event
        Generator.Start();
    }

    // Events / Methods
    private void Arrive() {
        Log("Arrive");
        HcInSystem.observeChange(1, ClockTime());
    }

    private void Depart() {
        Log("Depart");
        HcInSystem.observeChange(-1, ClockTime());
    }

    @Override
    public void Dispose() {
    }
}
