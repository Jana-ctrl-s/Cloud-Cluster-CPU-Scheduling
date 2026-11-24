import java.util.ArrayList;
import java.util.List;
public class SimulationResult {
    public String algorithm;
    public List<Slice> slices = new ArrayList<>();
    public List<Snapshot> snapshots = new ArrayList<>();
    public List<Process> finished = new ArrayList<>();
    public SimulationResult(String algorithm) {
        this.algorithm = algorithm;
    }
    public void addSlice(String label, int start, int end) {
        if (!slices.isEmpty()) {
            Slice last = slices.get(slices.size() - 1);
            if (last.label.equals(label) && last.end == start) {
                last.end = end;
                return;
            }
        }
        slices.add(new Slice(label, start, end));
    }
}
class Slice {
    public String label;
    public int start;
    public int end;
    public Slice(String label, int start, int end) {
        this.label = label;
        this.start = start;
        this.end = end;
    }
    public String toString() {
        return label + " [" + start + " - " + end + "]";
    }
}

class Snapshot {
    public int time;
    public String running;
    public List<String> tier1 = new ArrayList<>();
    public List<String> tier2 = new ArrayList<>();
    public Snapshot(int time, String running) {
        this.time = time;
        this.running = running;
    }
    public static String describe(Process p) {
        return p.id + " (remaining: " + p.remaining + ", Priority: " + p.effectivePriority + ")";
    }
}
interface Scheduler {
    SimulationResult run(List<Process> processes, int quantum, int contextSwitch);
}