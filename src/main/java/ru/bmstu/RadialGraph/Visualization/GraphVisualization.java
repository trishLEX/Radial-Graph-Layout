package ru.bmstu.RadialGraph.Visualization;

import ru.bmstu.RadialGraph.Graph.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GraphVisualization {
    public final static int SIZE = 720;
    public final static int MAX_SIZE = 1000;

    private static Graph makeGraph(String Path, boolean isIndexFromOne, boolean isSigns) {
        try {
            Scanner in = new Scanner(new File(Path));

            int count = in.nextInt();

            Graph graph = new Graph(count, isSigns);

            graph.scanGraph(in, isIndexFromOne, isSigns);

            return graph;
        } catch (FileNotFoundException error) {
            System.err.println("ERROR: File Not Found");
            System.err.println(error.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        boolean isIndexFromOne = false;
        boolean isSigns = false;
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("-i"))
                isIndexFromOne = true;
            else if (args[i].equals("-s"))
                isSigns = true;
        }

        Graph graph = makeGraph(args[0], isIndexFromOne, isSigns);

        if (graph != null) {

            int type = Integer.parseInt(args[1]);

            graph.useAlgorithm(type);

            Drawer drawer = new Drawer(graph, type);

            drawer.startLoop();
        }
    }
}