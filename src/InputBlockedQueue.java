import java.util.LinkedList;

public class InputBlockedQueue {
    private LinkedList<Integer> queue;
    private static InputBlockedQueue instance;
    private InputBlockedQueue() {
        this.queue = new LinkedList<>();
    }

    public static InputBlockedQueue getInstance(){
        if (instance == null)
            instance = new InputBlockedQueue();
        return instance;
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public LinkedList<Integer> getQueue() {
        return queue;
    }
}
