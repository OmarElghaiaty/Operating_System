import java.util.LinkedList;

public class OutputBlockedQueue {
    private LinkedList<Integer> queue;
    private static OutputBlockedQueue instance;
    private OutputBlockedQueue() {
        this.queue = new LinkedList<>();
    }

    public static OutputBlockedQueue getInstance(){
        if (instance == null)
            instance =  new OutputBlockedQueue();
        return instance;
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public LinkedList<Integer> getQueue() {
        return queue;
    }
}
