import java.util.ArrayList;
import java.util.List;
public class TestCases {
    public static List<Process> testCase1() {
        List<Process> list = new ArrayList<>();
        list.add(new Process("P1", 0, 4, 2));
        list.add(new Process("P2", 1, 7, 3));
        list.add(new Process("P3", 3, 2, 1));
        return list;
    }
    public static List<Process> testCase2() {
        List<Process> list = new ArrayList<>();
        list.add(new Process("P1", 0, 5, 4));
        list.add(new Process("P2", 1, 6, 2));
        list.add(new Process("P3", 4, 4, 3));
        return list;
    }
    public static List<Process> myTestCase() {
        List<Process> list = new ArrayList<>();
        list.add(new Process("P1", 0, 2, 3));
        list.add(new Process("P2", 1, 9, 2));
        list.add(new Process("P3", 2, 6, 1));
        list.add(new Process("P4", 5, 1, 4));
        return list;
    }
}