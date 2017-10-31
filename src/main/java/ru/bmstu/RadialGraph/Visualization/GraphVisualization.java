package main.java.ru.bmstu.RadialGraph.Visualization;

import main.java.ru.bmstu.RadialGraph.Algorithms.Calculation;
import main.java.ru.bmstu.RadialGraph.Graph.*;

import java.util.Scanner;

public class GraphVisualization {
    private static int type;

    public static int WIDTH = 720;
    public static int HEIGHT = 720;

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