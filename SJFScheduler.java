import java.util.ArrayList;
import java.util.List;
public class SJFScheduler implements Scheduler {
    public SimulationResult run(List<Process> input, int quantum, int cs) {
        SimulationResult result = new SimulationResult("SJF");
        List<Process> all = new ArrayList<>();
        for (Process p : input) {
            all.add(p.copy());
        }
        int time = 0;
        Process last = null;
        while (result.finished.size() < all.size()) {
            List<Process> ready = getReady(all, time);
            if (ready.isEmpty()) {
                addSnapshot(result, time, "idle", ready, null);
                time++;
                continue;
            }
            Process best = ready.get(0);
            for (Process p : ready) {
                if (isBetter(p, best, last)) {
                    best = p;
                }
            }
            if (last != null && best!=last && cs > 0) {
                int csStart = time;
                for (int i = 0; i < cs; i++) {
                    addSnapshot(result, time, "CS", getReady(all, time), null);
                    time++;
                }
                result.addSlice("CS", csStart, time);
            }
            if (best.firstRunTime == -1) {
                best.firstRunTime = time;
            }
            addSnapshot(result, time, best.id, getReady(all, time), best);
            result.addSlice(best.id, time, time + 1);
            best.remaining--;
            time++;
            last = best;
            if (best.remaining == 0) {
                best.finishTime = time;
                result.finished.add(best);
            }
        }
        return result;
    }
    private List<Process> getReady(List<Process> all, int time) {
        List<Process> ready = new ArrayList<>();
        for (Process p : all) {
            if (p.arrival <= time && p.remaining > 0) {
                ready.add(p);
            }
        }
        return ready;
    }
    private boolean isBetter(Process a, Process b, Process last) {
        if (a.remaining != b.remaining) {
            return a.remaining < b.remaining;
        }
        if (a == last) {
            return true;
        }
        if (b == last) {
            return false;
        }
        return a.arrival < b.arrival;
    }
    private void addSnapshot(SimulationResult result, int time, String running, List<Process> ready, Process runningProcess) {
        Snapshot s = new Snapshot(time, running);
        for (Process p : ready) {
            if (p != runningProcess) {
                s.tier1.add(Snapshot.describe(p));
            }
        }
        result.snapshots.add(s);
    }
}