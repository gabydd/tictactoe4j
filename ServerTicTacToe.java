// Needed to run this server:
// 1. Java RTP: a) Code will run within Java RTP
//              b) If running from another platform, then compilation and running commands need access to the RTP classes of hsa
// 2. Globals.java containing some extra constants (posted)
// 3. Utils.java containing methods that locate students names (posted)
// 4. "RealStudents.txt" file containing computer account log ins and names: See sample posted file for format. Be sure
//    to update Globals.TOTAL_STUDENTS to the correct number of entries in the RealStudents.txt file
// 5. FileIO.java to load the students into an array
// 6. Game.java

import hsa.*;
import javax.swing.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.*;
import java.io.IOException;

public class ServerTicTacToe {
	public static int infoRows = 4 + 4 * Globals.TOTAL_GAMES / 2 + 4; // rows = 4 header lines + 4 lines per game *
																		// TOTAL_GAMES / 2 (2 columns of games
																		// displayed) + 4 extra lines for error messages
	public static Console info = new Console(infoRows, 125);
	public static Console results = new Console(30, 40, "Game Results"); // row, col, title
	public static NodeInfo requestFromPlayer = new NodeInfo();
	private static ServerSocket serverSocket;
	public static GamesList games = new GamesList();

	public static void main(String args[]) {
		FileIO.readFileIntoArray("RealStudents.txt", Globals.realStudents); // for efficiency set Globals.TOTAL_STUDENTS
																			// to the exact number of students in the
																			// txt file
		try {
			serverSocket = new ServerSocket(Globals.PORT_NUMBER);
		} catch (Exception e) {
			System.out.println(e);
		}
		dumpInfo(games);

		int errorCode = Globals.NET_OK;
		String request = "";

		do {
			System.out.println("Waiting...");
			try {
				Socket socket = serverSocket.accept();
				request = NetIO.receiveRequest(socket);
				requestFromPlayer = new NodeInfo(request);

				if (parseOk(requestFromPlayer)) {
					Game game = games.tail;
					int currentGame = games.length - 1;
					if (game == null || !game.getIPAddress(1).equals(Globals.NULL_STR)) {
						currentGame++;
						game = new Game(currentGame);
						games.add(game);
					}

					// these two lines erase after the message "Both clients have been disconnected
					// from game" is printed out
					info.setCursor(infoRows - 2, 1);
					info.println();

					if (currentGame != Globals.NO_MORE_SLOTS
							&& requestFromPlayer.getCommand() == Globals.REQUEST_TO_PLAY_GAME) {
						if (game.getIPAddress(0).equals(Globals.NULL_STR)) {

							game.setIPAddress(0, requestFromPlayer.getIPAddress());
							game.setSocket(0, socket);
							game.setUserName(0, requestFromPlayer.getUserName());
							errorCode = NetIO.sendRequest(Globals.NULL_STR +
									Globals.COMMAND_TO_WAIT + "00" +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									"You are player 1...wait for another player",
									game.getSocket(0));

							// initial Player 1 request: PLAYER_ONE now waits
						} else if (!game.getIPAddress(0).equals(Globals.NULL_STR) && // Player one is
																						// waiting
								game.getIPAddress(1).equals(Globals.NULL_STR)) {

							if (playerConnected(0)) {
								game.setIPAddress(1, requestFromPlayer.getIPAddress());
								game.setSocket(1, socket);
								game.setUserName(1, requestFromPlayer.getUserName());
								game.setCurrentPlayer(Globals.PLAYER_ONE);

								// tell PLAYER_TWO to wait for PLAYER_ONE to make the first move and inform who
								// is the opponent
								String playerOneName = Utils.getStudentFirstName(game.getUserName(0));
								errorCode = NetIO.sendRequest(Globals.NULL_STR +
										Globals.COMMAND_TO_START_GAME +
										Globals.PLAYER_TWO + "0" +
										Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
										Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
										"Your opponent is " + playerOneName + ". Please wait for your turn...",
										game.getSocket(1));

								// tell PLAYER_ONE to start playing and inform who is the opponent

								String playerTwoName = Utils.getStudentFirstName(game.getUserName(1));
								errorCode = NetIO.sendRequest(Globals.NULL_STR +
										Globals.COMMAND_TO_START_GAME +
										Globals.PLAYER_ONE + "0" +
										Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
										Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
										"You may play...your opponent is " + playerTwoName + ".",
										game.getSocket(0));
								// p1,p2 start playing the game
								game.start();
							}
						} else
							info.println(Globals.DEBUG_ON ? "*** error: REQUEST_TO_PLAY case not possible" : "");
					} else {
						NetIO.sendRequest(Globals.NULL_STR +
								Globals.COMMAND_DISPLAY_MESSAGE + "00" +
								Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
								Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
								"Server is currently full",
								socket); // send a message to client that there is no more room for another game
					}
				} else {
					// client sent an invalid request
				}
			} catch (IOException e) {
			}
			ServerTicTacToe.dumpInfo(ServerTicTacToe.games);
		} while (true);
	}

