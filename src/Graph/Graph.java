package Graph;

import java.util.ArrayList;
import java.util.Scanner;

public class Graph {
    private ArrayList<Vertex> vertices;
    private double VertexR;
    private ArrayList<Double> radials;
    private double[][] w;

    public Graph(int count) {
        this.vertices = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            vertices.add(new Vertex(i));
        }

        this.VertexR = 0.015;

        this.radials = new ArrayList<>();

        this.w = new double[count][count];
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
    }

    public double getVertexR() {
        return VertexR;
    }

    public void setVertexR(double vertexR) {
        this.VertexR = vertexR;
    }

    public Vertex get(int i) {
        return this.vertices.get(i);
    }

    public double[][] getW() {
        return w;
    }

    public void scanGraph(Scanner in) {
        int m = in.nextInt();

        for (int i = 0; i < m; i++) {
            int x = in.nextInt();
            int y = in.nextInt();

            vertices.get(x).getChild().add(vertices.get(y));
            vertices.get(y).getChild().add(vertices.get(x));
            w[x][y] = w[y][x] = 1.0;
        }
    }

    @Override
    public String toString() {
        String res = "";
        for (Vertex v: vertices) {
            res += "v = " + v.getIndex() +  " depth = " + v.getDepth() + "\n";
            for (Vertex u: v.getChild()) {
                res += "    childs = " + u.getIndex() + " " +
                        //(u.getParent() != null ? u.getParent().getIndex() : "null") +
                        " depth = " + u.getDepth() + "\n";
            }
        }

        return res;
    }

    public ArrayList<Double> getRadials() {
        return radials;
    }

    public void setRadials(ArrayList<Double> radials) {
        this.radials = radials;
    }
}
