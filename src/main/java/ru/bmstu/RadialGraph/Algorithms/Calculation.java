package ru.bmstu.RadialGraph.Algorithms;

import ru.bmstu.RadialGraph.Graph.Graph;
import ru.bmstu.RadialGraph.Graph.Vertex;
import ru.bmstu.RadialGraph.Visualization.GraphVisualization;

final public class Calculation {
    private static final int START_INDEX = 0;

    private Graph graph;
    public static int WIDTH = GraphVisualization.WIDTH;
    public static int HEIGHT = GraphVisualization.HEIGHT;

    public Calculation(Graph graph) {
        this.graph = graph;
    }

    public Graph calculateGraph(int type) {
        if (type == 3) {
            //graph.makeTree(graph.get(START_INDEX));
            graph.makeTree(graph.getCenter().get(0));
            System.out.println("Tree is built");
            Algorithm3.useAlgorithm(graph);
        }

        if (type == 5) {
            //graph.makeTree(graph.get(START_INDEX));
            graph.makeTree(graph.getCenter().get(0));
            System.out.println("Tree is built");
            Algorithm5.useAlgorithm(graph);
        }

        if (type == 1) {
            //graph.makeTree(graph.get(START_INDEX));
            graph.makeTree(graph.getCenter().get(0));
            Algorithm3.useAlgorithm(graph);
            Algorithm1.useAlgorithm(graph);
        }

        System.out.println(graph);

        System.out.println("RADIALS " + graph.getRadials());

        for (Vertex v: graph.getVertices()) {
            System.out.println("COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
            System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
        }

        convertCoordinates(graph);

        return graph;
    }

    private void convertCoordinates(Graph graph) {
        calculateWidthAndHeight(graph);

        for (Vertex v: graph.getVertices()) {
            double sx = v.getSign().getX();
            double sy = v.getSign().getY();

            v.setX(v.getX() / WIDTH * 2);
            v.setY(v.getY() / HEIGHT * 2);
            v.setWidth(v.getWidth() / WIDTH * 2);
            v.setHeight(v.getHeight() / HEIGHT * 2);

            v.getSign().setX(sx / WIDTH * 2);
            v.getSign().setY(sy / HEIGHT * 2);
            v.getSign().setWidth(v.getSign().getWidth() / WIDTH * 2);
            v.getSign().setHeight(v.getSign().getHeight() / HEIGHT * 2);

            System.out.println("CONVERTED COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
            System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
        }

        for (int i = 0; i < graph.getRadials().size(); i++) {
            double r = graph.getRadials().get(i) / (WIDTH < HEIGHT? WIDTH : HEIGHT) * 2;
            graph.getRadials().set(i, r);
        }
    }

    private void calculateWidthAndHeight(Graph graph) {
        double up    = Double.NEGATIVE_INFINITY;
        double down  = Double.POSITIVE_INFINITY;
        double right = up;
        double left  = down;

        for (Vertex v: graph.getVertices()) {
            up = Math.max(v.getY() + v.getHeight() / 2, up);
            down = Math.min(v.getSign().getY() - v.getSign().getHeight() / 2, down);
            right = Math.max(v.getSign().getX() + v.getSign().getWidth() / 2, right);
            left = Math.min(v.getSign().getX() - v.getSign().getWidth() / 2, left);
        }

        double width  = right - left;
        double height = up - down;

        System.out.println("width = " + width + " height = " + height + " right = " + right + " left = " + left + " up = " + up + " down = " + down);

        System.out.println("width = " + width + " height = " + height + " right = " + right + " left = " + left + " up = " + up + " down = " + down);

        if (width > WIDTH || height > HEIGHT) {
            double side = width > height? width : height;
            WIDTH = HEIGHT = (int) side + 1;
            System.out.println("HERE HERE HERE");
        }

        translateRight(graph, left, right);
        translateLeft(graph, right, left);
        translateDown(graph, up, down);
        translateUp(graph, down, up);
    }

    private void translateLeft(Graph graph, double right, double left) {
        double offset = 0.0;
        while (right > (double) WIDTH / 2.0 - 1.0 && left > - (double) WIDTH / 2.0 + 1.0) {
            right -= 1.0;
            left -= 1.0;
            offset -= 1.0;
        }

        for (Vertex v: graph.getVertices()) {
            v.setX(v.getX() + offset);
        }

        System.out.println("left = " + left + " right = " + right);
    }

    private void translateRight(Graph graph, double left, double right) {
        double offset = 0.0;
        while (left < - (double) WIDTH / 2.0 + 1.0 && right < (double) WIDTH / 2.0 - 1.0) {
            left += 5.0;
            offset += 5.0;
        }

        for (Vertex v: graph.getVertices()) {
            v.setX(v.getX() + offset);
        }
    }

    private void translateDown(Graph graph, double up, double down) {
        double offset = 0.0;
        while (up > (double) HEIGHT / 2.0 - 1.0 && down > - (double) HEIGHT / 2.0 + 1.0) {
            up -= 5.0;
            offset -= 5.0;
        }

        for (Vertex v: graph.getVertices()) {
            v.setY(v.getY() + offset);
        }
    }

    private void translateUp(Graph graph, double down, double up) {
        double offset = 0.0;
        while (down < - (double) HEIGHT / 2.0 + 1 && up < (double) HEIGHT / 2.0 - 1) {
            down += 1.0;
            up += 1.0;
            offset += 1.0;
        }

        for (Vertex v: graph.getVertices()) {
            v.setY(v.getY() + offset);
        }

        System.out.println("up = " + up + " down = " + down);
    }
}