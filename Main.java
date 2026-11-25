import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Scheduler[] schedulers = { new SJFScheduler(), new ACS() };

        runTestCase(schedulers, "Test Case 1", TestCases.testCase1(), 3, 0);
        runTestCase(schedulers, "Test Case 2", TestCases.testCase2(), 3, 1);
        runTestCase(schedulers, "My Test Case", TestCases.myTestCase(), 2, 1);
    }

    // one test case -> every algorithm, grouped together
    static void runTestCase(Scheduler[] schedulers, String title, List<Process> processes, int q, int cs) {
        System.out.println("---------------------------");
        System.out.println(title + " (Q = " + q + ", CS = " + cs + ")");
        System.out.println("---------------------------");
        System.out.println();

        for (Scheduler scheduler : schedulers) {
            run(scheduler, processes, q, cs);
        }
    }

    static void run(Scheduler scheduler, List<Process> processes, int q, int cs) {
        SimulationResult result = scheduler.run(processes, q, cs);
        boolean twoTier = scheduler instanceof ACS;

        System.out.println("--- " + result.algorithm + " ---");
        System.out.println("Gantt: " + result.slices);
        System.out.println();

        for (Snapshot s : result.snapshots) {
            if (twoTier) {
                System.out.println("Time " + s.time + ": running " + s.running
                        + ", Tier 1 " + s.tier1 + ", Tier 2 " + s.tier2);
            } else {
                System.out.println("Time " + s.time + ": running " + s.running
                        + ", Ready Queue" + s.tier1);
            }
        }
        System.out.println();

        double totalTurnaround = 0;
        double totalWaiting = 0;
        for (Process p : result.finished) {
            int turnaround = p.finishTime - p.arrival;
            int waiting = turnaround - p.burst;
            int response = p.firstRunTime - p.arrival;
            totalTurnaround += turnaround;
            totalWaiting += waiting;
            System.out.println(p.id + ": finish " + p.finishTime + ", turnaround " + turnaround
                    + ", waiting " + waiting + ", response " + response);
        }
        int n = result.finished.size();
        System.out.println();
        System.out.println(String.format(Locale.US, "Average Turnaround Time: %.2f", totalTurnaround / n));
        System.out.println(String.format(Locale.US, "Average Waiting Time: %.2f", totalWaiting / n));
        System.out.println();
        System.out.println();
    }
}