package Algorithms;

import Graph.Graph;
import Graph.Vertex;
import Visualization.GraphVisualization;

import java.util.ArrayList;

public class Calculation {
    private static final int START_INDEX = 0;

    private Graph graph;
    private int type;
    public static int WIDTH = GraphVisualization.WIDTH;
    public static int HEIGHT = GraphVisualization.HEIGHT;

    public Calculation(Graph graph) {
        this.graph = graph;
    }

    private void dfs(Vertex v) {
        v.setMark(1);

        for (Vertex u: v.getChild()) {
            if (u.getMark() == 0) {
                u.setParent(v);         //устанавливаем родителя
                u.getChild().remove(v); //удаляем ссылку на родителя
                dfs(u);
            }
        }
    }

    private void makeTree(Vertex v) {
        v.setRoot(true);

        dfs(v);

        for (Vertex vert: graph.getVertices()) {
            ArrayList<Vertex> temp = new ArrayList<>();
            for (Vertex u: vert.getChild()) {
                if (u.getParent() == vert) {
                    temp.add(u);
                }
            }
            vert.setChild(temp);
        }
    }

    public Graph calculateGraph(int type) {
        this.type = type;

        if (type == 3) {
            makeTree(graph.get(START_INDEX));
            Algorithm3.useAlgorithm(graph);
        }
        //printGraph();
        //System.out.println(graph);
        if (type == 5) {
            makeTree(graph.get(START_INDEX));
             Algorithm5.useAlgorithm(graph);
        }
        //Algorithm3.useAlgorithm(graph);

        if (type == 1) {
            makeTree(graph.get(START_INDEX));
            Algorithm5.useAlgorithm(graph);
            Algorithm1.useAlgorithm(graph);
        }

        for (Vertex v: graph.getVertices()) {
            System.out.println("COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
            System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
        }

        convertCoordinates(graph);

        return graph;
    }

    private void convertCoordinates(Graph graph) {
        for (Vertex v: graph.getVertices()) {
            double sx = v.getSign().getX();
            double sy = v.getSign().getY();

            v.setX(v.getX() / WIDTH);
            v.setY(v.getY() / HEIGHT);
            v.setWidth(v.getWidth() / WIDTH);
            v.setHeight(v.getHeight() / HEIGHT);

            //System.out.println("old " + v.getIndex() + " sign y = " + v.getSign().getY() + " height = " + HEIGHT + " y / height = " + (v.getSign().getY() / HEIGHT));
            v.getSign().setX(sx / WIDTH);
            v.getSign().setY(sy / HEIGHT);
            v.getSign().setWidth(v.getSign().getWidth() / WIDTH);
            v.getSign().setHeight(v.getSign().getHeight() / HEIGHT);

            System.out.println("CONVERTED COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
            System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
        }

        for (int i = 0; i < graph.getRadials().size(); i++) {
            double r = graph.getRadials().get(i) / (WIDTH < HEIGHT? WIDTH : HEIGHT);
            graph.getRadials().set(i, r);
        }
    }

    private void printGraph() {
        for (Vertex v: graph.getVertices()) {
            System.out.println("v = " + v.getIndex());
            for (Vertex u: v.getChild()) {
                System.out.println("    childs = " + u.getIndex() + " " + (u.getParent() != null ? u.getParent().getIndex() : "null"));
            }
        }
    }
}
