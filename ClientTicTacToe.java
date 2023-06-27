import javax.swing.*;

import java.awt.*;
import java.net.Socket;

public class ClientTicTacToe extends JFrame {
	public static Socket socket;
    public static void main(String[] args) {
	Globals.serverIPAddress = Utils.initialNetworkConnection();
	if (Globals.serverIPAddress != null) {
	    createWindow();
	} else {
	    System.out.println("No server selected");
	}
    }

public static void createWindow() {
    JFrame mainWindow = new JFrame();
    mainWindow.setTitle("title");
    mainWindow.setResizable(false);
    mainWindow.setLocation(Globals.FRAME_X, Globals.FRAME_Y);
    mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    mainWindow.addWindowListener(new WindowEventHandler());

    JPanel tttPanel = new JPanel();
    tttPanel.setLayout(new GridLayout(Globals.ROWS, Globals.COLS));
    for (int i = 0; i < Globals.ROWS; i++) {
        for (int j = 0; j < Globals.COLS; j++) {
            Globals.grid[i][j] = new GridPanel(new Color(60, 30, 170), i, j);
            tttPanel.add(Globals.grid[i][j]);
        }
    }

    JPanel contentPanel = new JPanel();
	contentPanel.setLayout(new BorderLayout());
    contentPanel.add(tttPanel, BorderLayout.CENTER);

    Globals.status.setFont(new Font("Iosevka", Font.BOLD, 18));
    contentPanel.add(Globals.status, BorderLayout.SOUTH);

    mainWindow.setContentPane(contentPanel);
    mainWindow.pack();
    mainWindow.setVisible(true);

    do {
        gameLoop();
    } while (!Globals.gameOver);
}
    
    public static void gameLoop() {
	System.out.println("Waiting");
	String request = NetIO.receiveRequest(socket);
	System.out.println(request);
	NodeInfo commandFromServer = new NodeInfo(request);
	System.out.println(commandFromServer);
	switch (commandFromServer.getCommand()) {
	    case Globals.COMMAND_TO_WAIT:
		Utils.updateStatusLine(commandFromServer.getMessage());
		break;
	    case Globals.COMMAND_TO_START_GAME:
		Globals.iAmPlayer = Integer.parseInt(commandFromServer.getRowColPlayer().substring(0, 1));
		Globals.currentPlayer = Globals.PLAYER_ONE;
		Utils.updateStatusLine(commandFromServer.getMessage());
		break;
	    case Globals.COMMAND_YOUR_TURN:
		int row = Integer.parseInt(commandFromServer.getRowColPlayer().substring(0, 1));
		int col = Integer.parseInt(commandFromServer.getRowColPlayer().substring(1, 2));
		Globals.grid[row][col].setVal(Globals.currentPlayer);
		Globals.grid[row][col].drawXorO();
		Globals.currentPlayer = 3 - Globals.currentPlayer;
		Utils.updateStatusLine(commandFromServer.getMessage());
		break;
	    case Globals.COMMAND_GAME_OVER:
		Globals.gameOver = true;
		Utils.updateStatusLine(commandFromServer.getMessage());
		break;
	    case Globals.COMMAND_GAME_TERMINATE:
		Globals.gameOver = true;
		Utils.updateStatusLine(commandFromServer.getMessage());
		break;
	    case Globals.COMMAND_DISPLAY_MESSAGE:
		Utils.updateStatusLine(commandFromServer.getMessage());
		break;
	    default:
		break;
	}
       
    }
}
