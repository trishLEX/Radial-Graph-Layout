package ru.bmstu.RadialGraph.Graph;

import ru.bmstu.RadialGraph.Algorithms.CentralityDrawingAlgorithm;
import ru.bmstu.RadialGraph.Algorithms.ConcentricCirclesAlgorithm;
import ru.bmstu.RadialGraph.Algorithms.ParentCenteredAlgorithm;
import ru.bmstu.RadialGraph.Calculation.BreadthFirstSearch;
import ru.bmstu.RadialGraph.Visualization.GraphVisualization;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
//TODO сделать метод получние всех вершин без определённой
public class Graph {
    public static final double R_OFFSET = 1.0;
    public static int WIDTH = GraphVisualization.WIDTH;
    public static int HEIGHT = GraphVisualization.HEIGHT;

    private ArrayList<Vertex> vertices;
    private int size;
    private ArrayList<Double> radials;
    private ArrayList<ArrayList<Vertex>> verticesByDepth;
    private ArrayList<Vertex[]> deleted;
    private Vertex root;
    private int maxDepth;
    private ArrayList<Vertex> center;
    private int radii;

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
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public Vertex get(int i) {
        return this.vertices.get(i);
    }

    public void scanGraph(Scanner in) {
        int m = in.nextInt();

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

    public void calculateMaxDepth(Vertex root) {
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

        for (Vertex v: vertices) {
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

        for (int i = 0; i < this.radials.size(); i++) {
            double r = this.radials.get(i) / (WIDTH < HEIGHT? WIDTH : HEIGHT) * 2;
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

        return new double[] {up, down, right, left, right - left, up - down};
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

        if (width > WIDTH || height > HEIGHT) {
            double side = width > height ? width : height;
            if (!isRedraw) {
                WIDTH = HEIGHT = (int) side + 1;
            }
            else {
                double coeff = WIDTH / side;
                for (Vertex v: vertices) {
                    v.setVertexByCartesian(v.getX() * coeff, v.getY() * coeff);
                }
                if (type == 3)
                    this.fillRadialsByParentCentered();
                else
                    this.fillRadialsByConcentricCircle();

                corners = findCorners();

                up = corners[0];
                down = corners[1];
                right = corners[2];
                left = corners[3];
                width = corners[4];
                height = corners[5];

                System.out.println("width = " + width + " height = " + height + " right = " + right + " left = " + left + " up = " + up + " down = " + down);
            }
        }

        translateRight(left, right);
        translateLeft(right, left);
        translateDown(up, down);
        translateUp(down, up);
    }

    private void translateLeft(double right, double left) {
        double offset = 0.0;

        while (right > (double) WIDTH / 2.0 - 1.0 && left > - (double) WIDTH / 2.0 + 1.0) {
            right -= 1.0;
            left -= 1.0;
            offset -= 1.0;
        }

        for (Vertex v: this.vertices) {
            v.setX(v.getX() + offset);
        }

        System.out.println("left = " + left + " right = " + right);
    }

    private void translateRight(double left, double right) {
        double offset = 0.0;

        while (left < - (double) WIDTH / 2.0 + 1.0) {
            left += 1.0;
            offset += 1.0;
        }

        for (Vertex v: this.vertices) {
            v.setX(v.getX() + offset);
        }
    }

    private void translateDown(double up, double down) {
        double offset = 0.0;

        while (up > (double) HEIGHT / 2.0 - 1.0) {
            up -= 1.0;
            offset -= 1.0;
        }

        for (Vertex v: this.vertices) {
            v.setY(v.getY() + offset);
        }
    }

    private void translateUp(double down, double up) {
        double offset = 0.0;

        while (down < - (double) HEIGHT / 2.0 + 1 && up < (double) HEIGHT / 2.0 - 1) {
            down += 1.0;
            up += 1.0;
            offset += 1.0;
        }

        for (Vertex v: this.vertices) {
            v.setY(v.getY() + offset);
        }

        System.out.println("up = " + up + " down = " + down);
    }

    private Vertex findVertex(double x, double y) {
        for (Vertex v: vertices) {

            if (0 <= Math.abs(y - v.getY()) && Math.abs(y - v.getY()) <= v.getHeight() / 2 && 0 <= Math.abs(x - v.getX()) && Math.abs(x - v.getX()) <= v.getWidth() / 2)
                return v;
        }

        return null;
    }

    public void rebuild(double x, double y, int type) {
        System.out.println("x = " + x + " y = " + y);

        Vertex newRoot = findVertex(x, y);

        System.out.println("new root is " + newRoot);

        if (newRoot != null) {
            for (Vertex v : vertices) {
                v.setVertexByCartesian(0, 0);

                if (v.getParent() != null) {
                    v.addChild(v.getParent());
                    v.setParent(null);
                }

                v.setDepth(0);
                v.setRoot(false);
                v.setMark(0);

                v.setWidth(Vertex.VERTEX_WIDTH);
                v.setHeight(Vertex.VERTEX_HEIGHT);

                v.getSign().setX(v.getX());
                v.getSign().setY(y - v.getHeight() / 2);
                v.getSign().setWidth(Sign.SIGN_WIDTH);
                v.getSign().setHeight(Sign.SIGN_HEIGHT);
            }
            for (Vertex[] del : deleted) {
                del[0].addChild(del[1]);
                del[1].addChild(del[0]);
            }

            this.deleted.clear();
            this.radials.clear();
            this.verticesByDepth.clear();
            this.center.clear();
            this.maxDepth = 0;

            makeTree(newRoot);
            System.out.println(this);
            System.out.println("Tree is built");

            if (type == 1) {
                ParentCenteredAlgorithm.useAlgorithm(this);
                CentralityDrawingAlgorithm.useAlgorithm(this);
            }
            else if (type == 2) {
                ConcentricCirclesAlgorithm.useAlgorithm(this);
                CentralityDrawingAlgorithm.useAlgorithm(this);
            }
            else if (type == 3)
                ParentCenteredAlgorithm.useAlgorithm(this);
            else if (type == 4)
                ConcentricCirclesAlgorithm.useAlgorithm(this);

            System.out.println(this);

            System.out.println("RADIALS " + this.radials);

            for (Vertex v: this.getVertices()) {
                System.out.println("COORDINATES " + v.getIndex() + " (" + v.getX() + "," + v.getY() + ") w = " + v.getWidth() + " h = " + v.getHeight());
                System.out.println("            " + "sign: (" + v.getSign().getX() + "," + v.getSign().getY() + ") w = " + v.getSign().getWidth() + " h = " + v.getSign().getHeight());
            }

            this.convertCoordinates(true, type);
        }
    }
}
