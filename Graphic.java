import java.awt.*;
import java.awt.image.BufferedImage;

public class Graphic {
    private Canvas canvas;

    private BufferedImage background;
    private int thickness;

    public Graphic(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawDot(int x, int y, Color color) {
        getInfo();

        Graphics g = background.getGraphics();
        g.setColor(color);
        if(thickness == 1) {
            g.drawRect(x, y, 1, 1);
        } else {
            g.fillOval(x - thickness/2, y - thickness/2, thickness, thickness);
        }

        g.dispose();
        canvas.setCanvas(background);
    }
    public void drawLine(int x1, int x2, int y1, int y2, Color color) {
        getInfo();

        int diffX = Math.abs(x2-x1);
        int diffY = Math.abs(y2-y1);

        Graphics2D g2d = background.createGraphics();
        g2d.setColor(color);

        //Find the angle of the triangle created by diffX and diffY
        double angle = Math.atan2(x2-x1, y2-y1);
        g2d.rotate(0 - angle, x1, y1);
        //Length of the line is equal to hypotenuse of the triangle created by diffX and diffY
        int length = (int) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
        g2d.fillRect(x1, y1, thickness, length);

        g2d.dispose();
        canvas.setCanvas(background);
    }
    public void drawOval(int x1, int x2, int y1, int y2, Color color) {
        getInfo();

        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        //determine start point based on higher x/y value
        int sX = Math.min(x1, x2);
        int sY = Math.min(y1, y2);

        Graphics g = background.getGraphics();
        g.setColor(color);
        for(int i = 0; i < thickness; i++) {
            g.drawOval(sX+i, sY+i, width-i*2, height-i*2);
        }

        g.dispose();
        canvas.setCanvas(background);
    }
    public void fillRect(int x1, int x2, int y1, int y2, Color color) {
        getInfo();
        Graphics g = background.getGraphics();
        g.setColor(color);
        g.fillRect(x1, y1, x2 - x1, y2 - y1);
        g.dispose();
        canvas.setCanvas(background);
    }
    public void drawRect(int x1, int x2, int y1, int y2, Color color) {
        getInfo();
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);
        //determine start point based on higher x/y value
        int sX = (x1 < x2) ? x1 : x2;
        int sY = (y1 < y2) ? y1 : y2;

        Graphics g = background.getGraphics();
        g.setColor(color);
        for(int i = 0; i < thickness; i++) {
            g.drawRect(sX+i, sY+i, width-i*2, height-i*2);
        }
        g.dispose();
        canvas.setCanvas(background);
    }
    public void drawText(int x, int y, String text) {
        getInfo();
        Graphics g = background.getGraphics();
        g.setColor(Color.black);
        g.drawString(text, x, y);
        g.dispose();
        canvas.setCanvas(background);
    }

    private void getInfo() {
        background = canvas.getCanvas();
        thickness = canvas.getThickness();
    }
}
