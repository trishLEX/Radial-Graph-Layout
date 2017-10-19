package Visualization;

import Algorithms.Calculation;
import Graph.*;

import java.util.Scanner;

public class GraphVisualization {
    private static int type;

    public static final int WIDTH = 640;
    public static final int HEIGHT = 640;

    private static Graph makeGraph() {
        Scanner in = new Scanner(System.in);

        int count = in.nextInt();

        Graph graph = new Graph(count);

        graph.scanGraph(in);

        type = in.nextInt();

        return graph;
    }

    public static void main(String[] args) {
        Graph graph = makeGraph();

        Calculation calc = new Calculation(graph);
        graph = calc.calculateGraph(type);

        Drawer drawer = new Drawer(graph, type);

        drawer.startLoop();
    }
}