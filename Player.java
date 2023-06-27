import java.io.IOException;
import java.net.Socket;

public class Player extends Thread {

	private Game game;
	private Socket socket;
	private int currentGame;
	private int whichPlayer;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(int currentGame) {
		this.currentGame = currentGame;
	}

	public int getWhichPlayer() {
		return whichPlayer;
	}

	public void setWhichPlayer(int whichPlayer) {
		this.whichPlayer = whichPlayer;
	}

	public Player() {
		currentGame = -1;
	}

	public Player(int currentGame, Game game, Socket socket) {
		this.game = game;
		this.currentGame = currentGame;
		this.socket = socket;
	}

	public void run() {
		int errorCode = Globals.NET_OK;
		String request = "";
		boolean gameOver = false;
		while (!gameOver) {
			request = NetIO.receiveRequest(socket);
			if (request == "") {
				gameOver = true;
			} else {
				System.out.println(request);
				NodeInfo requestFromPlayer = new NodeInfo(request);
				switch (requestFromPlayer.getCommand()) {
					case Globals.REQUEST_TO_PROCESS_PLAY:
						int row = Integer.parseInt(requestFromPlayer.getRowColPlayer().substring(0, 1));
						int col = Integer.parseInt(requestFromPlayer.getRowColPlayer().substring(1, 2));
						game.setMatrixVal(row, col, game.getCurrentPlayer());

						// here change the variable right away. careful with the if below
						// the condition assumes that the change has happened first
						game.setCurrentPlayer(Utils.otherPlayer(game.getCurrentPlayer()));

						int currentPlayer = game.getCurrentPlayer();
						if (currentPlayer == Globals.PLAYER_ONE || currentPlayer == Globals.PLAYER_TWO) {
							errorCode = NetIO.sendRequest(Globals.NULL_STR +
									Globals.COMMAND_YOUR_TURN +
									row + col +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									Utils.getStudentFirstName(game.getUserName(currentPlayer - 1)) + " it's your turn.",
									game.getSocket(currentPlayer - 1));
						} else
							ServerTicTacToe.info.println("Error in ServerTicTacToe. No player");

						// int winner = checkWinner(getMatrix());
						int winner = Matrix.checkWinner(game.getMatrix());
						if (winner != Globals.GAME_STILL_ON) {
							// this delay is here because to requests are sent to the same machine at this
							// point: one just before this 'if' and
							// the other below. The receiving machine cannot read them fast enough so the
							// second request is lost. We slow down
							// the sending machine so that the receiving machine has time to accept the
							// second request. We will need to revise
							// the sendRequest method so that the sending machine waits a little longer
							// until the request has been received. It
							// looks like the errorCode of Globals.NET_OK is not enough
							String message = Globals.NULL_STR;
							if (winner != Globals.NO_WINNER) {
								message = Utils.getStudentFirstName(game.getUserName(winner - 1)) + " wins.";
							} else {
								message = "Game is a tie.";
							}

							errorCode = NetIO.sendRequest(Globals.NULL_STR +
									Globals.COMMAND_GAME_OVER +
									"00" +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									message,
									game.getSocket(0));

							errorCode = NetIO.sendRequest(Globals.NULL_STR +
									Globals.COMMAND_GAME_OVER +
									"00" +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									message,
									game.getSocket(1));
							ServerTicTacToe.dumpGameInResultsWindow(ServerTicTacToe.games, currentGame);
							gameOver = true;
							try {
								game.getSocket(1 - whichPlayer).close();
							} catch (IOException e) {

							}
						}

						break;

					case Globals.REQUEST_TO_DISCONNECT:
						if (!game.getIPAddress(0).equals(Globals.NULL_STR) && // (C6)
								!game.getIPAddress(1).equals(Globals.NULL_STR)) {

							if (requestFromPlayer.getIPAddress().equals(game.getIPAddress(0))) {
								errorCode = NetIO.sendRequest(Globals.NULL_STR +
										Globals.COMMAND_GAME_TERMINATE + "00" +
										Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
										Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
										"Game has been terminated by the other player.",
										game.getSocket(1));
							} else {
								errorCode = NetIO.sendRequest(Globals.NULL_STR +
										Globals.COMMAND_GAME_TERMINATE + "00" +
										Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
										Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
										"Game has been terminated by the other player.",
										game.getSocket(0));
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
							game.getSocket(1 - whichPlayer).close();
						} catch (IOException e) {

						}
						break;

				}
				ServerTicTacToe.dumpInfo(ServerTicTacToe.games);
			}
		}
		ServerTicTacToe.games[currentGame] = new Game(currentGame);
		System.out.println("Done running");
	}

}
