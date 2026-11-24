public class Process {
    public final String id;
    public final int arrival;
    public final int burst;
    public final int priority;
    public int remaining;
    public int firstRunTime = -1;
    public int finishTime = -1;
    public int tier2Since = -1;
    public int effectivePriority;
    public Process(String id, int arrival, int burst, int priority) {
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.remaining = burst;
        this.effectivePriority = priority;
    }
    public Process copy() {
        return new Process(id, arrival, burst, priority);
    }
}