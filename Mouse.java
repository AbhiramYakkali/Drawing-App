import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {
    private Canvas canvas;

    public Mouse(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        canvas.mouseClicked(e.getX(), e.getY(), e.getButton());
    }
    @Override
    public void mousePressed(MouseEvent e) {
        canvas.mousePressed(e.getX(), e.getY(), e.getButton());
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.mouseReleased(e.getX(), e.getY());
    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    //TODO: This is super slow for some reason
    public void mouseDragged(MouseEvent e) {
        canvas.mouseDragged(e.getX(), e.getY());
    }
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        canvas.wheelRotation(e.getWheelRotation());
    }

    public Point getMouseLocation() {
        return new Point((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
    }
}
