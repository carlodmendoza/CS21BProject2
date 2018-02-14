import javax.swing.*;

/**
 * Class for Tile objects
 * @author Gio Lopez/Carlo Mendoza
 * @version 2016-05-15 (v7.7)
 */
public class Tile extends JButton {
    private ImageIcon tileNum;
    private boolean isMatched = false;

    /**
     * Sets the ID of the tile specified
     * @param tileNum tile number
     */
    public void setId(ImageIcon tileNum) {
        this.tileNum = tileNum;
    }

    /**
     * Returns the Tile ID number.
     * @return Tile ID number
     */
    public ImageIcon getId() {
        return this.tileNum;
    }

    /**
     * Sets the tile match state.
     * @param isMatched boolean for if the tile is matched
     */
    public void setMatched(boolean isMatched) {
        this.isMatched = isMatched;
    }

    /**
     * Returns if the tile matches
     * @return boolean if tile is matched
     */
    public boolean getMatched() {
        return this.isMatched;
    }
}