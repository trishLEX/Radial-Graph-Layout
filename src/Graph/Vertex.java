package Graph;

import java.util.ArrayList;

/* x и y - координаты центра вершины */
public class Vertex {
    static final double VERTEX_WIDTH = 14.0;
    static final double VERTEX_HEIGHT = 14.0;

    private ArrayList<Vertex> child;
    private Vertex parent;
    private double x, y;
    private int index;
    private int mark;
    private boolean isRoot;
    private int depth;
    private double r;
    private double angle;
    private double width, height;
    private Sign sign;

    Vertex(int i) {
        this.child = new ArrayList<>();
        this.parent = null;
        this.x = this.y = 0;
        this.index = i;
        this.depth = 0;

        this.mark = 0;
        this.isRoot = false;

        this.r = 0;
        this.angle = 0;

        this.width = VERTEX_WIDTH;
        this.height = VERTEX_HEIGHT;

        this.sign = new Sign(x, y - height / 2);
    }

    protected Vertex() {
        this.child = new ArrayList<>();
        this.parent = null;
        this.x = this.y = 0;
        this.index = 0;
        this.depth = 0;

        this.mark = 0;
        this.isRoot = false;

        this.r = 0;
        this.angle = 0;

        this.width = this.height = 0.02;

        this.sign = new Sign(x, y - height / 2 - Sign.SIGN_HEIGHT / 2);
    }

    public ArrayList<Vertex> getChild() {
            return child;
    }

    public void setChild(ArrayList<Vertex> child) {
        this.child = child;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        this.sign.setX(x);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        this.sign.setY(y - VERTEX_HEIGHT / 2 - Sign.SIGN_HEIGHT / 2);
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Sign getSign() {
        return this.sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    private ArrayList<Double[]> getPoints() {
        double width2 = this.width / 2;
        double height2 = this.height / 2;
        double xs = this.sign.getX();
        double ys = this.sign.getY();
        double sWidth2 = this.sign.getWidth() / 2;
        double sHeight2 = this.sign.getHeight() / 2;

        ArrayList<Double[]> thisPoints = new ArrayList<>();

        thisPoints.add(new Double[]{x + width2, y + height2});
        thisPoints.add(new Double[]{x - width2, y + height2});
        thisPoints.add(new Double[]{x - width2, y - height2});
        thisPoints.add(new Double[]{x + width2, y - height2});

        thisPoints.add(new Double[]{xs - sWidth2, ys + sHeight2});
        thisPoints.add(new Double[]{xs - sWidth2, ys - sHeight2});
        thisPoints.add(new Double[]{xs + sWidth2, ys - sHeight2});
        thisPoints.add(new Double[]{xs + sWidth2, ys + sHeight2});

        return thisPoints;
    }

    @Override
    public String toString() {
        String res = "";
        res += this.index;
        return res;
    }

    public boolean isIn(Vertex v) {
        if (v == this)
            return false;
        //if (index == 3 && v.getIndex() == 4) {
            //System.out.println("index = 3 points:");
            //for (Double[] p: this.getPoints())
            //    System.out.println("(" + p[0] + "," + p[1] + ")");
            //System.out.println("\n");
            //System.out.println("index = 4 points:");
            //for (Double[] p: v.getPoints())
            //    System.out.println("(" + p[0] + "," + p[1] + ")");
        //}
        //boolean res1 = false;
        //boolean res2 = false;
        ArrayList<Double[]> vPoints = v.getPoints();

        for (Double[] point: this.getPoints()) {
            double px = point[0];
            double py = point[1];
            if (px < vPoints.get(0)[0] && px > vPoints.get(1)[0] && py < vPoints.get(0)[1] && py > vPoints.get(3)[1]) {
                return true;
            }
        }

        for (Double[] point: this.getPoints()) {
            double px = point[0];
            double py = point[1];
            if (px < vPoints.get(7)[0] && px > vPoints.get(4)[0] && py < vPoints.get(7)[1] && py > vPoints.get(6)[1]) {
                return true;
            }
        }

        return false;
    }

    public void setVertex(double r, double angle) {
        this.r = r;
        this.angle = angle;
        this.setX(r * Math.cos(angle));
        this.setY(r * Math.sin(angle));
    }
}
