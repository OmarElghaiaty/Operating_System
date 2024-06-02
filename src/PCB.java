public class PCB {
    int pid;
    int pc;
    ProcessState processState;
    int lowerMemBoundary;
    int higherMemBoundary;

    public PCB(int pid, int pc, ProcessState processState, int lowerMemBoundary, int higherMemBoundary) {
        this.pid = pid;
        this.pc = pc;
        this.processState = processState;
        this.lowerMemBoundary = lowerMemBoundary;
        this.higherMemBoundary = higherMemBoundary;
    }


}
