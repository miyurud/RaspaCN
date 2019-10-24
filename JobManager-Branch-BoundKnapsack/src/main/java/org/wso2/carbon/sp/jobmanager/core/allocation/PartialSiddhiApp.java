package org.wso2.carbon.sp.jobmanager.core.allocation;

public class PartialSiddhiApp {

    private double cpuUsage;
    private double latency;
    private double throughput;
    private double latencyBycpuUsage;
    private int eventCount;
    private String name;

    /**
     * Constructor that instantiates cpuUsage, latency and name for an item.
     *
     * @param cpuUsage
     * @param latency
     * @param name
     */
    public PartialSiddhiApp(double cpuUsage, double latency, double throughput, int eventCount, String name) {
        this.cpuUsage = cpuUsage;
        this.latency = latency;
        this.throughput = throughput;
        this.eventCount = eventCount;
        latencyBycpuUsage = (double) latency / (double) cpuUsage;
        this.name = name;
    }

    /**
     * Method that gets the latency / cpuUsage latency from an item.
     *
     * @return
     */
    public double getlatencyBycpuUsage() {
        return latencyBycpuUsage;
    }

    /**
     * Method that returns the cpuUsage an item has.
     *
     * @return
     */
    public double getcpuUsage() {
        return cpuUsage;
    }

    /**
     * Method that gets the latency an item has.
     *
     * @return
     */
    public double getlatency() {
        return latency;
    }

    /**
     * Method that sets the name of an item.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Method that gets the throughput an item has.
     *
     * @return
     */
    public double getThroughput() {
        return throughput;
    }

    /**
     * Method that sets the throughput of an item has.
     *
     * @return
     */
    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
}
