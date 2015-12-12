package basicgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import util.GraphLoader;

public class GraphGrader {
    private String feedback;

    private int correct;

    private static final int TESTS = 16;

    public static String printList(List<Integer> lst) {
        String res = "";
        for (int i : lst) {
            res += i + "-";
        }
        return res.substring(0, res.length() - 1);
    }

    public static String printOutput(double score, String feedback) {
        return "Score: " + score + "\nFeedback: " + feedback;
    }

    public static String appendFeedback(int num, String test) {
        return "\n** Test #" + num + ": " + test + "...";
    }

    public static void main(String[] args) {
        GraphGrader grader = new GraphGrader();
        grader.run();
    }

    public void runTest(int i, String desc, int start, List<Integer> corr) {
        GraphAdjList lst = new GraphAdjList();
        GraphAdjMatrix mat = new GraphAdjMatrix();
        
        feedback += "\n\nGRAPH: " + desc;
        feedback += appendFeedback(i * 2 - 1, "Testing adjacency list"); 

        GraphLoader.loadGraph("data/graders/mod1/graph" + i + ".txt", lst);
        List<Integer> result = lst.getDistance2(start);
        judge(result, corr);
 
        feedback += appendFeedback(i * 2, "Testing adjacency matrix");
        GraphLoader.loadGraph("data/graders/mod1/graph" + i + ".txt", mat);
        result = mat.getDistance2(start);
        judge(result, corr);
    }

    public void runSpecialTest(int i, String file, String desc, int start, List<Integer> corr, String type) {
        GraphAdjList lst = new GraphAdjList();
        GraphAdjMatrix mat = new GraphAdjMatrix();

        String prefix = "data/graders/mod1/";

        feedback += "\n\n" + desc;
        feedback += appendFeedback(i * 2 - 1, "Testing adjacency list");

        if (type.equals("road")) {
            GraphLoader.loadRoadMap(prefix + file, lst);
            GraphLoader.loadRoadMap(prefix + file, mat);
        } else if (type.equals("air")) {
            GraphLoader.loadRoutes(prefix + file, lst);
            GraphLoader.loadRoutes(prefix + file, mat);
        }

        List<Integer> result = lst.getDistance2(start);
        judge(result, corr);

        feedback += appendFeedback(i * 2, "Testing adjacency matrix");
        result = mat.getDistance2(start);
        judge(result, corr);
    }

    public void judge(List<Integer> result, List<Integer> corr) {
        if (result.size() != corr.size() || !result.containsAll(corr)) {
            feedback += "FAILED. Expected " + printList(corr) + ", got " + printList(result) + ". ";
            if (result.size() > corr.size()) {
                feedback += "Make sure you aren't including vertices of distance 1. ";
            }
            if (result.size() < corr.size()) { 
                feedback += "Make sure you're exploring all possible paths. ";
            }
        } else {
            feedback += "PASSED.";
            correct++;
        }
    }

    public ArrayList<Integer> readCorrect(String file) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/graders/mod1/" + file));
            String next;
            while ((next = br.readLine()) != null) {
                ret.add(Integer.parseInt(next));
            }
        } catch (Exception e) {
            feedback += "\nCould not open answer file! Please submit a bug report.";
        }
        return ret;
    }

    public void run() {
        feedback = "";
        correct = 0;
        ArrayList<Integer> correctAns;

        try {
            correctAns = new ArrayList<Integer>();
            correctAns.add(7);
            runTest(1, "Straight line (0->1->2->3->...)", 5, correctAns);

            correctAns = new ArrayList<Integer>();
            correctAns.add(4);
            correctAns.add(6);
            correctAns.add(8);
            runTest(2, "Undirected straight line (0<->1<->2<->3<->...)", 6, correctAns);

            correctAns = new ArrayList<Integer>();
            correctAns.add(0);
            runTest(3, "Star graph - 0 is connected in both directions to all nodes except itself (starting at 0)", 0, correctAns);

            correctAns = new ArrayList<Integer>();
            for (int i = 1; i < 10; i++)
                correctAns.add(i);
            runTest(4, "Star graph (starting at 5)", 5, correctAns);
            
            correctAns = new ArrayList<Integer>();
            for (int i = 6; i < 11; i++)
                correctAns.add(i);
            runTest(5, "Star graph - Each 'arm' consists of two undirected edges leading away from 0 (starting at 0)", 0, correctAns);

            correctAns = new ArrayList<Integer>();
            runTest(6, "Same graph as before (starting at 5)", 5, correctAns);

            correctAns = readCorrect("ucsd.map.twoaway");
            runSpecialTest(7, "ucsd.map", "UCSD MAP: Intersections around UCSD", 3, correctAns, "road");

            correctAns = readCorrect("routesUA.dat.twoaway");
            runSpecialTest(8, "routesUA.dat", "AIRLINE MAP: Airplane routes around the world", 6, correctAns, "air");

            if (correct == TESTS)
                feedback = "All tests passed. Great job!" + feedback;
            else
                feedback = "Some tests failed. Check your code for errors, then try again:" + feedback;

        } catch (Exception e) {
            feedback += "\nError during runtime: " + e;
        }
            
        System.out.println(printOutput((double)correct / TESTS, feedback));
    }
}