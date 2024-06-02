import java.util.LinkedList;

public class UserInputMutex {
    private boolean isAvailable;
    private Integer pid;
    static UserInputMutex instance;
    private UserInputMutex(){
        isAvailable = true;
        pid = null;
    }

    public static UserInputMutex getInstance() {
        if (instance == null)
            instance = new UserInputMutex();
        return instance;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    public void semWait(Process process){
        if (!isAvailable) {
            System.out.println("RESOURCE ALREADY TAKEN");
            // add to blocked queue
            InputBlockedQueue.getInstance().getQueue().add(process.getPid());
            BlockedQueue.getInstance().getQueue().add(process.getPid());
            Scheduler.changePCBStateInMemory(process.getPid() , ProcessState.BLOCKED);
            return;
        }
        isAvailable = false;
        pid = process.getPid();
    }
    public void semSignal(Process process){
        if (process.getPid() != pid){
            System.out.println("RESOURCE IS NOT YOURS");
            return;
        }
        else if (!InputBlockedQueue.getInstance().isEmpty()){
            int p = InputBlockedQueue.getInstance().getQueue().removeFirst();
//            InputBlockedQueue.getInstance().getQueue().add(p);
            LinkedList<Integer> list = BlockedQueue.getInstance().getQueue();
            for (int i = 0 ; i < list.size() ; i++){
                if (list.get(i) == p){
                    list.remove(i);
                    break;
                }
            }
            ReadyQueue.getInstance().getQueue().add(p);
            Scheduler.changePCBStateInMemory(p , ProcessState.READY);
            System.out.println("SEM SIGNAL OCCURED WHILE ONE OR MORE PROCESSES ARE BLOCKED ON RESOURCE, HERE IS THE QUEUES AFTER SIGNALLING");
            Scheduler.printInputBlocked();
            Scheduler.printOutputBlocked();
            Scheduler.printFileBlocked();
            Scheduler.printReadyQueue();
            Scheduler.printBlockedQueue();
            pid = p;
            return;
        }
        isAvailable = true;
        pid = null;

    }
}
