import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class ExperimentRunner {
    public static void main(String[] args) throws IOException {
        Scheduler[] schedulers = { new SJFScheduler(), new ACS() };
        for (Scheduler scheduler : schedulers) {
            runExperiment(scheduler, "testcase1", TestCases.testCase1());
            runExperiment(scheduler, "testcase2", TestCases.testCase2());
            runExperiment(scheduler, "mytestcase", TestCases.myTestCase());
        }
    }

    static void runExperiment(Scheduler scheduler, String name, List<Process> processes) throws IOException {
        String fileName = "experiment_" + scheduler.run(processes, 1, 0).algorithm + "_" + name + ".csv";

        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("Q,CS,AvgWaiting,AvgTurnaround");

            for (int q = 1; q <= 10; q++) {
                for (int cs = 0; cs <= 5; cs++) {
                    SimulationResult result = scheduler.run(processes, q, cs);
                    double totalWaiting = 0;
                    double totalTurnaround = 0;
                    for (Process p : result.finished) {
                        int turnaround = p.finishTime - p.arrival;
                        int waiting = turnaround - p.burst;
                        totalTurnaround += turnaround;
                        totalWaiting += waiting;
                    }
                    int n = result.finished.size();
                    out.println(q + "," + cs + ","
                            + String.format(Locale.US, "%.2f", totalWaiting / n) + ","
                            + String.format(Locale.US, "%.2f", totalTurnaround / n));
                }
            }
        }
        System.out.println("Done. Results saved in " + fileName);
    }
}