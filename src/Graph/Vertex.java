package Graph;

import java.util.ArrayList;

/* x и y - координаты центра вершины */
public class Vertex {
    static final double VERTEX_WIDTH = 0.02;
    static final double VERTEX_HEIGHT = 0.02;

    private ArrayList<Vertex> child;
    private Vertex parent;
    protected double x, y;
    protected int index;
    protected int mark;
    protected boolean isRoot;
    protected int depth;
    //private double r;
    //private double angle;
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

        //this.r = 0;
        //this.angle = 0;

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

        this.width = this.height = 0.02;

        this.sign = new Sign(x, y - height / 2);
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
        this.sign.setY(y - VERTEX_HEIGHT / 2);
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

//    public double getR() {
//        return r;
//    }
//
//    public void setR(double r) {
//        this.r = r;
//    }
//
//    public double getAngle() {
//        return angle;
//    }
//
//    public void setAngle(double angle) {
//        this.angle = angle;
//    }

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

    @Override
    public String toString() {
        String res = "";
        res += this.index;
        return res;
    }
}
