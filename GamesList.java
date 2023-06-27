public class GamesList {
    public Game head;
    public Game tail;
    public int length = 0;
    public GamesList() {
        this(null);
    }
    public GamesList(Game game) {
        head = game;
        if (head != null) {
            length++;
        }
    }

    public void add(Game game) {
        length++;
		Game pre = tail;
		if (pre == null) {
	        head = game;
		} else {
	        pre.next = game;
		}
        tail = game;
        game.pre = tail;
    }

    public void remove(Game game) {
        length--;
		if (head == game) {
			head = game.next;
			if (game == tail) {
				tail = null;
			} else {
				head.pre = null;
			}
		} else {
			game.pre.next = game.next;
			if (game == tail) {
				tail = game.pre;
			} else {
				game.next.pre = game.pre;
			}
		}
		game.next = null;
		game.pre = null;
    }
}
