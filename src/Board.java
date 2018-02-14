import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

/**
 * Provides the game logic and the JFrame with the playing field.
 * @author Gio Lopez/Carlo Mendoza
 * @version 2016-05-23 (v8)
 */
public class Board extends JPanel {
    private JPanel mainPanel;
    private JFrame mainFrame;
    private JLabel leftBorder, nameLabel, rightBorder, timeLabel;
    private int nSeconds, matchedTiles;
    private java.util.List<Tile> tiles;
    private Tile selectedTile, t1, t2;
    private javax.swing.Timer timer;
    private java.util.Timer timerClock;
    private String[] imgNames1;
    private String[] imgNames2;
    private String enemyName, playerName;
    private LaunchGame.TalkToServerThread ttst;
    private boolean hasWinner, isSinglePlayer;

    // music related
    String bgMusic = "../assets/mus/mus_vsasgore.ogg";
    String bgmPath = System.getProperty("user.dir") + "/" + bgMusic;
    String winMusic = "../assets/mus/mus_menu6.ogg";
    String winPath = System.getProperty("user.dir") + "/" + winMusic;
    String loseMusic = "../assets/mus/mus_gameover.ogg";
    String losePath = System.getProperty("user.dir") + "/" + loseMusic;
    MusicPlayer mp = new MusicPlayer();

    /**
     * Constructor for objects of class Board.
     * This constructor is also responsible for setting the UI LnF,
     * starting the timer, creating the JFrame, and starting the looping music.
     * Note that the GameServer is responsible for sending the image arrays.
     * @param imgN1[] first array of images for tiles
     * @param imgN2[] second array of images for tiles
     * @param pN player's name
     * @param eN enemy's name
     * @param ttst LaunchGame (menu) object to associate with
     * @param b if the game is running in single player
     */
    public Board(String[] imgN1, String[] imgN2, String pN, String eN, LaunchGame.TalkToServerThread ttst, boolean b) {
        // Sets look and feel to system default. Only tested on Windows 8/10 and OS X 10.10.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        isSinglePlayer = b;
        hasWinner = false;
        imgNames1 = imgN1;
        imgNames2 = imgN2;
        playerName = pN;
        enemyName = eN;
        this.ttst = ttst;
        mainPanel = new JPanel();
        mainFrame = new JFrame();
        timeLabel = new JLabel(" ", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        timeLabel.setBackground(Color.BLACK);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setOpaque(true);
        nameLabel = new JLabel("Hello " + playerName + "!                    Your enemy's name is: " + enemyName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        nameLabel.setBackground(Color.BLACK);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setOpaque(true);
        leftBorder = new JLabel();
        leftBorder.setIcon(new ImageIcon("../assets/img/border.gif"));
        leftBorder.setBackground(Color.BLACK);
        leftBorder.setOpaque(true);
        rightBorder = new JLabel();
        rightBorder.setIcon(new ImageIcon("../assets/img/border.gif"));
        rightBorder.setBackground(Color.BLACK);
        rightBorder.setOpaque(true);
        nSeconds = 0;
        timerClock = new java.util.Timer();
        timerClock.schedule(new UpdateUITask(), 0, 1000);
        int pairs = 10; // number of pairs to match, default 10
        java.util.List<Tile> tilesList = new ArrayList<Tile>();
        java.util.List<ImageIcon> images = new ArrayList<ImageIcon>();

        // iterates through images and adds them to a List<ImageIcon>
        for (int i = 0; i < pairs; i++) {
            images.add(new ImageIcon("../assets/img/" + imgNames1[i] + ".png"));
            images.add(new ImageIcon("../assets/img/" + imgNames2[i] + ".png"));
        }

        // iterates through objects in List<ImageIcon> and
        // adds an ActionListener, as well as sets the unclicked icon
        for (ImageIcon img : images) {
            Tile t = new Tile();
            t.setId(img);
            t.setIcon(new ImageIcon("../assets/img/heart.png"));
            t.addActionListener(ae -> {selectedTile = t; doTurn();});
            tilesList.add(t);
        }
        this.tiles = tilesList;

        // starts the Timer for tile checking and updating
        timer = new javax.swing.Timer(750, ae -> checkTiles());
        timer.setRepeats(false);

        //sets up the board itself
        setLayout(new GridLayout(4, 5));
        for (Tile t : tiles) add(t);

        //sets up the main panel
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(timeLabel, "North");
        mainPanel.add(this, "Center");
        mainPanel.add(nameLabel, "South");
        mainPanel.add(leftBorder, "West");
        mainPanel.add(rightBorder, "East");

        //sets up the main frame
        mainFrame.setPreferredSize(new Dimension(580, 550));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.revalidate();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        // plays the background music in a loop
        try {
            mp.audioStartUnpreloaded(new URL("file:///" + bgmPath), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates UI for the in-game timer (stopwatch)
     */
    private class UpdateUITask extends TimerTask {
        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    timeLabel.setText("Time: " + String.valueOf(nSeconds++) + "                              Matched Tiles: " + matchedTiles);
                }
            });
        }
    }

    /**
     * Handles start-of-game turn
     */
    public void doTurn() {
        if (t1 == null && t2 == null) {
            t1 = selectedTile;
            t1.setIcon(t1.getId());
        }

        if (t1 != null && t1 != selectedTile && t2 == null) {
            t2 = selectedTile;
            t2.setIcon(t2.getId());
            timer.start();
        }
    }

    /**
     * Main game logic; checks if tiles match, if the game is won, etc.
     */
    public void checkTiles() {
        if (t1.getId().toString().equalsIgnoreCase(t2.getId().toString())) { //match condition
            t1.setEnabled(false); //disables the button
            t2.setEnabled(false);
            t1.setMatched(true); //flags the button as having been matched
            t2.setMatched(true);
            matchedTiles++;

            if (this.isGameWon()) {
                timerClock.cancel();
                if (!isSinglePlayer) ttst.setWinState(true);
                try {
                    mp.stopMusic();
                    mp.audioStartUnpreloaded(new URL("file:///" + winPath), -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!isSinglePlayer) {
                    JOptionPane.showMessageDialog(this, "You finished the puzzle first!", "You won!", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "You finished the puzzle!", "You won!", JOptionPane.PLAIN_MESSAGE);
                }
                System.exit(0);
            }
        } else {
            t1.setIcon(new ImageIcon("../assets/img/heart.png")); //'hides' text
            t2.setIcon(new ImageIcon("../assets/img/heart.png"));
        }
        t1 = null; //reset t1 and t2
        t2 = null;
    }

    /**
     * method ran when the player loses
     */
    public void youLose() {
        timerClock.cancel();
        try {
            mp.stopMusic();
            mp.audioStartUnpreloaded(new URL("file:///" + losePath), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Stay determined!", "You cannot give up just yet...", JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
    }

    /**
     * Returns if the game has been won or lost.
     * @return     boolean if game is won or lost
     */
    public boolean isGameWon() {
        for (Tile t : this.tiles) {
            if (t.getMatched() == false) {
                return false;
            }
        }
        return true;
    }
}