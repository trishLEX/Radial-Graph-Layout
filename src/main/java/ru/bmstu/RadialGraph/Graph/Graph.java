package ru.bmstu.RadialGraph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
//TODO сделать метод получние всех вершин без определённой
public class Graph {
    private final double VERTEX_R = 0.015;
    public static final double R_OFFSET = 1.0;

    private ArrayList<Vertex> vertices;
    private int size;
    private double VertexR;
    private ArrayList<Double> radials;
    private ArrayList<ArrayList<Vertex>> verticesByDepth;
    private ArrayList<Vertex[]> deleted;
    private Vertex root;
    private int maxDepth;
    private ArrayList<Vertex> center;
    private int radii;
    private int diam;

    private double[][] w;

    public int getMaxDepth() {
        return maxDepth;
    }

    public Graph(int count) {
        this.vertices = new ArrayList<>();
        this.size = count;

        for (int i = 0; i < count; i++) {
            vertices.add(new Vertex(i));
        }

        this.VertexR = VERTEX_R;

        this.radials = new ArrayList<>();
        this.maxDepth = 0;
        this.verticesByDepth = new ArrayList<>();
        this.root = null;
        this.deleted = new ArrayList<>();

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

        this.calculateCenter();
    }

    @Override
    public String toString() {
        String res = "";
        for (Vertex v: vertices) {
            res += "v = " + v +  " depth = " + v.getDepth() + "\n";
            for (Vertex u: v.getChild()) {
                res += "    childs = " + u.getIndex() + " " +
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

    public void fillRadials3() {
        this.getRadials().clear();

        for (Vertex v: this.getVertices()) {
            if (v.getChild().size() != 0)
                this.getRadials().add(v.distTo(v.getChild().get(0)));
            else
                this.getRadials().add(0.0);
        }
    }

    public void fillRadials5() {
        this.getRadials().clear();

        for (int i = 0; i <= this.maxDepth; i++) {
            this.radials.add(root.distTo(this.verticesByDepth.get(i).get(0)));
        }
    }

    private void calculateMaxDepth(Vertex root, int depth) {
        for (Vertex v: root.getChild()) {
            System.out.println(depth);
            v.setDepth(depth + 1);
            if (depth + 1 > maxDepth)
                maxDepth = depth + 1;
            calculateMaxDepth(v, depth + 1);
        }
    }

    public void calculateMaxDepth(Vertex root) {
        //root.setDepth(0);
        calculateMaxDepth(root, 0);

        ArrayList<ArrayList<Vertex>> verticesByDepth = new ArrayList<ArrayList<Vertex>>();

        for (int i = 0; i <= maxDepth; i++)
            verticesByDepth.add(new ArrayList<>());

        for (Vertex v: this.vertices) {
            verticesByDepth.get(v.getDepth()).add(v);
        }

        this.verticesByDepth = verticesByDepth;
    }

    public ArrayList<ArrayList<Vertex>> getVerticesByDepth() {
        return this.verticesByDepth;
    }

    public ArrayList<Vertex> getVerticesByDepth(int depth) {
        return this.verticesByDepth.get(depth);
    }

    public void setVerticesByDepth(ArrayList<ArrayList<Vertex>> verticesByDepth) {
        this.verticesByDepth = verticesByDepth;
    }

    public Vertex getRoot() {
        return root;
    }

    public void setRoot(Vertex root) {
        this.root = root;
    }

    private void bfs(Vertex v) {
        v.setMark(1);

        ArrayDeque<Vertex> queue = new ArrayDeque<>();
        queue.push(v);

        while(!queue.isEmpty()) {
            Vertex u = queue.poll();

            for (Vertex w: u.getChild()) {
                if (w.getMark() == 0) {
                    w.setMark(1);
                    w.setParent(u);
                    w.getChild().remove(u);
                    queue.push(w);
                }
            }
        }
    }


    public void makeTree(Vertex v) {
        v.setRoot(true);
        this.root = v;

        bfs(v);

        ArrayList<Vertex[]> deleted = new ArrayList<>();

        for (Vertex vertex: vertices) {
            ArrayList<Vertex> temp = new ArrayList<>();
            for (Vertex u: vertex.getChild()) {
                if (u.getParent() == vertex) {
                    temp.add(u);
                }
                else {
                    Vertex[] deletedConnection = new Vertex[] {vertex, u};
                    if (!contains(deleted, deletedConnection))
                        deleted.add(deletedConnection);
                }
            }
            vertex.setChild(temp);
        }

        this.deleted = deleted;
    }

    private boolean contains(ArrayList<Vertex[]> deleted, Vertex[] delconn) {
        for (Vertex[] connection: deleted){
            if ((connection[0] == delconn[0] && connection[1] == delconn[1]) || (connection[0] == delconn[1] && connection[1] == delconn[0]))
                return true;
        }

        return false;
    }

    private int eccentricity(Vertex v) {
        BreadthFirstSearch bfs = new BreadthFirstSearch(this, v);
        int max = bfs.getDistTo(0);
        for (int i = 1; i < vertices.size(); i++) {
            max = Math.max(max, bfs.getDistTo(i));
        }
        return max;
    }

    private void graphRadii() {
        int min = eccentricity(vertices.get(0));
        int max = min;
        for (int i = 1; i < vertices.size(); i++) {
            min = Math.min(min, eccentricity(vertices.get(i)));
            max = Math.max(max, eccentricity(vertices.get(i)));
        }
        this.diam = max;
        this.radii = min;
    }

    private void calculateCenter() {
        ArrayList<Vertex> center = new ArrayList<>();
        graphRadii();
        System.out.println("radii = " + radii);
        for (Vertex v: vertices) {
            if (eccentricity(v) == radii)
                center.add(v);
        }
        this.center = center;
    }

    public ArrayList<Vertex[]> getDeleted() {
        return deleted;
    }

    public int getRadii() {
        return radii;
    }

    public int getDiam() {
        return diam;
    }

    public ArrayList<Vertex> getCenter() {
        return this.center;
    }

    public int getSize() {
        return size;
    }
}
