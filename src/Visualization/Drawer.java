package Visualization;

import Graph.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

class Drawer {
    private static final int NUMBER_OF_SIDES = 50;
    //private static final int WIDTH = 640;
    //private static final int HEIGHT = 640;
    private static final double[] GRAY = {0.8, 0.8, 0.8};
    private static final double[] RED = {1.0, 0.0, 0.0};
    private static final double[] BLACK = {0.0, 0.0, 0.0};

    private static final String NAME = "Radial Graph";
    private static final int WIDTH = GraphVisualization.WIDTH;
    private static final int HEIGHT = GraphVisualization.HEIGHT;

    private long window;

    private Graph graph;

    private int type;
    //private int width;

    private void background() {
        glClearColor(1, 1, 1, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
    }

    Drawer(Graph graph, int type) {
        this.graph = graph;
        this.type = type;
        //this.width = width;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("unable to initialize GLFW");

        this.window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, NAME, 0, 0);

        if (window == 0) {
            throw new RuntimeException("Failed to create window");
        }

        GLFW.glfwMakeContextCurrent(window);

        GL.createCapabilities();
    }

    private void drawQuads(double x, double y, double width, double height) {
        glVertex2d(x + width / 2, y + height / 2);
        glVertex2d(x - width / 2, y + height / 2);
        glVertex2d(x - width / 2, y - height / 2);
        glVertex2d(x + width / 2, y - height / 2);
    }

    private void drawVertex(double x, double y, double width, double height) {
        glLineWidth(1);
        glBegin(GL_LINE_LOOP);
        {
            glColor3dv(BLACK);

            drawQuads(x, y, width, height);
        }
        glEnd();

        glBegin(GL_QUADS);
        {
            glColor3dv(RED);
//            for (int i = 0; i <= NUMBER_OF_SIDES; i++) {
//                glVertex2d(
//                        (x + r * cos(i * PI * 2 / NUMBER_OF_SIDES)),
//                        (y + r * sin(i * PI * 2 / NUMBER_OF_SIDES))
//                );
//            }
            drawQuads(x, y, width, height);
        }
        glEnd();

    }

    private void drawLine(Vertex v1, Vertex v2) {
        glLineWidth(2);
        glBegin(GL_LINES);
        {
            glColor3dv(BLACK);
            glVertex2d(v1.getX(), v1.getY());
            glVertex2d(v2.getX(), v2.getY());
        }
        glEnd();
    }

    private void drawGraph(Graph graph) {
        int i = 0;
        for (Vertex v: graph.getVertices()) {
            //glLineWidth(2);
            for (Vertex u: v.getChild()) {
                drawVertex(u.getX(), u.getY(), u.getWidth(), u.getHeight());
            }
            drawVertex(v.getX(), v.getY(), v.getWidth(), v.getHeight());

            for (Vertex u: v.getChild()) {
                drawLine(v, u);
            }

            if (type == 3) {
                drawCircles(v, i);
            }

            i++;
        }
    }

    private void drawCircles() {
        glColor3dv(GRAY);
        for (Double r: graph.getRadials()) {
            //System.out.println(r);
            glBegin(GL_LINE_LOOP);
            {
                for (int i = 0; i < NUMBER_OF_SIDES; i++) {
                    glVertex2d(r * cos(i * PI * 2 / NUMBER_OF_SIDES),
                               r * sin(i * PI * 2 / NUMBER_OF_SIDES));
                }
            }
            glEnd();
        }
    }

    private void drawCircles(Vertex v, int k){
        glLineWidth(1);
        glColor3dv(GRAY);
        glBegin(GL_LINE_LOOP);
        {
            for (int i = 0; i < NUMBER_OF_SIDES; i++) {
                glVertex2d(v.getX() + graph.getRadials().get(k) * cos(i * PI * 2 / NUMBER_OF_SIDES),
                           v.getY() + graph.getRadials().get(k) * sin(i * PI * 2 / NUMBER_OF_SIDES));
            }
        }
        glEnd();
    }

    private void draw() {
        glPointSize(1);
        //glLineWidth(2);

        drawGraph(this.graph);
        //System.out.println(this.graph.getRadials());

        if (type == 5) {
            glLineWidth(1);
            drawCircles();
        }
    }

    void startLoop(){
        while (!GLFW.glfwWindowShouldClose(this.window)) {
            background();

            draw();

            GLFW.glfwSwapBuffers(this.window);
            GLFW.glfwPollEvents();
        }
    }
}
