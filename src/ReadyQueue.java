import java.util.LinkedList;

public class ReadyQueue {
        private LinkedList<Integer> queue;
        private static ReadyQueue instance;
        private ReadyQueue() {
            this.queue = new LinkedList<>();
        }

        public boolean isEmpty(){
            return queue.isEmpty();
        }

        public static ReadyQueue getInstance(){
            if (instance == null)
                instance = new ReadyQueue();
            return instance;
        }

        public LinkedList<Integer> getQueue() {
            return queue;
        }


}
