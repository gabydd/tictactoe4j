import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetIO {
	public static void main(String[] args) {
	}

	public static String myIPAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static boolean isANumber(String num) {
		boolean result = true;
		for (int i = 0; i < num.length(); i++)
			result = result && Character.isDigit(num.charAt(i));
		return result;
	}

	public static int sendRequest(String message, Socket socket) {
		System.out.println("sendRequest");
		System.out.println(message);
		int errorCode = Globals.NET_OK;
			try {
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(message);
			} catch (IOException e) {
				System.out.println(e);
			}
		return errorCode;
	}


	public static String receiveRequest(Socket socket) {
		System.out.println("receiveRequest");
		String request = "";
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			request = in.readUTF();
			System.out.println(request);
		} catch (IOException e) {
			System.out.println("socket closed");
			e.printStackTrace();
		}
		return request;
	}

	public static String myUserName() {
		try {
			return System.getProperty("user.name");
		} catch (Exception e) {
			System.out.println("Can't get username");
			return "";
		}
	}
}
