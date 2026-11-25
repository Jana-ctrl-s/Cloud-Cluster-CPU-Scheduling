import java.util.ArrayList;
import java.util.List;
public class ACS implements Scheduler {
    private int Aging_Interval = 4;
    public SimulationResult run(List<Process> input, int quantum, int cs) {
        SimulationResult result = new SimulationResult("ACS");
        List<Process> all = new ArrayList<>();
        for (Process p : input) {
            all.add(p.copy());
        }
        List<Process> tier1 = new ArrayList<>();
        List<Process> tier2 = new ArrayList<>();
        int time = 0;
        Process last = null;
        addArrivals(all, tier1, time);
        while (result.finished.size() < all.size()) {
            Process next;
            boolean fromTier1;
            if (!tier1.isEmpty()) {
                next = tier1.get(0);
                fromTier1 = true;
            } else if (!tier2.isEmpty()) {
                next = pickTier2(tier2, time);
                fromTier1 = false;
            } else {
                addSnapshot(result, time, "idle", tier1, tier2);
                time++;
                addArrivals(all, tier1, time);
                continue;
            }
            if (last != null && next != last && cs > 0) {
                int csStart = time;
                for (int i = 0; i < cs; i++) {
                    addSnapshot(result, time, "CS", tier1, tier2);
                    time++;
                    addArrivals(all, tier1, time);
                }
                result.addSlice("CS", csStart, time);
            }
            if (fromTier1) {
                tier1.remove(next);
            } else {
                tier2.remove(next);
            }
            if (next.firstRunTime == -1) {
                next.firstRunTime = time;
            }
            int run = next.remaining;
            if (fromTier1) {
                run = Math.min(quantum, next.remaining);
            }
            for (int i = 0; i < run; i++) {
                addSnapshot(result, time, next.id, tier1, tier2);
                result.addSlice(next.id, time, time + 1);
                next.remaining--;
                time++;
                addArrivals(all, tier1, time);
            }
            last = next;
            if (next.remaining == 0) {
                next.finishTime = time;
                result.finished.add(next);
            } else {
                next.tier2Since = time;
                next.effectivePriority = next.priority;
                tier2.add(next);
            }
        }
        return result;
    }
    private void addArrivals(List<Process> all, List<Process> tier1, int time) {
        for (Process p : all) {
            if (p.arrival == time) {
                tier1.add(p);
            }
        }
    }
    private void age(Process p, int time) {
        p.effectivePriority = p.priority - (time - p.tier2Since) / Aging_Interval;
    }
    private Process pickTier2(List<Process> tier2, int time) {
        Process best = null;
        for (Process p : tier2) {
            age(p, time);
            if (best == null || isBetter(p, best)) {
                best = p;
            }
        }
        return best;
    }
    private boolean isBetter(Process a, Process b) {
        if (a.effectivePriority != b.effectivePriority) {
            return a.effectivePriority < b.effectivePriority;
        }
        if (a.tier2Since != b.tier2Since) {
            return a.tier2Since < b.tier2Since;
        }
        return a.arrival < b.arrival;
    }
    private void addSnapshot(SimulationResult result, int time, String running, List<Process> tier1, List<Process> tier2) {
        Snapshot s = new Snapshot(time, running);
        for (Process p : tier1) {
            s.tier1.add(Snapshot.describe(p));
        }
        for (Process p : tier2) {
            age(p, time);
            s.tier2.add(Snapshot.describe(p));
        }
        result.snapshots.add(s);
    }
}