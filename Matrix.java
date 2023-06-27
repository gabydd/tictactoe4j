public class Matrix {
    public static int checkWinner(int[][] board) {
	int ret = get(board, 5) != Globals.NO_PLAYER ? Globals.GAME_TIE : Globals.GAME_STILL_ON;
	int[][] positions = new int[][]{{1, 0, 2}, {4, 3, 5}, {8, 7, 6}, {3, 0, 6}, {7, 4, 1}, {2, 5, 8}, {0, 4, 8}, {6, 4, 2}};
	for (int i = 0; i < positions.length; i++) {
	    int a = get(board, positions[i][0]);
	    int b = get(board, positions[i][1]);
	    int c = get(board, positions[i][2]);
	    if (a == Globals.NO_PLAYER) {
		ret = ret < 1 ? Globals.GAME_STILL_ON : ret;
	    } else if (a == b && a == c) {
		ret = a;
	    }
	}
	return ret;
    }

    public static int get(int[][] board, int n) {
	return board[n / 3][n % 3];
    }
}
