import java.util.ArrayList;

public class Process {
    private int pid;
    private ProcessState state;
    private ArrayList<String> instructions;
    private Object tempVar;

    private Variable firstVar;
    private Variable secondVar;
    private Variable thirdVar;

    public Variable getFirstVar() {
        return firstVar;
    }

    public void setFirstVar(Variable firstVar) {
        this.firstVar = firstVar;
    }

    public Variable getThirdVar() {
        return thirdVar;
    }

    public void setThirdVar(Variable thirdVar) {
        this.thirdVar = thirdVar;
    }

    public Variable getSecondVar() {
        return secondVar;
    }

    public void setSecondVar(Variable secondVar) {
        this.secondVar = secondVar;
    }

    public Object getTempVar() {
        return tempVar;
    }

    public void setTempVar(Object tempVar) {
        this.tempVar = tempVar;
    }

    public Process(int pid , ArrayList<String> instructions) {
        this.pid = pid;
        state = ProcessState.NEW;
        this.instructions = instructions;
        tempVar = null;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public int getPid() {
        return pid;
    }



    public void setPid(int pid) {
        this.pid = pid;
    }


    public ArrayList<String> getInstructions() {
        return this.instructions;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }
}
