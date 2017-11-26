package ru.bmstu.RadialGraph.Calculation;

import ru.bmstu.RadialGraph.Algorithms.ConcentricCirclesAlgorithm;
import ru.bmstu.RadialGraph.Algorithms.CentralityDrawingAlgorithm;
import ru.bmstu.RadialGraph.Algorithms.ParentCenteredAlgorithm;
import ru.bmstu.RadialGraph.Graph.Graph;
import ru.bmstu.RadialGraph.Graph.Vertex;
import ru.bmstu.RadialGraph.Visualization.GraphVisualization;

final public class Calculation {
    private static final int START_INDEX = 0;
    public static int WIDTH = GraphVisualization.WIDTH;
    public static int HEIGHT = GraphVisualization.HEIGHT;

    private Graph graph;

    public Calculation(Graph graph) {
        this.graph = graph;
    }

    public Graph calculateGraph(int type) {
        if (type == 1) {
            //graph.makeTree(graph.get(START_INDEX));
            graph.makeTree(graph.getCenter().get(0));
            System.out.println("Tree is built");

            ParentCenteredAlgorithm.useAlgorithm(graph);
            CentralityDrawingAlgorithm.useAlgorithm(graph);
        }
        else if (type == 2) {
            //graph.makeTree(graph.get(START_INDEX));
            graph.makeTree(graph.getCenter().get(0));
            System.out.println("Tree is built");

            ConcentricCirclesAlgorithm.useAlgorithm(graph);
            CentralityDrawingAlgorithm.useAlgorithm(graph);
        }
        else if (type == 3) {
            //graph.makeTree(graph.get(START_INDEX));
            graph.makeTree(graph.getCenter().get(0));
            System.out.println("Tree is built");

            ParentCenteredAlgorithm.useAlgorithm(graph);
        }
        else if (type == 4) {
            //graph.makeTree(graph.get(START_INDEX));
            graph.makeTree(graph.getCenter().get(0));
            System.out.println("Tree is built");

            ConcentricCirclesAlgorithm.useAlgorithm(graph);
        }
        else throw new RuntimeException("Wrong number of Algorithm");

        System.out.println(graph);

        System.out.println("RADIALS " + graph.getRadials());

        for (Vertex v: graph.getVertices()) {
            System.out.println("COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
            System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
        }

        graph.convertCoordinates();

        return graph;
    }
}