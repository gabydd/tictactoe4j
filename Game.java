import java.net.Socket;
import java.io.IOException;

public class Game extends Thread {
	private String[] ipAddresses = new String[Globals.PAIR_OF_PLAYERS];
	private String[] imageFileNames = new String[Globals.PAIR_OF_PLAYERS];
	private String[] userNames = new String[Globals.PAIR_OF_PLAYERS];

	private int currentPlayer = Globals.NO_PLAYER;
	private int[][] matrix = new int[Globals.ROWS][Globals.COLS];
	private MessageReader[] readers = new MessageReader[Globals.PAIR_OF_PLAYERS];
	private MessageQueue messageQueue = new MessageQueue();
	private int currentGame;
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
		this.currentGame = currentGame;
		for (int row = 0; row < Globals.ROWS; row++)
			for (int col = 0; col < Globals.COLS; col++)
				matrix[row][col] = Globals.NO_PLAYER;
		for (int i = 0; i < readers.length; i++) {
			readers[i] = new MessageReader();
			readers[i].setGame(this);
			readers[i].setMessageQueue(messageQueue);
		}
	}

	public void run() {
		for (int i = 0; i < readers.length; i++) {
			readers[i].start();
		}
		boolean gameOver = false;
		int errorCode;
		NodeInfo requestFromPlayer;
		while (!gameOver) {
			requestFromPlayer = messageQueue.read();
			System.out.println(requestFromPlayer.getCommand());
			ServerTicTacToe.requestFromPlayer = requestFromPlayer;
			switch (requestFromPlayer.getCommand()) {
				case Globals.REQUEST_TO_PROCESS_PLAY:
					int row = Integer.parseInt(requestFromPlayer.getRowColPlayer().substring(0, 1));
					int col = Integer.parseInt(requestFromPlayer.getRowColPlayer().substring(1, 2));
					setMatrixVal(row, col, getCurrentPlayer());

					// here change the variable right away. careful with the if below
					// the condition assumes that the change has happened first
					setCurrentPlayer(Utils.otherPlayer(getCurrentPlayer()));

					int currentPlayer = getCurrentPlayer();
					if (currentPlayer == Globals.PLAYER_ONE || currentPlayer == Globals.PLAYER_TWO) {
						NetIO.sendServerRequest(Globals.NULL_STR +
								Globals.COMMAND_YOUR_TURN +
								row + col +
								Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
								Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
								Utils.getStudentFirstName(getUserName(currentPlayer - 1)) + " it's your turn.",
								getSocket(currentPlayer - 1));
						errorCode = messageQueue.read().getCommand();
					} else
						ServerTicTacToe.info.println("Error in ServerTicTacToe. No player");

					int winner = Matrix.checkWinner(getMatrix());
					if (winner != Globals.GAME_STILL_ON) {
						String message = Globals.NULL_STR;
						if (winner != Globals.NO_WINNER) {
							message = Utils.getStudentFirstName(getUserName(winner - 1)) + " wins.";
						} else {
							message = "Game is a tie.";
						}

						NetIO.sendServerRequest(Globals.NULL_STR +
								Globals.COMMAND_GAME_OVER +
								"00" +
								Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
								Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
								message,
								getSocket(0));
						errorCode = messageQueue.read().getCommand();

						NetIO.sendServerRequest(Globals.NULL_STR +
								Globals.COMMAND_GAME_OVER +
								"00" +
								Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
								Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
								message,
								getSocket(1));
						errorCode = messageQueue.read().getCommand();
						ServerTicTacToe.dumpGameInResultsWindow(this, currentGame);
						gameOver = true;
						try {
							getSocket(0).close();
							getSocket(1).close();
						} catch (IOException e) {

						}
					}

					break;

				case Globals.REQUEST_TO_DISCONNECT:
					if (!getIPAddress(0).equals(Globals.NULL_STR) && // (C6)
							!getIPAddress(1).equals(Globals.NULL_STR)) {

						if (requestFromPlayer.getIPAddress().equals(getIPAddress(0))) {
							NetIO.sendServerRequest(Globals.NULL_STR +
									Globals.COMMAND_GAME_TERMINATE + "00" +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									"Game has been terminated by the other player.",
									getSocket(1));
						errorCode = messageQueue.read().getCommand();
						} else {
							NetIO.sendServerRequest(Globals.NULL_STR +
									Globals.COMMAND_GAME_TERMINATE + "00" +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									"Game has been terminated by the other player.",
									getSocket(0));
						errorCode = messageQueue.read().getCommand();
						}

						ServerTicTacToe.info.setCursor(ServerTicTacToe.infoRows - 2, 1);
						ServerTicTacToe.info
								.println("Both clients have been disconnected from game " + currentGame);
					} else
						ServerTicTacToe.info.println(
								Globals.DEBUG_ON ? "Both games null string: REQUEST_TO_DISCONNECT case not possible"
										: "");
					gameOver = true;
					try {
						sleep(10);
					} catch (InterruptedException e) {

					}
					try {
						getSocket(0).close();
						getSocket(1).close();
					} catch (IOException e) {

					}
					break;

			}
			ServerTicTacToe.dumpInfo(ServerTicTacToe.games);
		}
		GamesList games = ServerTicTacToe.games;
		ServerTicTacToe.games.remove(this);
		System.out.println("Done running");
	}

	public String getIPAddress(int whatPlayer) {
		return ipAddresses[whatPlayer];
	}

	public void setIPAddress(int whatPlayer, String ipAddress) {
		ipAddresses[whatPlayer] = ipAddress;
	}

	public Socket getSocket(int whatSocket) {
		return readers[whatSocket].getSocket();
	}

	public void setSocket(int whatSocket, Socket socket) {
		readers[whatSocket].setSocket(socket);
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
