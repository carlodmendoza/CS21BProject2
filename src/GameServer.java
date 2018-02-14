import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Provides server for two clients to interact with.
 * @author Gio Lopez/Carlo Mendoza; based on the work of Alberto H. Medalla
 * @version 2016-05-23 (v8)
 */
public class GameServer {
    private ServerSocket theServer;
    private static final int MAX_PLAYERS = 2;
    private int numPlayers, winPlayer;
    
    // Properties to pass
    private String p1Name, p2Name;
    private String[] imgNames1 = {"alphys", "dog", "flowey", "mettaton", "muffet", "napstablook", "papyrus", "sans", "toriel", "undyne"};
    private String[] imgNames2 = {"alphys", "dog", "flowey", "mettaton", "muffet", "napstablook", "papyrus", "sans", "toriel", "undyne"};
    
    // fields for each thread
    private TalkToClientThread p1Thread;
    private TalkToClientThread p2Thread;
    private boolean hasWinner, winnerSet;

    /**
     * Constructor for objects of class GameServer.
     * Additionally, now handles tile shuffling, which will be sent to the players.
     */
    public GameServer() {
        // shuffles the arrays
        Collections.shuffle(Arrays.asList(imgNames1));
        Collections.shuffle(Arrays.asList(imgNames2));
        hasWinner = false;
        winnerSet = false;

        // start networking
        numPlayers = 0;
        winPlayer = 0;
        try {
            theServer = new ServerSocket(8888);
            System.out.println("=========== MEMORY GAME BY TEAM HAJIME ===========");
            System.out.println("===== THE SERVER'S NOW ACCEPTING CONNECTIONS =====");
        } catch (IOException ex) {
            System.out.println("Error in GameServer constructor.");
        }
    }

    /**
     * Allows server to accept client connections.
     * Connects first client to connect as P1; second as P2,
     * then disallows other connections.
     */
    public void acceptConnections() {
        try {
            while (numPlayers < MAX_PLAYERS) {
                Socket s = theServer.accept();
                numPlayers++; // increment numPlayers after each connection
                System.out.println("[PLAYER " + numPlayers + "] has connected.");
                
                // used to identify which thread is which.
                if(numPlayers == 1) {
                    p1Thread = new TalkToClientThread(s, numPlayers);
                } else {
                    p2Thread = new TalkToClientThread(s, numPlayers);
                    Thread t1 = new Thread(p1Thread);
                    Thread t2 = new Thread(p2Thread);
                    t1.start();
                    t2.start();
                }
            }
        } catch (IOException ex) {
            System.out.println("Error in acceptConnections() method.");  
        }
    }

    /**
     * Thread class for communicating with client.
     * @implements Runnable
     */
    class TalkToClientThread implements Runnable {
        
        // Each thread will have a player ID to differentiate between the 2 threads.
        private int playerID;
        private Socket theSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private boolean win, connected;

        /**
         * Constructor for TalkToClientThread; reads player name and player ID
         * @param  s   Socket to connect to
         * @param  pid Player ID of client
         */
        public TalkToClientThread(Socket s, int pid) {
            playerID = pid;
            theSocket = s;
            try {  
                dataIn = new DataInputStream(theSocket.getInputStream());
                dataOut = new DataOutputStream(theSocket.getOutputStream());
                if (playerID == 1) {
                    dataOut.writeInt(playerID);
                    dataOut.flush();
                    p1Name = dataIn.readUTF();
                    System.out.println("[PLAYER " + playerID + "] name is " + p1Name);
                } else {
                    dataOut.writeInt(playerID);
                    dataOut.flush();
                    p2Name = dataIn.readUTF();
                    System.out.println("[PLAYER " + playerID + "] name is " + p2Name);
                }
                connected = true;  
            } catch (IOException ex) {    
                System.out.println("Error in TalkToClientThread constructor.");   
            }
        }

        /**
         * Method ran by thread when thread is started.
         */
        @Override
        public void run() {
            readWriteToClient();
        }

        /**
         * Server I/O between itself and two clients.
         */
        public void readWriteToClient() {
            try {
                if(playerID == 1) {
                    dataOut.writeUTF(p2Name);
                    dataOut.flush();
                } else {
                    dataOut.writeUTF(p1Name);
                    dataOut.flush();
                }
                for (int i = 0; i<= imgNames1.length-1; i++) {
                    dataOut.writeUTF(imgNames1[i]);
                    dataOut.flush();
                    System.out.println("Sent tiles part 1");
                }
                for (int i = 0; i<= imgNames2.length-1; i++) {
                    dataOut.writeUTF(imgNames2[i]);
                    dataOut.flush();
                    System.out.println("Sent tiles part 2");
                }
                while (connected) {
                    if (!hasWinner) {
                        hasWinner = dataIn.readBoolean();
                        dataOut.writeInt(0);
                        dataOut.flush();
                    } else {
                        if (!winnerSet) {
                            winPlayer = playerID;
                            winnerSet = true;
                        }
                        dataOut.writeInt(winPlayer);
                        dataOut.flush();
                        System.out.println("THE WINNER is " + winPlayer);
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error in TalkToClientThread");
            }
        }
    }
    
    /**
     * Entrypoint for running GameServer.
     * Immediately creates a new GameServer and starts accepting connections.
     * @param args[] --
     */
    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
