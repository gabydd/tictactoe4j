import javax.swing.JOptionPane;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;

public class Utils {
	public static final char DOT = '.';
	public static final String DOT_DOT = "..";

	public static String initialNetworkConnection() {
		String ipAddress = null;
		String[] serversNames = { Globals.SERVER1_NAME,
				Globals.SERVER2_NAME,
				Globals.SERVER3_NAME,
				Globals.SERVER4_NAME };

		String[] serversAddresses = { Globals.SERVER1_ADDRESS,
				Globals.SERVER2_ADDRESS,
				Globals.SERVER3_ADDRESS,
				Globals.SERVER4_ADDRESS };

		String serverName = (String) JOptionPane.showInputDialog(null,
				"Choose a Server",
				"The TTT Connection",
				Globals.OK_CANCEL,
				null,
				serversNames,
				Globals.SERVER1_NAME);
		if (serverName != null) {
			int i = 0;
			for (; !serverName.equals(serversNames[i]); i++)
				;
			ipAddress = serversAddresses[i];
		}

		if (ipAddress != null) {
			if (validIPFormat(ipAddress)) {
				try {
					ClientTicTacToe.socket = new Socket(Globals.serverIPAddress, Globals.PORT_NUMBER);
				} catch (IOException e) {
					System.out.println(e);
				}
				int errorCode = NetIO.sendRequest(Globals.NULL_STR +
						Globals.REQUEST_TO_PLAY_GAME + "00" +
						leftPad(NetIO.myUserName(), Globals.CLIENT_ID_LENGTH, '0') +
						leftPad("1.1.1.1", Globals.MAX_IP_ADDRESS_LENGTH, '0') +
						Globals.NO_MESSAGE,
						ClientTicTacToe.socket);
				if (errorCode != Globals.NET_OK) {
					JOptionPane.showMessageDialog(null,
							"Timed out. Server not found",
							"The Tic Tac Toe Connection",
							JOptionPane.ERROR_MESSAGE);
					ipAddress = null;
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Invalid IP address",
						"The Tic Tac Toe Connection",
						JOptionPane.ERROR_MESSAGE);
				ipAddress = null;
			}
		}

		return ipAddress;
	}

	private static boolean threeSeparateDotsInsideString(String s) {
		int count = 0;

		if (s != null && s.length() > 0 && (s.indexOf(DOT_DOT) == -1)) {
			if (s.charAt(0) != DOT && s.charAt(s.length() - 1) != DOT) {
				for (int i = 0; i < s.length(); i++)
					if (s.charAt(i) == DOT)
						count++;
			}
		}
		return count == 3;
	}

	public static boolean isANumber(String s) {
		boolean result = true;
		for (int i = 0; i < s.length(); i++)
			result = result && Character.isDigit(s.charAt(i));
		return result;
	}

	public static boolean validIPFormat(String s) {
		boolean result = false;
		boolean goAhead = true;
		String t = "";

		if (threeSeparateDotsInsideString(s)) {
			for (int dot = 1; dot <= 3 && goAhead; dot++) {
				t = s.substring(0, s.indexOf(DOT));
				s = s.substring(s.indexOf(DOT) + 1); // From previous test we are guaranteed DOT is not the last
														// character

				if (isANumber(t)) {
					if (Integer.parseInt(t) < 0 || Integer.parseInt(t) > 255)
						goAhead = false;
				} else
					goAhead = false;
			}
			t = s;
			result = goAhead && isANumber(t) && Integer.parseInt(t) >= 0 && Integer.parseInt(t) <= 255;
		}
		return result;
	}

	/*
	 * This method pads text to the left with the paddingItem as many times as
	 * necessary so that the padding plus the text will have the desiredLength.
	 * If the text.length is larger than desiredLength then the method returns
	 * text unchanged. If text.length() is zero, then a padding of desiredLength
	 * will be returned
	 */
	public static String leftPad(String text, int desiredLength, char paddingItem) {
		String padding = Globals.NULL_STR;
		for (int i = 0; i < desiredLength - text.length(); i++)
			padding = padding + paddingItem;
		return padding + text;
	}

	public static String rightPad(String text, int desiredLength, char paddingItem) {
		String padding = Globals.NULL_STR;
		for (int i = 0; i < desiredLength - text.length(); i++)
			padding = padding + paddingItem;
		return text + padding;
	}

	// this method strips text from the left of the character
	// strip item. as soon as a different character is found
	// the method returns what's left over

	public static String leftStrip(String text, char stripItem) {
		int i = 0;
		while (text.charAt(i) == stripItem)
			i++;
		return text.substring(i);
	}

	public static void delay(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			if (Globals.DEBUG_ON)
				System.out.println(ex);
		}
	}

	public static int otherPlayer(int cp) {
		if (cp == Globals.PLAYER_ONE)
			return Globals.PLAYER_TWO;
		else if (cp == Globals.PLAYER_TWO)
			return Globals.PLAYER_ONE;
		else
			return Globals.NO_PLAYER;
	}

	public static String myIPAddress() {
		String ipAddress = new String(Globals.NULL_STR);
		try {
			InetAddress me = InetAddress.getLocalHost();
			ipAddress = me.getHostAddress();

		} catch (Exception e) {
			if (Globals.DEBUG_ON)
				e.printStackTrace();
		}
		return (ipAddress);
	}

	public static void updateStatusLine(String message) {
		Globals.status.setText(" Status: " + message);
	}

	// these methods for student testing of the server sending
	// requests to them so that their receive threads are working properly
	public static String reverse(String text) {
		String t = "";
		for (int i = text.length() - 1; i >= 0; i--)
			t = t + text.charAt(i);
		return t;
	}

	public static String getStudentName(String studentNo) {
		if (!studentNo.equals(Globals.NULL_STR)) {
			boolean found = false;
			int i = 0;
			for (i = 0; i < Globals.TOTAL_STUDENTS && !found; i++)
				found = Globals.realStudents[i].indexOf(studentNo) == 0;
			return (found ? Globals.realStudents[i - 1] : studentNo);
		} else {
			return (Globals.NONE);
		}
	}

	public static String getStudentFirstName(String studentNo) {
		if (!studentNo.equals(Globals.NULL_STR)) {
			String studentName = reverse(getStudentName(studentNo));

			if (studentName.indexOf(Globals.COMMA_STR) != -1)
				studentName = studentName.substring(0, studentName.indexOf(Globals.COMMA_STR));
			return reverse(studentName);
		} else {
			return (Globals.NONE);
		}
	}

	public static String composeRequest(char requestType, String rowColPlayer, String msg) {
		return Globals.NULL_STR +
				requestType +
				rowColPlayer +
				leftPad(System.getProperty("user.name"), Globals.CLIENT_ID_LENGTH, '0') +
				msg;

	}
}
