package Algorithms;

import Graph.Graph;
import Graph.Vertex;
import Visualization.GraphVisualization;

import java.util.ArrayList;

public class Calculation {
    private static final int START_INDEX = 0;

    private Graph graph;
    private static final int WIDTH = GraphVisualization.WIDTH;
    private static final int HEIGHT = GraphVisualization.HEIGHT;

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
            System.out.println("COORDINATES " + v.getIndex() + " " + v.getX() + " " + v.getY());
        }

        convertCoordinates(graph);

        return graph;
    }
    private void convertCoordinates(Graph graph) {
        for (Vertex v: graph.getVertices()) {
            v.setX(v.getX() / WIDTH);
            v.setY(v.getY() / HEIGHT);
            System.out.println("CONVERTED COORDINATES " + v.getIndex() + " " + v.getX() + " " + v.getY());
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
