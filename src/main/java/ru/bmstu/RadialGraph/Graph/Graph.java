package ru.bmstu.RadialGraph.Graph;

import org.joml.Vector2d;
import ru.bmstu.RadialGraph.Algorithms.CentralityDrawingAlgorithm;
import ru.bmstu.RadialGraph.Algorithms.ConcentricCirclesAlgorithm;
import ru.bmstu.RadialGraph.Algorithms.ParentCenteredAlgorithm;
import ru.bmstu.RadialGraph.Calculation.BreadthFirstSearch;
import ru.bmstu.RadialGraph.Visualization.GraphVisualization;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;

public class Graph {
    public final static double R_OFFSET = 1.0;

    private final static int MAX_SIZE = GraphVisualization.MAX_SIZE;
    private final static boolean SIGNS = GraphVisualization.SIGNS;

    private int SIZE = GraphVisualization.SIZE;

    private ArrayList<Vertex> vertices;
    private int size;
    private ArrayList<Double> radials;
    private ArrayList<ArrayList<Vertex>> verticesByDepth;
    private ArrayList<Vertex[]> deleted;
    private Vertex root;
    private int maxDepth;
    private ArrayList<Vertex> center;
    private int radii;
    private boolean isRedraw;

    public int getMaxDepth() {
        return maxDepth;
    }

