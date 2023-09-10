import javax.swing.*;
import java.awt.*;

public class Globals {
    public static final String NULL_STR = "";
    public static final boolean DEBUG_ON = true;

    // BEGIN OF CONSTANTS FOR CULMINATING ACTIVITY: ICS4U 2013 UNIVERSITY/AP COURSE AT BLOOR CI
    
    // NETWORK
    public static final int NET_OK            = 0;
    public static final int NET_SEND_ERROR    = -1;
    public static final int NET_RECEIVE_ERROR = -2;
    public static final int NET_REQUEST_ERROR = -3;
    public static final int NET_RECEIVE_REQUEST_ERROR = -4;
    public static final int NET_TIME_OUT_ERROR = -5;

    public static final int SENDING_ATTEMPTS_LIMIT = 5;
    public static final int PORT_NUMBER = 6666;
    public static final int TIME_OUT = 10000; // milliseconds
    public static final int QUEUE_SIZE = 100;
    
    // TIC TAC TOE
    public static final int GAME_TIE      = 0;
    public static final int GAME_STILL_ON = -1;
    public static final int NO_PLAYER  = 0;
    public static final int PLAYER_ONE = 1;
    public static final int PLAYER_TWO = 2;
    public static final int PAIR_OF_PLAYERS = 2;
    public static final int NO_WINNER   = 0;
    public static final int OK_CANCEL   = 1; 
    
    // Graphics constants
    public static final int ROWS = 3;
    public static final int COLS = 3;

    public static final int ROW_HEIGHT = 175;
    public static final int COL_WIDTH  = 175;

    public static final int FRAME_X = 250;
    public static final int FRAME_Y = 100;

    // areas of the playing board
    public static JFrame mainWindow   = new JFrame();
    public static GridPanel[][] grid  = new GridPanel[Globals.ROWS][Globals.COLS];
    public static GridBagConstraints gridBagConstraints = new GridBagConstraints();
    public static JLabel status       = new JLabel(" Status: ");

    public static String serverIPAddress   = new String(NULL_STR);   // Global so it can be accessed from class GridPanel
    public static String clientIPAddress   = new String(NULL_STR);

    // Servers
    public static final String SERVER1_NAME = "iMac";
    public static final String SERVER2_NAME = "MacBook Pro";
    public static final String SERVER3_NAME = "MacBook";
    public static final String SERVER4_NAME = "Nothing"; 

    public static final String SERVER1_ADDRESS = "192.168.1.106";
    public static final String SERVER2_ADDRESS = "192.168.1.110";
    public static final String SERVER3_ADDRESS = "192.168.1.132";
    public static final String SERVER4_ADDRESS = "10.200.1.171"; 

 // END OF CONSTANTS FOR CULMINATING ACTIVITY: ICS4U 2013 UNIVERSITY/AP COURSE AT BLOOR CI


    public static final int FILE_TRANSFER_TIME_OUT = 9000; // receive file timeout 
    public static final int EXIT_DELAY  = 5000;
    
    // game related variables
    public static boolean gameOver   = false;
    public static int currentPlayer  = NO_PLAYER;          
    public static int iAmPlayer      = NO_PLAYER;
    
    // for testing the students: 20 + 3 (me and IBM and TOSHIBA at home)
    public static final int TOTAL_STUDENTS = 2;

    // for testing students code
    public static String[][] studentsIdAndIPList = new String[TOTAL_STUDENTS][2];
    public static String[] realStudents = new String[TOTAL_STUDENTS]; 
    
    public static final int PICTURE_WIDTH = COL_WIDTH;
    public static final int PICTURE_HEIGHT = ROW_HEIGHT;
    
    public static final int TOTAL_GAMES = 14; // make this always EVEN so it fits in the server window
    public static final int NO_MORE_SLOTS = -1;
    
    public static final String BLANK_STR = new String(" ");
    public static final int ASCII_ZERO   = 48;
    public static final char NEW_LINE = 10;
    public static final String COMMA_STR = new String(",");
    public static final String IP_SEPARATOR = "" + (char) 234; //"" + (char) 234;
    
    
    // constants for game commands: 1st byte of message
    public static final char REQUEST_UNKNOWN         = 255;
    public static final char REQUEST_TO_PLAY_GAME    = 245; // client to server
    public static final char REQUEST_TO_PROCESS_PLAY = 244; // client to server
    public static final char COMMAND_GAME_TERMINATE  = 243; // server to client
    public static final char COMMAND_TO_WAIT         = 242; // server to client
    public static final char COMMAND_TO_START_GAME   = 241; // server to client
    public static final char COMMAND_YOUR_TURN       = 240; // server to client
    public static final char COMMAND_GAME_OVER       = 239; // server to client
    public static final char COMMAND_DISPLAY_MESSAGE = 238; // server to client
    public static final char REQUEST_TO_DISCONNECT   = COMMAND_GAME_TERMINATE; // client to server
    public static final char REQUEST_NET_OK          = 0; // client to server
    
    // request structure as a string
    // null string + command + row  + column + identification
    //               char    + char + char   + 15 chars
       
    // constants for the transmission of message
    public static final int COMMAND_LENGTH = 1;
    public static final int ROW_COL_LENGTH = 2;
    public static final int CLIENT_ID_LENGTH = 15;
    public static final int MAX_IP_ADDRESS_LENGTH = 15;
    public static final int MINIMUM_MESSAGE_LENGTH = COMMAND_LENGTH + ROW_COL_LENGTH + CLIENT_ID_LENGTH;
    public static final String NO_MESSAGE = "No message";
    public static final String NONE = "(none)";
    
    public static final int LENGTH_REQUEST_TO_PROCESS = 3; // P<row><col>
    
    // Constants for populating an unknown request
    public static final String DEFAULT_ROW_COL     = "00";
    public static final String UNKNOWN_PLAYER_NAME = "$$$$$$999999999";
    public static final String UNKNOWN_ADDRESS     = "000.000.000.000";
    
    public static boolean SUCCESS = true;
    public static boolean FAILURE = false;
    
    public static final int FILE_OPEN_ERROR   = -51;
    public static final int FILE_CREATE_ERROR = -52;
    
    public static final int USER_CANCEL       = -101;

    public static final int OFFSET = 15;
}
