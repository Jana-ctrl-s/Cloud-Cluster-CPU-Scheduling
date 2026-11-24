import java.util.List;
import java.util.Locale;

public class RunSJF {
    public static void main(String[] args) {
        run("Test Case 1", TestCases.testCase1(), 3, 0);
        run("Test Case 2", TestCases.testCase2(), 3, 1);
        run("My Test Case", TestCases.myTestCase(), 2, 1);
    }

    static void run(String title, List<Process> processes, int q, int cs) {
        System.out.println("SJF - " + title + " (Q = " + q + ", CS = " + cs + ")");
        System.out.println();
        SimulationResult result = new SJFScheduler().run(processes, q, cs);
        System.out.println("Gantt: " + result.slices);
        System.out.println();

        for (Snapshot s : result.snapshots) {
            System.out.println("Time " + s.time + ": running " + s.running + ", waiting " + s.tier1);
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
            System.out.println(p.id + ": finish " + p.finishTime + ", turnaround " + turnaround + ", waiting " + waiting + ", response " + response);
        }
        int n = result.finished.size();
        System.out.println();
        System.out.println(String.format(Locale.US, "Average Turnaround Time: %.2f", totalTurnaround / n));
        System.out.println(String.format(Locale.US, "Average Waiting Time: %.2f", totalWaiting / n));
        System.out.println();
        System.out.println();
    }
}