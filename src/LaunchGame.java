import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import javax.swing.*;
import java.net.*;
import java.util.*;

/**
 * Creates the JFrame for the "main menu".
 * The main menu is the first user-facing prompt, and is responsible
 * for most of the client's networking.
 * @author Gio Lopez/Carlo Mendoza
 * @version 2016-05-23 (v8)
 */
public class LaunchGame extends JComponent {
    private Image bkgd, logo;
    private Picker picker;
    private boolean isSinglePlayer, connected;
    private Font copyright, menuFont;
    private JFrame frame;
    private int exitCountdown, myID, winnerID;
    private String enemyName, ipAddress, msg, myName;
    private int port = 8888;
    private ArrayList<String> tileNames1;
    private ArrayList<String> tileNames2;
    private Board b;
    private TalkToServerThread ttst;
    private ArrowKeys ak;

    // music
    String menuMusic = "../assets/mus/mus_temshop.ogg";
    String menuMusicPath = System.getProperty("user.dir") + "/" + menuMusic;
    MusicPlayer mp = new MusicPlayer();

    /**
     * Constructor for objects of class LaunchGame.
     * Also sets look and feel and derives the fonts.
     * @param  f JFrame to close later
     */
    public LaunchGame(JFrame f) {
        // Sets look and feel to system default. Only tested on Windows 8/10 and OS X 10.10.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        tileNames1 = new ArrayList<String>();
        tileNames2 = new ArrayList<String>();

        // frame creation
        frame = f;
        exitCountdown = 0;
        msg = "IF YOU CHOOSE TO PLAY IN 2 PLAYER VERSUS, ENTER YOUR NAME AFTER IP"; // initial message
        bkgd = Toolkit.getDefaultToolkit().getImage("../assets/img/menu.png");
        logo = Toolkit.getDefaultToolkit().getImage("../assets/img/logo.png");
        picker = new Picker();
        ak = new ArrowKeys();
        frame.getContentPane().addKeyListener(ak); // add the key listener
        frame.getContentPane().setFocusable(true);
        System.out.println("===== PLAYER CONSOLE INITIATED =====");
        isSinglePlayer = true;
        winnerID = 0;

        // Font derivation
        try {
            copyright = Font.createFont(Font.TRUETYPE_FONT, new File("../assets/font/cot.ttf")).deriveFont(Font.PLAIN, 24);
            menuFont = Font.createFont(Font.TRUETYPE_FONT, new File ("../assets/font/dtmmono.ttf")).deriveFont(Font.PLAIN, 40);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // plays the background music in a loop
        try {
            mp.audioStartUnpreloaded(new URL("file:///" + menuMusicPath), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles drawing the main menu.
     * @param g Graphics object to draw on; note that this is casted immediately to a Graphics2D object.
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.drawImage(bkgd, 0, 0, this);
        g2d.setPaint(Color.WHITE);
        g2d.drawImage(logo, 55, 90, this);
        g2d.setFont(menuFont);
        g2d.drawString("GAME MODE", 250, 260);
        if (isSinglePlayer) {
            g2d.setPaint(Color.YELLOW);
            g2d.drawString("Single Player", 250, 315);
            g2d.setPaint(Color.WHITE);
            g2d.drawString("2 Player VS", 250, 360);
        } else {
            g2d.setPaint(Color.WHITE);
            g2d.drawString("Single Player", 250, 315);
            g2d.setPaint(Color.YELLOW);
            g2d.drawString("2 Player VS", 250, 360);
        }
        g2d.setFont(copyright);
        g2d.setPaint(Color.WHITE);
        g2d.drawString("[c] 2016 Mendoza | Lopez", 230, 470);
        g2d.drawString("ART ASSETS based on UNDERTALE [c] 2015 Toby Fox", 60, 490);
        picker.draw(g2d);

        Font status = copyright.deriveFont(Font.PLAIN, 16);
        g2d.setFont(status);
        g2d.drawString(msg, 10, 20);
    }

    /**
     * Creates tile Arrays, a Board object, and makes the frame (for menu) visible.
     * @param singPlay if single player or not
     */
    public void startGame(boolean singPlay) {
        if (singPlay) {
            String[] imgNames1 = {"alphys", "dog", "flowey", "mettaton", "muffet", "napstablook", "papyrus", "sans", "toriel", "undyne"};
            String[] imgNames2 = {"alphys", "dog", "flowey", "mettaton", "muffet", "napstablook", "papyrus", "sans", "toriel", "undyne"};
            Collections.shuffle(Arrays.asList(imgNames1));
            Collections.shuffle(Arrays.asList(imgNames2));
            enemyName = "--";
            b = new Board (imgNames1, imgNames2, myName, enemyName, ttst, singPlay);
            frame.setVisible(false);
        } else {
            String [] newTileNames1 = tileNames1.toArray(new String[tileNames1.size()]);
            String [] newTileNames2 = tileNames2.toArray(new String[tileNames2.size()]);
            b = new Board (newTileNames1, newTileNames2, myName, enemyName, ttst, singPlay);
            frame.setVisible(false);
        }
    }

    /**
     * Inner class for the KeyListener used.
     */
    class ArrowKeys implements KeyListener {
        /**
         * Ran when arrow keys are pressed.
         * @param e KeyEvent
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (isSinglePlayer) {
                    isSinglePlayer = false;
                    picker.moveDown();
                    repaint();
                } else {
                    isSinglePlayer = true;
                    picker.moveUp();
                    repaint();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (!isSinglePlayer) {
                    isSinglePlayer = true;
                    picker.moveUp();
                    repaint();
                } else {
                    isSinglePlayer = false;
                    picker.moveDown();
                    repaint();
                }
            } else if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (picker.getY() == 285)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        System.out.println("You have chosen to play in Single Player. \nGame will now start...");
                        String msgDialog = "What is your name?"; // prompts user name
                        do {
                            myName = JOptionPane.showInputDialog(msgDialog);
                            if (myName.equals("")) {
                                msgDialog = "Please enter a valid name.";
                                myName = JOptionPane.showInputDialog(msgDialog);
                            }
                        } while (myName.equals(""));
                        startGame(true);
                        mp.stopMusic();
                    }
                });
            } else if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (picker.getY() == 330)) {
                System.out.println("You have chosen to play in 2 Player VS.");
                String msgDialog = "Please enter the server IP. Enter localhost if on the same PC.";
                do {
                    ipAddress = JOptionPane.showInputDialog(msgDialog);
                    if (ipAddress.equals("")) {
                        msgDialog = "Please enter a valid server IP.";
                        ipAddress = JOptionPane.showInputDialog(msgDialog);
                    }
                }   while (ipAddress.equals(""));
                LaunchGame.this.connectToServer();
                frame.getContentPane().removeKeyListener(ak);
            }
            else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            	exitCountdown++;
            	if (exitCountdown == 2) {
            		msg = "Closing game...";
            		LaunchGame.this.repaint();
            	}
            	if (exitCountdown == 10) System.exit(0);
        }
    }
        public void keyReleased(KeyEvent e) {
        	exitCountdown = 0;
        	msg = "IF YOU CHOOSE TO PLAY IN 2 PLAYER VERSUS, ENTER YOUR NAME AFTER IP";
        	LaunchGame.this.repaint();
        }
        public void keyTyped(KeyEvent e) {}
    }

    /**
     * Initiates connection to server.
     * Also creates a new thread for server communication as not to block the GUI.
     */
    public void connectToServer() {
        Socket s;
        try {
            s = new Socket(ipAddress, port);
            ttst = new TalkToServerThread(s);
            Thread t = new Thread(ttst);
            t.start();
            connected = true;
        } catch (IOException ex) {
            System.out.println("Error in connectToServer() method.");
            connected = false;
            frame.getContentPane().addKeyListener(ak);
        }
    }

    /**
     * Thread class for communicating with server.
     */
    class TalkToServerThread implements Runnable {
        private Socket theSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private boolean winState;

        /**
         * Constructor for TalkToServerThread.
         * @param  s Socket to connect to
         */
        public TalkToServerThread(Socket s) {
            theSocket = s;
            try {
                dataIn = new DataInputStream(theSocket.getInputStream());
                dataOut = new DataOutputStream(theSocket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("Error in TalkToServerThread constructor.");
            }
        }

        /**
         * Method ran by thread when thread is started.
         */
        @Override
        public void run() {
            readWriteToServer();
        }

        /**
         * Client I/O between itself and the server.
         */
        public void readWriteToServer() {
            String msgDialog = "What is your name?"; // prompts user name
            do {
                myName = JOptionPane.showInputDialog(msgDialog);
                if (myName.equals("")) {
                    msgDialog = "Please enter a valid name.";
                    myName = JOptionPane.showInputDialog(msgDialog);
                }
            } while (myName.equals(""));
            try {
                myID = dataIn.readInt();
                dataOut.writeUTF(myName);
                dataOut.flush();
                System.out.println("Hello, " + myName + "! You are connected to the game server as player #" + myID + ".");
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        LaunchGame.this.frame.setTitle(myName + "! You are Player " + myID);
                        if (myID == 1) {
                            msg = "WAITING FOR PLAYER 2";
                            LaunchGame.this.repaint();
                        }
                    }
                });
                enemyName = dataIn.readUTF();
                    try {
                        for (int i = 0; i < 10; i++) tileNames1.add(dataIn.readUTF());
                        for (int i = 0; i < 10; i++) tileNames2.add(dataIn.readUTF());
                        System.out.println("Tiles received");
                        LaunchGame.this.startGame(false);
                        mp.stopMusic();
                    } catch (IOException ex) {}

                while(true) {
                    dataOut.writeBoolean(winState);
                    dataOut.flush();
                    winnerID = dataIn.readInt();
                    if(winnerID != 0) {
                        if (winnerID == myID) {
                            break;
                        } else {
                            b.youLose();
                            break;
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error in readWriteToServer() method.");
            }
        }
        public void setWinState(boolean hasWinner) {
            winState = hasWinner;
        }
}

    /**
     * Entrypoint for running the client LaunchGame.
     * Runs GUI on EDT.
     * @param args[] --
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame menuWindow = new JFrame();
                menuWindow.setTitle("MEMORY GAME by Team Hajime");
                menuWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                menuWindow.setResizable(false);
                menuWindow.getContentPane().setPreferredSize(new Dimension(800, 500));
                LaunchGame menuOptions = new LaunchGame(menuWindow);
                menuWindow.add(menuOptions);
                menuWindow.pack();
                menuWindow.revalidate();
                menuWindow.setLocationRelativeTo(null);
                menuWindow.setVisible(true);
            }
        });

    }
}