	private static boolean playerConnected(int whatPlayer) {
		return true;
	}

	private static boolean parseOk(NodeInfo request) {
		return true;
	}

	public static void dumpInfo(GamesList games) {
		info.setCursor(1, 1);
		for (int i = 0; i < 125; i++)
			info.print("-");
		info.println();
		info.println(" My IP Address here is: " + Utils.myIPAddress());
		info.println(" Last request is: " + (int) requestFromPlayer.getCommand() +
				" from " + Utils.getStudentFirstName(requestFromPlayer.getUserName()) +
				" from address: " + requestFromPlayer.getIPAddress());
		for (int i = 0; i < 125; i++)
			info.print("-");
		info.println();
		int i = 0;
		Game game = games.head;
		while (game != null) {
			info.print(" Board: ");
			for (int row = 0; row < Globals.ROWS; row++) {
				for (int col = 0; col < Globals.COLS; col++) {
					info.print(game.getMatrixVal(row, col) + Globals.BLANK_STR);
				}
				switch (row) {
					case 0:
						info.print("Game: " + i);
						if (i < 10)
							info.print("  ");
						else
							info.print(" ");
						info.print("Current : " + game.getCurrentPlayer());
						if (game.next != null) {
							info.print("                             Board: ");
						}
						break;

					case 1:
						if (game.getIPAddress(0).equals(Globals.NULL_STR))
							info.print("         Player 1: " + Utils.rightPad("", 28, ' '));
						else
							info.print("         Player 1: "
									+ Utils.rightPad(Utils.getStudentFirstName(game.getUserName(0)), 28, ' '));
						info.print("         ");
						break;

					case 2:
						if (game.getIPAddress(1).equals(Globals.NULL_STR))
							info.print("         Player 2: " + Utils.rightPad("", 28, ' '));
						else
							info.print("         Player 2: "
									+ Utils.rightPad(Utils.getStudentFirstName(game.getUserName(1)), 28, ' '));
						info.print("         ");
						break;
				}

				if (game.next != null) {
					for (int col = 0; col < Globals.COLS; col++) {
						info.print(game.next.getMatrixVal(row, col) + Globals.BLANK_STR);
					}

					switch (row) {
						case 0:
							info.print("Game: " + (i + 1));
							if (i + 1 < 10)
								info.print("  ");
							else
								info.print(" ");
							info.print("Current : " + game.next.getCurrentPlayer());
							break;

						case 1:
							if (game.next.getIPAddress(0).equals(Globals.NULL_STR))
								info.print("         Player 1: " + Utils.rightPad("", 28, ' '));
							else
								info.print("         Player 1: "
										+ Utils.rightPad(Utils.getStudentFirstName(game.next.getUserName(0)), 28, ' '));
							break;

						case 2:
							if (game.next.getIPAddress(1).equals(Globals.NULL_STR))
								info.print("         Player 2: " + Utils.rightPad("", 28, ' '));
							else
								info.print("         Player 2: "
										+ Utils.rightPad(Utils.getStudentFirstName(game.next.getUserName(1)), 28, ' '));
							break;
					}
				}
				if (row < Globals.ROWS - 1) {
					info.println();
					info.print("        ");
				}
			}
			info.println();
			info.println();
			game = game.next != null ? game.next.next : null;
			i += 1;
		}
	}

	public static void dumpGameInResultsWindow(Game game, int currentGame) {
		results.println("================================");
		results.println("Finished Game: " + currentGame);
		results.println("X: " + Utils.getStudentFirstName(game.getUserName(0)));
		results.println("O: " + Utils.getStudentFirstName(game.getUserName(1)));
		for (int row = 0; row < Globals.ROWS; row++) {
			for (int col = 0; col < Globals.COLS; col++) {
				if (game.getMatrixVal(row, col) == Globals.PLAYER_ONE)
					results.print("X");
				else if (game.getMatrixVal(row, col) == Globals.PLAYER_TWO)
					results.print("O");
				else
					results.print(" ");

				if (col < Globals.COLS - 1) {
					results.print("|");
				}

			}
			results.println();
			if (row < Globals.ROWS - 1) {
				results.println("-----");
			}
		}
		results.println("================================");
	}
}