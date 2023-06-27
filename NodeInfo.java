public class NodeInfo {
    private char command = ' ';
    private String rowColPlayer = "";
    private String username = "";
    private String ipAddress = "";
    private String message = "";

    public static void main(String[] args) {
	System.out.println(new NodeInfo(
		(char) 245 + "12" + "000000123456789" + "00010.178.3.100" + "Your opponent is Dan...wait your turn."));
    }

    public NodeInfo() {
	}
    public NodeInfo(String request) {
	command = request.charAt(0);
	rowColPlayer = request.substring(1, 3);
	username = request.substring(3, 18);
	ipAddress = request.substring(18, 33);
	message = request.substring(33);
    }

    public char getCommand() {
	return command;
    }

    public String getRowColPlayer() {
	return rowColPlayer;
    }

    public String getUserName() {
	return username;
    }

    public String getIPAddress() {
	return ipAddress;
    }

    public String getMessage() {
	return message;
    }

    public String toString() {
	return "Command\t\t\t: " + (int) command + "\nRowColPlayer\t\t: " + rowColPlayer + "\nUsername\t\t: " + username + "\nIp Address\t\t: " + ipAddress + "\nMessage\t\t\t: " + message;
	// return String.format(
	//        "Command\t\t\t: %d\nRowColPlayer\t\t: %s\nUsername\t\t: %s\nIp Address\t\t: %s\nMessage\t\t\t: %s\n",
	//        ((int) command), rowColPlayer, username, ipAddress, message);
    }
}
