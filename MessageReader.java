import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageReader extends Thread {
	private Game game = null;
	private Socket socket = null;
	private MessageQueue messageQueue = null;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public MessageQueue getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(MessageQueue messageQueue) {
		this.messageQueue = messageQueue;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public MessageReader() {

	}

	public void run() {
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String request;
			while (true) {
				request = in.readUTF();
				System.out.println("Read message:");
				System.out.println(request);
				int errorCode = Globals.NET_RECEIVE_ERROR;
				if (NetIO.isANumber(request)) {
					errorCode = Integer.parseInt(request);
					if (errorCode == Globals.NET_OK) {
						messageQueue.push(new NodeInfo());
					}
				} else {
					messageQueue.push(new NodeInfo(request));
					out.writeUTF("" + Globals.NET_OK);
				}
			}
		} catch (IOException e) {

		}
	}
}
