import java.awt.*;
import javax.swing.*;

/**
 * Class definition for a "picker," which is used as the
 * selection cursor in the main menu for this project
 *
 * @author Gio Lopez/Carlo Mendoza
 * @version 2016-05-15 (v7.7)
 */
public class Picker extends JComponent {
    private int x, y;
    private Image picker;

    /**
     * Constructor for objects of class Picker
     */
    public Picker() {
        x = 200;
        y = 285;
        picker = Toolkit.getDefaultToolkit().getImage("../assets/img/picker.png");
    }

    /**
     * Draws the picker on a Graphics2D object.
     * @param  g2d the Graphics2D object to draw on
     */
    protected void draw(Graphics2D g2d) {
        g2d.drawImage(picker, x, y, this);
    }

    /**
     * Moves the object upward to a fixed position
     */
    public void moveUp() {y = 285;}

    /**
     * Moves the object downward to a fixed position
     */
    public void moveDown() {y = 330;}

    /**
     * Returns the y value.
     * @return y-coordinate
     */
    public int getY() {return y;}
}