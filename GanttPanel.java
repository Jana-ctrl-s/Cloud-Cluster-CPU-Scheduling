import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class GanttPanel extends JPanel {
    static Color[] COLORS = { new Color(135, 206, 250), new Color(144, 238, 144), new Color(255, 218, 120),
            new Color(255, 160, 160), new Color(200, 170, 240), new Color(120, 220, 210) };
    SimulationResult r;
    int end;

    GanttPanel(SimulationResult r) {
        this.r = r;
        end = r.slices.get(r.slices.size() - 1).end;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(40 + end * 30, 90));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Slice s : r.slices) {
            int x = 20 + s.start * 30, w = (s.end - s.start) * 30;
            g.setColor(s.label.equals("CS") ? Color.LIGHT_GRAY : COLORS[Math.abs(s.label.hashCode()) % COLORS.length]);
            g.fillRect(x, 20, w, 40);
            g.setColor(Color.BLACK);
            g.drawRect(x, 20, w, 40);
            g.drawString(s.label, x + w / 2 - 8, 45);
            g.drawString("" + s.start, x - 4, 78);
        }
        g.drawString("" + end, 16 + end * 30, 78);
    }

    static String report(SimulationResult r, boolean twoTier) {
        String t = "";
        double tat = 0, wait = 0;
        for (Process p : r.finished) {
            int ta = p.finishTime - p.arrival, w = ta - p.burst;
            tat += ta;
            wait += w;
            t += p.id + ": finish " + p.finishTime + ", turnaround " + ta + ", waiting " + w
                    + ", response " + (p.firstRunTime - p.arrival) + "\n";
        }
        int n = r.finished.size();
        t += String.format(Locale.US, "\nAverage Turnaround Time: %.2f\nAverage Waiting Time: %.2f\n\n", tat / n, wait / n);
        for (Snapshot s : r.snapshots)
            t += "Time " + s.time + ": running " + s.running
                    + (twoTier ? ", Tier 1 " + s.tier1 + ", Tier 2 " + s.tier2 : ", Ready " + s.tier1) + "\n";
        return t;
    }

    public static void main(String[] args) {
        Scheduler[] algos = { new SJFScheduler(), new ACS() };
        JComboBox<String> box = new JComboBox<>(new String[] { "Test Case 1", "Test Case 2", "My Test Case", "My Test Case P2" });
        JTextField qField = new JTextField("3", 3), csField = new JTextField("0", 3);
        JButton run = new JButton("Run");
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 0));
        JPanel[] cols = { new JPanel(new GridLayout(2, 1)), new JPanel(new GridLayout(2, 1)) };
        for (int i = 0; i < 2; i++) {
            cols[i].setBorder(BorderFactory.createTitledBorder(i == 0 ? "SJF" : "ACS"));
            center.add(cols[i]);
        }

        run.addActionListener(e -> {
            int q, cs;
            try {
                q = Integer.parseInt(qField.getText().trim());
                cs = Integer.parseInt(csField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Q and CS must be whole numbers.");
                return;
            }
            int k = box.getSelectedIndex();
            List<Process> ps = k == 0 ? TestCases.testCase1() : k == 1 ? TestCases.testCase2()
                    : k == 2 ? TestCases.myTestCase() : TestCases.myTestCaseP2();
            for (int i = 0; i < 2; i++) {
                SimulationResult r = algos[i].run(ps, q, cs);
                JTextArea text = new JTextArea(report(r, i == 1));
                text.setEditable(false);
                text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                cols[i].removeAll();
                cols[i].add(new JScrollPane(new GanttPanel(r)));
                cols[i].add(new JScrollPane(text));
                cols[i].revalidate();
                cols[i].repaint();
            }
        });

        JPanel top = new JPanel();
        top.add(new JLabel("Test case:")); top.add(box);
        top.add(new JLabel("Q:")); top.add(qField);
        top.add(new JLabel("CS:")); top.add(csField);
        top.add(run);

        JFrame f = new JFrame("CPU Scheduling Simulator");
        f.add(top, BorderLayout.NORTH);
        f.add(center);
        f.setSize(1200, 650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}