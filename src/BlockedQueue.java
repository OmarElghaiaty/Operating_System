import java.util.LinkedList;

public class BlockedQueue {
    private LinkedList<Integer> queue;
    private static BlockedQueue instance;
    private BlockedQueue() {
        this.queue = new LinkedList<>();
    }

    public static BlockedQueue getInstance(){
        if (instance == null)
            instance = new BlockedQueue();
        return instance;
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public LinkedList<Integer> getQueue() {
        return queue;
    }
}
