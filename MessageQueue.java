public class MessageQueue {
    NodeInfo tail;

    public MessageQueue() {
        tail = null;
    }

    public MessageQueue(NodeInfo n) {

    }

    public NodeInfo pop() {
        NodeInfo last = tail;
        if (last != null)
            tail = last.pre;
        return last;
    }

    public NodeInfo read() {
        NodeInfo n = null;
        while (n == null) {
            try {
            Thread.sleep(1);
            } catch (InterruptedException e) {
                
            }
            n = pop();
        }
		System.out.println(n.getCommand());
        return n;
    }

    public void push(NodeInfo n) {
        n.pre = tail;
        tail = n;
    }
}
