import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class Canvas extends JComponent {
    public static Window window;
    private final Mouse mouse;
    private final Graphic graphic;

    private static final int WIDTH = 1920, HEIGHT = 1080;

    private BufferedImage background, buffer;

    /*
    0: standard brush
    1: line
    2: shape
     */
    private int mode = 0;
    /*
    0: circle/oval
    1: square/rectangle
    2: custom(doesn't work yet)
     */
    private int shape = 0;
    private boolean shift = false;
    private boolean mousePressed = false;
    //The direction that the line is being drawn(when shift is held)
    //0 = horizontal line, 1 = vertical line
    private int lineDirection = -1;
    private int thickness = 1;
    //Used for custom shape; keep track of each vertex(edges of shape created from this information)
    ArrayList<int[]> vertices;

    private Color color = Color.black;
    private int startX = -1, startY;

    public Canvas() {
        window = new Window(WIDTH, HEIGHT, "Drawing App", this);
        mouse = new Mouse(this);
        graphic = new Graphic(this);

        assignBindings();
        this.addMouseListener(mouse);
        this.addMouseWheelListener(mouse);
        this.addMouseMotionListener(mouse);

        background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        graphic.fillRect(0, WIDTH, 0, HEIGHT, Color.white);
        graphic.drawText(10, 10, "Thickness: " + thickness);
        buffer = copyImage(background);
    }

    public void mouseDragged(int x, int y) {
        if(mode == 0) {
            if(!shift) {
                graphic.drawDot(x, y, color);
            } else {
                if(lineDirection == -1 && startX != -1) {
                    int diffX = Math.abs(startX - x);
                    int diffY = Math.abs(startY - y);

                    lineDirection = (diffX > diffY) ? 0 : 1;
                } else {
                    if(lineDirection == 0) graphic.drawDot(x, startY, color);
                    else if(lineDirection == 1) graphic.drawDot(startX, y, color);
                }
            }
        } else if(mode == 1) {
            background = copyImage(buffer);
            graphic.drawLine(startX, x, startY, y, color);
        } else if(mode == 2) {
            background = copyImage(buffer);
            drawShape(shape, startX, startY, x, y);
        }
    }
    public void mousePressed(int x, int y, int button) {
        if(button == 1) {
            mousePressed = true;
            if(startX == -1 && (shift || mode != 0)) {
                startX = x;
                startY = y;
            }
            if(mode == 2 && shape == 2) {
                vertices.add(new int[]{x, y});
            }
        }
    }
    public void mouseReleased(int x, int y) {
        mousePressed = false;
        if(startX != -1) {
            startX = -1;
            shift = false;
            lineDirection = -1;
        }
        buffer = copyImage(background);
    }
    public void mouseClicked(int x, int y, int button) {
        //Check if user right clicks
        if(button == 3) {
            if(mode == 2 && shape == 2) {
                //Custom shape; right click used to determine vertices of the shape
                startX = x;
                startY = y;
                vertices.add(new int[]{x, y});

                //Draw the completed edge
                int[] start = vertices.get(vertices.size() - 2);
                int[] end = vertices.get(vertices.size() - 1);
                graphic.drawLine(start[0], end[0], start[1], end[1], color);
                buffer = copyImage(background);
            }
        }
    }

    public void wheelRotation(int clicks) {
        thickness -= clicks;
        if(thickness < 1) thickness = 1;
        graphic.fillRect(0, 100, 0, 100, Color.white);
        graphic.drawText(10, 10, "Thickness: " + thickness);
    }


    private void drawShape(int shape, int startX, int startY, int mouseX, int mouseY) {
        if(shape == 0 || shape == 1) {
            //circle/oval
            int endX = mouseX, endY = mouseY;
            if(shift) {
                int diffX = Math.abs(mouseX-startX), diffY = Math.abs(mouseY-startY);
                if(diffX > diffY) {
                    endY = (endY > startY) ? startY + diffX : startY - diffX;
                } else {
                    endX = (endX > startX) ? startX + diffY : startX - diffY;
                }
            }
            if(shape == 0) graphic.drawOval(startX, endX, startY, endY, color);
            else if(shape == 1) graphic.drawRect(startX, endX, startY, endY, color);
        } else if(shape == 2) {
            graphic.drawLine(startX, mouseX, startY, mouseY, color);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this);
        g.dispose();
    }

    public static BufferedImage copyImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void main(String[] args) {
        new Canvas();
    }

    private void assignBindings() {
        createShortcut("ESCAPE", "exit", "ESC");
        createShortcut("L", "line", "L");
        createShortcut("B", "brush", "B");
        createShortcut("C", "circle", "C");
        createShortcut("R", "rectangle", "R");
        createShortcut("U", "customShape", "U");

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,
                InputEvent.SHIFT_DOWN_MASK), "shiftmod");

        this.getActionMap().put("shiftmod", new ShortcutAction("SHIFT"));
    }
    //Helper function to create input/action maps
    private void createShortcut(String key, String title, String keyName) {
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), title);
        this.getActionMap().put(title, new ShortcutAction(keyName));
    }

    public BufferedImage getCanvas() {
        return copyImage(background);
    }
    public void setCanvas(BufferedImage canvas) {
        background = copyImage(canvas);
        repaint();
    }
    public int getThickness() {
        return thickness;
    }

    private class ShortcutAction extends AbstractAction {
        private final String key;

        public ShortcutAction(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch(key) {
                case "ESC":
                    System.exit(0);
                    break;
                case "B":
                    mode = 0;
                    break;
                case "L":
                    mode = 1;
                    break;
                case "C":
                    mode = 2;
                    shape = 0;
                    break;
                case "R":
                    mode = 2;
                    shape = 1;
                    break;
                case "U":
                    mode = 2;
                    shape = 2;
                    vertices = new ArrayList<>();
                    break;
                case "SHIFT":
                    if(startX == -1) {
                        shift = true;
                        Point mLoc = mouse.getMouseLocation();
                        startX = (int) mLoc.getX();
                        startY = (int) mLoc.getY();
                    }
                    break;
            }
        }
    }
}