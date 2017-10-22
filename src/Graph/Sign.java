package Graph;

public class Sign {
    private final double SIGN_WIDTH = 7 * Vertex.VERTEX_WIDTH;
    private final double SIGN_HEIGHT = 3 * Vertex.VERTEX_HEIGHT;

    private double x, y;
    private double width;
    private double height;

    Sign(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    Sign(double x, double y) {
        this.width = SIGN_WIDTH;
        this.height = SIGN_HEIGHT;
        this.x = x;
        this.y = y - this.height / 2;
    }

    public double getX() {
        return x;
    }

    void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    void setY(double y) {
        this.y = y - this.height / 2;
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
}
