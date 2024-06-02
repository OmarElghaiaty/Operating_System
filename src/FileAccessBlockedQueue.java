import java.util.LinkedList;

public class FileAccessBlockedQueue {
    private LinkedList<Integer> queue;
    private static FileAccessBlockedQueue instance;
    private FileAccessBlockedQueue() {
        this.queue = new LinkedList<>();
    }

    public static FileAccessBlockedQueue getInstance(){
        if (instance == null)
            instance = new FileAccessBlockedQueue();
        return instance;
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public LinkedList<Integer> getQueue() {
        return queue;
    }
}
