package Demos;

public interface IMMnQueue {
    double getHourlyArrivalRate();
    double getHourlyServiceRate();
    int getNServers();
    double getAvgNQueueing();
    double getAvgNServing();
    double getAvgHoursInSystem();
}