    public Graph(int count) {
        this.vertices = new ArrayList<>();
        this.size = count;

        for (int i = 0; i < count; i++) {
            vertices.add(new Vertex(i));
        }

        this.radials = new ArrayList<>();
        this.maxDepth = 0;
        this.verticesByDepth = new ArrayList<>();
        this.root = null;
        this.deleted = new ArrayList<>();
        this.isRedraw = false;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public Vertex get(int i) {
        return this.vertices.get(i);
    }

    public void scanGraph(Scanner in) {
        int m = in.nextInt();

        if (SIGNS) {
            for (int i = 0; i < size; i++) {
                String sign = in.nextLine();
                String[] words = sign.split(" ");
                int width = 0;
                int height = sign.length() == 0 ? 0 : words.length;
                for (String word: words) {
                    width = Math.max(word.length(), width);
                }

                vertices.get(i).getSign().setWidth(width * Vertex.SIZE_OF_LETTER + (width == 0 ? 0 : 3));
                vertices.get(i).getSign().setHeight(height * (Vertex.SIZE_OF_LETTER + 3));
            }
        }

        for (int i = 0; i < m; i++) {
            int x = in.nextInt();
            int y = in.nextInt();

            vertices.get(x).getChild().add(vertices.get(y));
            vertices.get(y).getChild().add(vertices.get(x));
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

    public void fillRadialsByParentCentered() {
        this.radials.clear();

        for (Vertex v: this.vertices) {
            if (v.getChild().size() != 0)
                this.radials.add(v.distTo(v.getChild().get(0)));
            else
                this.radials.add(0.0);
        }
    }

    public ArrayList<Vertex[]> getDeleted() {
        return deleted;
    }

    public ArrayList<Vertex> getCenter() {
        return this.center;
    }

    public int getSize() {
        return size;
    }

    public void fillRadialsByConcentricCircle() {
        this.radials.clear();

        for (int i = 0; i <= this.maxDepth; i++) {
            this.radials.add(root.distTo(this.verticesByDepth.get(i).get(0)));
        }
    }

    private void calculateMaxDepth(Vertex root, int depth) {
        for (Vertex v: root.getChild()) {

            v.setDepth(depth + 1);
            if (depth + 1 > maxDepth)
                maxDepth = depth + 1;

            calculateMaxDepth(v, depth + 1);
        }
    }

    private void calculateMaxDepth(Vertex root) {
        calculateMaxDepth(root, 0);

        ArrayList<ArrayList<Vertex>> verticesByDepth = new ArrayList<>();

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

    public Vertex getRoot() {
        return root;
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

    private void checkForConnections() {
        int count = 0;
        for (Vertex v: vertices)
            if (v.getDepth() == 0)
                count++;

        if (count != 1)
            throw new RuntimeException("The number of connected component is more than one");
    }

    private void makeTree(Vertex v) {
        if (v == null)
            v = this.center.get(0);

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

        System.out.println(this);

        calculateMaxDepth(this.root);

        checkForConnections();

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

        for (int i = 1; i < size; i++) {
            max = Math.max(max, bfs.getDistTo(i));
        }

        return max;
    }

    private void graphRadii() {
        int min = eccentricity(vertices.get(0));

        for (int i = 1; i < size; i++) {
            min = Math.min(min, eccentricity(vertices.get(i)));
        }

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

    public void convertCoordinates(boolean isRedraw, int type) {
        this.calculateWidthAndHeight(isRedraw, type);

        System.out.println("final Graph:\n" + this);

        for (Vertex v: vertices) {
            double sx = v.getSign().getX();
            double sy = v.getSign().getY();

            v.setX(v.getX() / SIZE * 2);
            v.setY(v.getY() / SIZE * 2);
            v.setWidth(v.getWidth() / SIZE * 2);
            v.setHeight(v.getHeight() / SIZE * 2);

            v.getSign().setX(sx / SIZE * 2);
            v.getSign().setY(sy / SIZE * 2);
            v.getSign().setWidth(v.getSign().getWidth() / SIZE * 2);
            v.getSign().setHeight(v.getSign().getHeight() / SIZE * 2);

            System.out.println("CONVERTED COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
            System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
        }

        for (int i = 0; i < this.radials.size(); i++) {
            double r = this.radials.get(i) / SIZE * 2;
            this.radials.set(i, r);
        }
    }

    private double[] findCorners() {
        double up    = Double.NEGATIVE_INFINITY;
        double down  = Double.POSITIVE_INFINITY;
        double right = up;
        double left  = down;

        for (Vertex v: vertices) {
            up = Math.max(v.getY() + v.getHeight() / 2, up);
            down = Math.min(v.getSign().getY() - v.getSign().getHeight() / 2, down);
            right = Math.max(v.getSign().getX() + v.getSign().getWidth() / 2, right);
            left = Math.min(v.getSign().getX() - v.getSign().getWidth() / 2, left);
        }

        return new double[] {up + 5, down - 5, right + 5, left - 5, right - left + 10, up - down + 10};
    }

    private void resizeCoords(double coefficient, int type) {
        for (Vertex v : vertices) {
            v.setVertexByCartesian(v.getX() * coefficient, v.getY() * coefficient);
            v.setHeight(v.getHeight() * coefficient);
            v.setWidth(v.getWidth() * coefficient);
            v.getSign().setHeight(v.getSign().getHeight() * coefficient);
            v.getSign().setWidth(v.getSign().getWidth() * coefficient);
        }

        if (type == 3)
            this.fillRadialsByParentCentered();
        else
            this.fillRadialsByConcentricCircle();
    }

    private void calculateWidthAndHeight(boolean isRedraw, int type) {
        double[] corners = findCorners();

        double up = corners[0];
        double down = corners[1];
        double right = corners[2];
        double left = corners[3];
        double width = corners[4];
        double height = corners[5];

        System.out.println("width = " + width + " height = " + height + " right = " + right + " left = " + left + " up = " + up + " down = " + down);

        if (width > SIZE || height > SIZE) {
            double side = width > height ? width : height;

            if (!isRedraw) {
                SIZE = (int) side + 1;

                if (side > MAX_SIZE)
                    SIZE = MAX_SIZE;
            }

            double resizeCoeff = SIZE / side;

            resizeCoords(resizeCoeff, type);
        }

        corners = findCorners();
        up = corners[0];
        down = corners[1];
        right = corners[2];
        left = corners[3];
        width = corners[4];
        height = corners[5];
        System.out.println("width = " + width + " height = " + height + " right = " + right + " left = " + left + " up = " + up + " down = " + down);

        Vector2d vectorToCenter = new Vector2d(-(left + right) / 2, -(down + up) / 2);
        translate(vectorToCenter);

        corners = findCorners();
        up = corners[0];
        down = corners[1];
        right = corners[2];
        left = corners[3];
        width = corners[4];
        height = corners[5];
        System.out.println("width = " + width + " height = " + height + " right = " + right + " left = " + left + " up = " + up + " down = " + down);
    }

    private void translate(Vector2d w) {
        for (Vertex v: vertices) {
            v.translate(w);
        }
    }

    private Vertex findVertex(double x, double y) {
        for (Vertex v: vertices) {

            if (    0 <= Math.abs(y - v.getY()) &&
                    Math.abs(y - v.getY()) <= v.getHeight() / 2 &&
                    0 <= Math.abs(x - v.getX()) &&
                    Math.abs(x - v.getX()) <= v.getWidth() / 2 ||
                            0 <= Math.abs(y - v.getSign().getY()) &&
                            Math.abs(y - v.getSign().getY()) <= v.getSign().getHeight() / 2 &&
                            0 <= Math.abs(x - v.getSign().getX()) &&
                            Math.abs(x - v.getSign().getX()) <= v.getSign().getWidth() / 2)
                return v;
        }

        return null;
    }

    public void rebuild(double x, double y, int type) {
        System.out.println("x = " + x + " y = " + y);

        Vertex newRoot = findVertex(x, y);

        System.out.println("new root is " + newRoot);

        if (newRoot != null) {
            this.root = newRoot;

            for (Vertex v : vertices) {
                v.clear();
            }

            this.clear();
            this.isRedraw = true;

            useAlgorithm(type);
        }
    }

    public void rebuild(int type) {
            for (Vertex v : vertices) {
                v.clear();
            }

            this.clear();
            this.isRedraw = true;

            useAlgorithm(type);
    }

    public void useAlgorithm(int type) {
        this.makeTree(this.root);
        System.out.println(this);
        System.out.println("Tree is built");
        System.out.println("TYPE = " + type);

        if (type == 1) {
            ParentCenteredAlgorithm.useAlgorithm(this);
            CentralityDrawingAlgorithm.useAlgorithm(this);
        } else if (type == 2) {
            ConcentricCirclesAlgorithm.useAlgorithm(this);
            CentralityDrawingAlgorithm.useAlgorithm(this);
        } else if (type == 3) {
            ParentCenteredAlgorithm.useAlgorithm(this);
        } else if (type == 4) {
            ConcentricCirclesAlgorithm.useAlgorithm(this);
        } else throw new RuntimeException("Wrong number of Algorithm");


        System.out.println(this);

        System.out.println("RADIALS " + this.radials);

        for (Vertex v : this.getVertices()) {
            System.out.println("COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
            System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
        }

        this.convertCoordinates(isRedraw, type);
    }

    private void clear() {
        for (Vertex[] del : deleted) {
            del[0].addChild(del[1]);
            del[1].addChild(del[0]);
        }

        this.deleted.clear();
        this.radials.clear();
        this.verticesByDepth.clear();
        this.center.clear();
        this.maxDepth = 0;
    }

    public int getWindowSize() {
        return this.SIZE;
    }
}
