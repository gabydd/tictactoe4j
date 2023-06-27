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
	private static NodeInfo requestFromPlayer = new NodeInfo();
	private static ServerSocket serverSocket;
	public static Game[] games = new Game[Globals.TOTAL_GAMES];

	public static void main(String args[]) {
		FileIO.readFileIntoArray("RealStudents.txt", Globals.realStudents); // for efficiency set Globals.TOTAL_STUDENTS
																			// to the exact number of students in the
																			// txt file
		try {
			serverSocket = new ServerSocket(Globals.PORT_NUMBER);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < Globals.TOTAL_GAMES; i++)
			games[i] = new Game(i);
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
				int currentGame = findSpot(games, requestFromPlayer.getIPAddress());

				// these two lines erase after the message "Both clients have been disconnected
				// from game" is printed out
				info.setCursor(infoRows - 2, 1);
				info.println();

				if (currentGame != Globals.NO_MORE_SLOTS
						&& requestFromPlayer.getCommand() == Globals.REQUEST_TO_PLAY_GAME) {
					if (games[currentGame].getIPAddress(0).equals(Globals.NULL_STR)) {

						games[currentGame].setIPAddress(0, requestFromPlayer.getIPAddress());
						games[currentGame].setSocket(0, socket);
						games[currentGame].setUserName(0, requestFromPlayer.getUserName());
						errorCode = NetIO.sendRequest(Globals.NULL_STR +
								Globals.COMMAND_TO_WAIT + "00" +
								Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
								Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
								"You are player 1...wait for another player",
								games[currentGame].getSocket(0));

						// initial Player 1 request: PLAYER_ONE now waits
					} else if (!games[currentGame].getIPAddress(0).equals(Globals.NULL_STR) && // Player one is waiting
							games[currentGame].getIPAddress(1).equals(Globals.NULL_STR)) {

						if (playerConnected(0)) {
							games[currentGame].setIPAddress(1, requestFromPlayer.getIPAddress());
							games[currentGame].setSocket(1, socket);
							games[currentGame].setUserName(1, requestFromPlayer.getUserName());
							games[currentGame].setCurrentPlayer(Globals.PLAYER_ONE);

							// tell PLAYER_TWO to wait for PLAYER_ONE to make the first move and inform who
							// is the opponent
							String playerOneName = Utils.getStudentFirstName(games[currentGame].getUserName(0));
							errorCode = NetIO.sendRequest(Globals.NULL_STR +
									Globals.COMMAND_TO_START_GAME +
									Globals.PLAYER_TWO + "0" +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									"Your opponent is " + playerOneName + ". Please wait for your turn...",
									games[currentGame].getSocket(1));

							// tell PLAYER_ONE to start playing and inform who is the opponent

							String playerTwoName = Utils.getStudentFirstName(games[currentGame].getUserName(1));
							errorCode = NetIO.sendRequest(Globals.NULL_STR +
									Globals.COMMAND_TO_START_GAME +
									Globals.PLAYER_ONE + "0" +
									Utils.leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
									Utils.leftPad(NetIO.myIPAddress(), Globals.MAX_IP_ADDRESS_LENGTH, '0') +
									"You may play...your opponent is " + playerTwoName + ".",
									games[currentGame].getSocket(0));
							// p1,p2 start playing the game
							games[currentGame].start();
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

			requestFromPlayer = new NodeInfo();
			} catch (IOException e) {
			}
		} while (true);
	}

	private static boolean playerConnected(int whatPlayer) {
		return true;
	}

	private static boolean parseOk(NodeInfo request) {
		return true;
	}

	public static void dumpInfo(Game[] games) {
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

		for (int i = 0; i < Globals.TOTAL_GAMES; i = i + 2) {
			info.print(" Board: ");
			for (int row = 0; row < Globals.ROWS; row++) {
				for (int col = 0; col < Globals.COLS; col++) {
					info.print(games[i].getMatrixVal(row, col) + Globals.BLANK_STR);
				}
				switch (row) {
					case 0:
						info.print("Game: " + i);
						if (i < 10)
							info.print("  ");
						else
							info.print(" ");
						info.print("Current : " + games[i].getCurrentPlayer());
						info.print("                             Board: ");
						break;

					case 1:
						if (games[i].getIPAddress(0).equals(Globals.NULL_STR))
							info.print("         Player 1: " + Utils.rightPad("", 28, ' '));
						else
							info.print("         Player 1: "
									+ Utils.rightPad(Utils.getStudentFirstName(games[i].getUserName(0)), 28, ' '));
						info.print("         ");
						break;

					case 2:
						if (games[i].getIPAddress(1).equals(Globals.NULL_STR))
							info.print("         Player 2: " + Utils.rightPad("", 28, ' '));
						else
							info.print("         Player 2: "
									+ Utils.rightPad(Utils.getStudentFirstName(games[i].getUserName(1)), 28, ' '));
						info.print("         ");
						break;
				}

				for (int col = 0; col < Globals.COLS; col++) {
					info.print(games[i + 1].getMatrixVal(row, col) + Globals.BLANK_STR);
				}

				switch (row) {
					case 0:
						info.print("Game: " + (i + 1));
						if (i + 1 < 10)
							info.print("  ");
						else
							info.print(" ");
						info.print("Current : " + games[i + 1].getCurrentPlayer());
						break;

					case 1:
						if (games[i + 1].getIPAddress(0).equals(Globals.NULL_STR))
							info.print("         Player 1: " + Utils.rightPad("", 28, ' '));
						else
							info.print("         Player 1: "
									+ Utils.rightPad(Utils.getStudentFirstName(games[i + 1].getUserName(0)), 28, ' '));
						break;

					case 2:
						if (games[i + 1].getIPAddress(1).equals(Globals.NULL_STR))
							info.print("         Player 2: " + Utils.rightPad("", 28, ' '));
						else
							info.print("         Player 2: "
									+ Utils.rightPad(Utils.getStudentFirstName(games[i + 1].getUserName(1)), 28, ' '));
						break;
				}

				if (row < Globals.ROWS - 1) {
					info.println();
					info.print("        ");
				}
			}
			info.println();
			info.println();
		}
	}

	public static void dumpGameInResultsWindow(Game[] games, int currentGame) {
		results.println("================================");
		results.println("Finished Game: " + currentGame);
		results.println("X: " + Utils.getStudentFirstName(games[currentGame].getUserName(0)));
		results.println("O: " + Utils.getStudentFirstName(games[currentGame].getUserName(1)));
		for (int row = 0; row < Globals.ROWS; row++) {
			for (int col = 0; col < Globals.COLS; col++) {
				if (games[currentGame].getMatrixVal(row, col) == Globals.PLAYER_ONE)
					results.print("X");
				else if (games[currentGame].getMatrixVal(row, col) == Globals.PLAYER_TWO)
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

	private static int findSpot(Game[] games, String key) {
		// search to see if player is already on a game
		for (int row = 0; row < Globals.TOTAL_GAMES; row++)
			if (key.equals(games[row].getIPAddress(0)) ||
					key.equals(games[row].getIPAddress(1)))
				return row;

		// search to see if there is a waiting player
		for (int row = 0; row < Globals.TOTAL_GAMES; row++)
			if (!games[row].getIPAddress(0).equals(Globals.NULL_STR) &&
					games[row].getIPAddress(1).equals(Globals.NULL_STR))
				return row;

		// search to see if there is an empty row so player can wait
		for (int row = 0; row < Globals.TOTAL_GAMES; row++)
			if (games[row].getIPAddress(0).equals(Globals.NULL_STR) &&
					games[row].getIPAddress(1).equals(Globals.NULL_STR))
				return row;

		// at this point the game array is full
		return Globals.NO_MORE_SLOTS;
	}
}