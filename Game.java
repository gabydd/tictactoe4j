import java.net.Socket;

public class Game {
	private String[] ipAddresses = new String[Globals.PAIR_OF_PLAYERS];
	private String[] imageFileNames = new String[Globals.PAIR_OF_PLAYERS];
	private String[] userNames = new String[Globals.PAIR_OF_PLAYERS];

	private int currentPlayer = Globals.NO_PLAYER;
	private int[][] matrix = new int[Globals.ROWS][Globals.COLS];
	private Player[] players = new Player[Globals.PAIR_OF_PLAYERS];
	public Game next;
	public Game pre;

	public Game(int currentGame) {
		ipAddresses[0] = Globals.NULL_STR;
		ipAddresses[1] = Globals.NULL_STR;

		imageFileNames[0] = "";
		imageFileNames[1] = "";

		userNames[0] = "";
		userNames[1] = "";

		currentPlayer = Globals.NO_PLAYER;
		for (int row = 0; row < Globals.ROWS; row++)
			for (int col = 0; col < Globals.COLS; col++)
				matrix[row][col] = Globals.NO_PLAYER;
		for (int i = 0; i < players.length; i++) {
			players[i] = new Player();
			players[i].setWhichPlayer(i);
			players[i].setCurrentGame(currentGame);
			players[i].setGame(this);
		}
	}

	public void start() {
		for (int i = 0; i < players.length; i++) {
			players[i].start();
		}
	}

	public String getIPAddress(int whatPlayer) {
		return ipAddresses[whatPlayer];
	}

	public void setIPAddress(int whatPlayer, String ipAddress) {
		ipAddresses[whatPlayer] = ipAddress;
	}

	public Socket getSocket(int whatSocket) {
		return players[whatSocket].getSocket();
	}

	public void setSocket(int whatSocket, Socket socket) {
		players[whatSocket].setSocket(socket);
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(int cp) {
		currentPlayer = cp;
	}

	public String getImageFileName(int whatPlayer) {
		return imageFileNames[whatPlayer];
	}

	public void setImageFileName(int whatPlayer, String fn) {
		imageFileNames[whatPlayer] = fn;
	}

	public String getUserName(int whatPlayer) {
		return userNames[whatPlayer];
	}

	public void setUserName(int whatPlayer, String un) {
		userNames[whatPlayer] = un;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int getMatrixVal(int row, int col) {
		return matrix[row][col];
	}

	public void setMatrixVal(int row, int col, int val) {
		matrix[row][col] = val;
	}
}
