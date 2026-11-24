import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class GanttPanel extends JPanel {
    private static final int UNIT = 30;
    private static final int LEFT = 20;
    private static final int TOP = 30;
    private static final int HEIGHT = 40;
    private SimulationResult result;
    public GanttPanel() {
        setBackground(Color.WHITE);
    }

    public void setResult(SimulationResult result) {
        this.result = result;
        int endTime = 0;
        if (result != null && !result.slices.isEmpty()) {
            endTime = result.slices.get(result.slices.size() - 1).end;
        }
        setPreferredSize(new Dimension(LEFT * 2 + endTime * UNIT + 10, TOP + HEIGHT + 50));
        revalidate();
        repaint();
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (result == null || result.slices.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawString("No result yet. Press Run.", LEFT, TOP);
            return;
        }

        int endTime = 0;
        for (Slice s : result.slices) {
            int x = LEFT + s.start * UNIT;
            int width = (s.end - s.start) * UNIT;

            g.setColor(colorFor(s.label));
            g.fillRect(x, TOP, width, HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(x, TOP, width, HEIGHT);
            g.drawString(s.label, x + width / 2 - 8, TOP + HEIGHT / 2 + 5);
            g.drawString("" + s.start, x - 4, TOP + HEIGHT + 18);
            endTime = s.end;
        }
        g.drawString("" + endTime, LEFT + endTime * UNIT - 4, TOP + HEIGHT + 18);
    }

    private Color colorFor(String label) {
        if (label.equals("CS")) {
            return Color.LIGHT_GRAY;
        }
        Color[] colors = {
                new Color(135, 206, 250), new Color(144, 238, 144), new Color(255, 218, 120),
                new Color(255, 160, 160), new Color(200, 170, 240), new Color(120, 220, 210)
        };
        return colors[Math.abs(label.hashCode()) % colors.length];
    }
    public static void main(String[] args) {
        SimulationResult result = new SJFScheduler().run(TestCases.testCase2(), 3, 1);

        GanttPanel panel = new GanttPanel();
        panel.setResult(result);

        JFrame frame = new JFrame("Gantt chart - SJF, Test Case 2 (CS = 1)");
        frame.add(new JScrollPane(panel));
        frame.setSize(700, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}