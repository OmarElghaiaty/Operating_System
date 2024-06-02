import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Scheduler {
    static int firstProccessArrival = 0;
    static int secondProccessArrival = 1;
    static int thirdProccessArrival = 4;

    static int timeSlice = 2;

    static boolean inc = false;
    static ArrayList<Process> processesInMemory = new ArrayList<>();

    public static PrintingSimulationGUI gui;

        static boolean firstFinished = false;
        static boolean secondFinished = false;
        static boolean thirdFinished = false;

        public static void printInputBlocked(){
            System.out.println("--------------INPUT BLOCKED QUEUE-------------------");
            for (int i = 0 ; i < InputBlockedQueue.getInstance().getQueue().size() ; i++){
                System.out.print(InputBlockedQueue.getInstance().getQueue().get(i) + " ");
            }
            System.out.println();
            System.out.println("-----------------END INPUT BLOCKED QUEUE---------------");
        }

    public static void printOutputBlocked(){
        System.out.println("--------------INPUT BLOCKED QUEUE-------------------");
        for (int i = 0 ; i < OutputBlockedQueue.getInstance().getQueue().size() ; i++){
            System.out.print(OutputBlockedQueue.getInstance().getQueue().get(i) + " ");
        }
        System.out.println();
        System.out.println("-----------------END INPUT BLOCKED QUEUE---------------");
    }
    public static void printFileBlocked(){
        System.out.println("--------------INPUT BLOCKED QUEUE-------------------");
        for (int i = 0 ; i < FileAccessBlockedQueue.getInstance().getQueue().size() ; i++){
            System.out.print(FileAccessBlockedQueue.getInstance().getQueue().get(i) + " ");
        }
        System.out.println();
        System.out.println("-----------------END INPUT BLOCKED QUEUE---------------");
    }
    public static void schedule() {
        int time = 0;
        int remSlice = 0;
        gui = new PrintingSimulationGUI();
        Process currentProcess = null;
        LinkedList<Integer> queue = ReadyQueue.getInstance().getQueue();
        LinkedList<Integer> blocked = BlockedQueue.getInstance().getQueue();
        LinkedList<Integer> blockedOnInput = InputBlockedQueue.getInstance().getQueue();
        LinkedList<Integer> blockedOnOutput = OutputBlockedQueue.getInstance().getQueue();
        LinkedList<Integer> blockedOnFile = FileAccessBlockedQueue.getInstance().getQueue();
        processesInMemory = new ArrayList<>();
        while (true) {
//            System.out.println(time);
            if (time < firstProccessArrival && time < secondProccessArrival && time < thirdProccessArrival) {
                time++;
                continue;
            }
//            System.out.println(time + "<< TIME");
            if (time == firstProccessArrival) {
                ArrayList<String> instructions = readProgram("Program_1");
                Process p = new Process(1, instructions);
                if (processesInMemory.size() < 2)
                    processesInMemory.add(p);
                int start = allocateProcess(p);
                PCB pcb = new PCB(p.getPid(), 0, ProcessState.NEW, start, start + 13);
                assignPCB(pcb);

                queue.add(p.getPid());
                changePCBStateInMemory(p.getPid(), ProcessState.READY);
            } else if (time == secondProccessArrival) {
                ArrayList<String> instructions = readProgram("Program_2");
                Process p = new Process(2, instructions);
                if (processesInMemory.size() < 2)
                    processesInMemory.add(p);
                int start = allocateProcess(p);
                PCB pcb = new PCB(p.getPid(), 0, ProcessState.NEW, start, start + 13);
                assignPCB(pcb);
                queue.add(p.getPid());
                changePCBStateInMemory(p.getPid(), ProcessState.READY);

            } else if (time == thirdProccessArrival) {
                ArrayList<String> instructions = readProgram("Program_3");
                Process p = new Process(3, instructions);
                if (processesInMemory.size() < 2)
                    processesInMemory.add(p);
                int start = allocateProcess(p);
                PCB pcb = new PCB(p.getPid(), 0, ProcessState.NEW, start, start + 13);
                assignPCB(pcb);

                queue.add(p.getPid());
                changePCBStateInMemory(p.getPid(), ProcessState.READY);
            }
            System.out.println("READY QUEUE BEFORE FETCHING INSTRUCTION:");
            printReadyQueue();

            if (queue.size() != 0 && remSlice == 0) {
                int currentProcessID = ReadyQueue.getInstance().getQueue().removeFirst();
                currentProcess = getProcessByID(currentProcessID);
                if (currentProcess == null){
                    int last;
                    if (firstFinished)
                        last = 1;
                    else if (secondFinished)
                        last = 2;
                    else if (thirdFinished)
                        last = 3;
                    else if (!blocked.isEmpty())
                        last = blocked.get(0);
                    else
                        last = ReadyQueue.getInstance().getQueue().getLast();
                    int index = unloadProcess(last);
                    System.out.println("PROCESS WITH ID " + last + " IS SAVED (UNLOADED) TO DISK");
                    loadProcess(currentProcessID , index);
                    System.out.println("PROCESS WITH ID " + currentProcessID + " IS LOADED FROM DISK");
                    currentProcess = getProcessByID(currentProcessID);
                }
                remSlice = timeSlice;
            }
            if (queue.size() == 0 && currentProcess == null) {
                time++;
                System.out.println("NO CURRENTLY ACTIVE PROCESS");
                continue;
            }
            changePCBStateInMemory(currentProcess.getPid(), ProcessState.RUNNING);
            System.out.println("CURRENT CYCLE: " + time);
            System.out.println("CURRENT PROCESS ID: " + currentProcess.getPid());
            int pc = getPCOfProcess(currentProcess);
            String instruction = currentProcess.getInstructions().get(pc);
            String[] instructionDetails = instruction.split(" ");
            if (instructionDetails[0].equals("assign") && instructionDetails[2].equals("input"))
                pc--;
            else if (instructionDetails[0].equals("assign") && instructionDetails[2].equals("readFile"))
                pc--;
            System.out.println("INSTRUCTION TO EXECUTE IS " + instruction);
            System.out.println("READY QUEUE BEFORE EXECUTING");
            printReadyQueue();
            System.out.println("BLOCKED QUEUE BEFORE EXECUTING");
            printBlockedQueue();
            ExecuteInstruction.execute(instruction, currentProcess);
            if (inc){
                System.out.println("INCC UPDATED");
                pc++;
                inc = !inc;
            }
            System.out.println("READY QUEUE AFTER EXECUTING INSTRUCTION");
            printReadyQueue();
            System.out.println("BLOCKED QUEUE AFTER EXECUTING INSTRUCTION");
            printBlockedQueue();
            System.out.println("MEMORY AFTER EXECUTING THE INSTRUCTION");
            pc++;
            changePCBPCInMemory(currentProcess.getPid() , pc);
            printMemory();
            if (currentProcess.getState() == ProcessState.BLOCKED) {
                changePCBStateInMemory(currentProcess.getPid() , ProcessState.BLOCKED);
                remSlice = 0;
                time++;
                printInputBlocked();
                printOutputBlocked();
                printFileBlocked();
                continue;
            }
            time++;
            remSlice--;
            if (pc == currentProcess.getInstructions().size()) {
                changePCBStateInMemory(currentProcess.getPid(), ProcessState.FINISHED);
                if (currentProcess.getPid() == 1)
                    firstFinished = true;
                else if (currentProcess.getPid() == 2)
                    secondFinished = true;
                else
                    thirdFinished = true;
                currentProcess = null;
                remSlice = 0;
            }

            if (currentProcess != null && currentProcess.getState() == ProcessState.RUNNING && remSlice == 0) {
                changePCBStateInMemory(currentProcess.getPid(), ProcessState.READY); // change state
                ReadyQueue.getInstance().getQueue().add(currentProcess.getPid());
            }
            if (firstFinished && secondFinished && thirdFinished)
                break;

        }
    }

    public static void printBlockedQueue() {
        System.out.println("---------------------------BLOCKED-------------------------------");
        LinkedList<Integer> blocked = BlockedQueue.getInstance().getQueue();
        for (int i = 0 ; i < blocked.size() ; i++){
            System.out.print(blocked.get(i) + " ");
        }
        System.out.println();
        System.out.println("--------------------END BLOCKED----------------------------------");
    }

    public static void printReadyQueue() {
        System.out.println("---------------------------READY-------------------------------");
        LinkedList<Integer> queue = ReadyQueue.getInstance().getQueue();
        for (int i = 0 ; i < queue.size() ; i++){
            System.out.print(queue.get(i) + " ");
        }
        System.out.println();
        System.out.println("--------------------END READY----------------------------------");
    }

    public static Process getProcessByID(int pid){
        for (int i = 0 ; i < processesInMemory.size() ; i++){
            if (processesInMemory.get(i).getPid() == pid){
                return processesInMemory.get(i);
            }
        }
        return null;
    }

    public static int getPCOfProcess(Process currentProcess) {
        Object[] memory = Memory.getInstance().getMemory();
        int start = getPCBstart(currentProcess.getPid());
        return (int) memory[start + 2];
    }

    public static ArrayList<String> readProgram(String programName) {
        BufferedReader br = null;
        String itemsPath = programName + ".txt";
        String line = null;
        ArrayList<String> res = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(itemsPath));
            line = br.readLine();
            while (line != null) {
                res.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public static void changePCBStateInMemory(int pid, ProcessState processState) {
        Object[] memory = Memory.getInstance().getMemory();
        int start = getPCBstart(pid);
        memory[start + 1] = processState;
        Process p = getProcessByID(pid);
        if (p == null){

        }
        else
            p.setState(processState);
        //p.setState(processState);
    }

    public static void changePCBPCInMemory(int pid, int pc) {
        Object[] memory = Memory.getInstance().getMemory();
        int start = getPCBstart(pid);
        memory[start + 2] = pc;
    }

    public static void changePCBBoundaries(Process p, String boundaries) {
        Object[] memory = Memory.getInstance().getMemory();
        int start = getPCBstart(p.getPid());
        memory[start + 3] = boundaries;
    }
    public static void printMemory(){
        Object[] memory = Memory.getInstance().getMemory();
        System.out.println("----------------------MEMORY-------------------------------");
        for (int i = 0 ; i < memory.length ; i++){
            if (memory[i] == null){
                System.out.println("CELL " + i + " = NULL");
                continue;
            }
            if (memory[i] instanceof Variable) {
                System.out.println("CELL " + i + " : Variable Name : " + ((Variable) memory[i]).getName() + ", Variable Value = " + ((Variable) memory[i]).getValue());
                continue;
            }
            if (memory[i] instanceof ProcessState) {
                System.out.println("CELL " + i + " = " + memory[i]);
                continue;
            }
            System.out.println("CELL " + i + " = " + memory[i]);



        }
        System.out.println("----------------------END MEMORY--------------------------------");
    }

    public static int allocateProcess(Process p) {
        ArrayList<String> instructions = p.getInstructions();
        Object[] memory = Memory.getInstance().getMemory();
        int startIndex;
        if (memory[12] == null) {
            startIndex = 12;
        } else if (memory[26] == null) {
            startIndex = 26;
        } else {
            LinkedList<Integer> readyQueue = ReadyQueue.getInstance().getQueue();
            LinkedList<Integer> blockedQueue = BlockedQueue.getInstance().getQueue();
            int last;
            if (firstFinished)
                last = 1;
            else if (secondFinished)
                last = 2;
            else if (thirdFinished)
                last = 3;
            else if (!blockedQueue.isEmpty()){
                last = blockedQueue.getLast();
            }
            else
                last = readyQueue.getLast();
            System.out.println("PROCESS WITH ID " + last + " IS SAVED (UNLOADED) TO DISK");
            startIndex = unloadProcess(last);
            processesInMemory.add(p);
        }
        int res = startIndex;
        for (int i = 0; i < instructions.size(); i++) {
            memory[startIndex] = instructions.get(i);
            startIndex++;
            // momkn nthrow exception law process tawela awy wla msh case ?
        }


        return res;

    }

    public static Variable getVariable(int pid , String varName){
        int start = getPCBstart(pid);
        Object[] memory = Memory.getInstance().getMemory();
        String higherBound = memory[start+3].toString().split("-")[1];
        int high = Integer.parseInt(higherBound);
//        System.out.println(varName);
        if ((memory[high]) != null && ((Variable) memory[high]).getName().equals(varName)){
            return (Variable) memory[high];
        }
        if (( memory[high-1]) != null &&((Variable) memory[high-1]).getName().equals(varName)){
            return (Variable) memory[high-1];
        }
        if (( memory[high-2]) != null &&((Variable) memory[high-2]).getName().equals(varName)){
            return (Variable) memory[high-2];
        }
        return null;
    }

    public static void assignProcessVariable(Process p, Object var, String varName) {
        Variable v = getVariable(p.getPid() , varName);
        int start = getPCBstart(p.getPid());
        Object[] memory = Memory.getInstance().getMemory();

        int startInMemory = Integer.parseInt((memory[start + 3] + "").split("-")[0]);
        if (v == null)
            v = new Variable(varName , var);
        else{
            v.setValue(var);
        }
        if (p.getFirstVar() == null) {
//            System.out.println("HOW NULL");
//            System.out.println(p.getPid() + "AOOQQ");
//            System.out.println(p.getFirstVar() + ";;");
            p.setFirstVar(v);
//            System.out.println(p.getFirstVar() + "ALO");
            memory[startInMemory + 14 - 3] = v;
        }
        else if (p.getSecondVar() == null) {
            p.setSecondVar(v);
            memory[startInMemory + 14 - 2] = v;

        }
        else if (p.getThirdVar() == null) {
            p.setThirdVar(v);
            memory[startInMemory + 14 - 1] = v;

        }

//        if (var.equals("null"))
//            System.out.println("ALOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOmmOOOOOm");


    }

    public static int getPCBstart(int pid) {
        Object[] memory = Memory.getInstance().getMemory();
        if (Integer.parseInt(memory[0] + "") == pid)
            return 0;
        else if (Integer.parseInt(memory[4] + "") == pid)
            return 4;
        else
            return 8;
    }

    public static void assignPCB(PCB pcb) {
        Object[] memory = Memory.getInstance().getMemory();
        int startIndex;
        if (memory[0] == null)
            startIndex = 0;
        else if (memory[4] == null)
            startIndex = 4;
        else
            startIndex = 8;
        memory[startIndex] = pcb.pid;
        memory[startIndex + 1] = pcb.processState;
        memory[startIndex + 2] = 0;
        memory[startIndex + 3] = pcb.lowerMemBoundary + "-" + pcb.higherMemBoundary;
    }

    public static int unloadProcess(int last) {
        Object[] memory = Memory.getInstance().getMemory();
        if (Integer.parseInt(memory[0] + "") == last) {
            String memBoundary = memory[3] + "";
            memory[3] = "";
            int index = Integer.parseInt(memBoundary.split("-")[0]);
            saveProcessToDisk(last , 0 , index);
            return index;
        } else if (Integer.parseInt(memory[4] + "") == last) {
            String memBoundary = memory[7] + "";
            memory[7] = "";
            int index = Integer.parseInt(memBoundary.split("-")[0]);
            saveProcessToDisk(last , 4 , index);
            return index;
        } else {
            String memBoundary = memory[11] + "";
            memory[11] = "";
            int index = Integer.parseInt(memBoundary.split("-")[0]);
            saveProcessToDisk(last , 8 , index);
            return index;
        }
    }

    public static void saveProcessToDisk(int last, int i , int index) {
        Object[] memory = Memory.getInstance().getMemory();
        String toWrite = "";
        int pid = Integer.parseInt(memory[i] + "");
        int in = 0;
        toWrite += "PROCESS ID: " + pid + "\n";
        toWrite += "INSTRUCTIONS: \n";
        while(true){
            if (memory[index + in] == null || in == 11)
                break;
            toWrite += (String) memory[index + in] + "\n";
            memory[index + in] = null;

            in++;
        }
        if (memory[index + 11] != null){
            System.out.println(pid);
            Variable v = (Variable)  memory[index + 11];
            toWrite += "Variable name: " + v.getName() + " , Variable value: " + v.getValue() + "\n";
        }
        else if (memory[index + 11] == null){
            toWrite += "Variable Not Yet Set" + "\n";
        }

        if (memory[index + 12] != null){
            Variable v = (Variable)  memory[index + 12];
            toWrite += "Variable name: " + v.getName() + " , Variable value: " + v.getValue() + "\n";
        }
        else if (memory[index + 12]== null)
            toWrite += "Variable Not Yet Set" + "\n";
        if (memory[index + 13] != null){
            Variable v = (Variable)  memory[index + 13];
            toWrite += "Variable name: " + v.getName() + " , Variable value: " + v.getValue() + "\n";
        }
        else if (memory[index+13] == null)
            toWrite += "Variable Not Yet Set" + "\n";
        if (processesInMemory.get(0).getPid() == pid && processesInMemory.get(0).getTempVar() != null)
            toWrite += "Temp Value of this process: " + processesInMemory.get(0).getTempVar();
        else if (processesInMemory.get(1).getPid() == pid && processesInMemory.get(0).getTempVar() != null)
            toWrite += "Temp Value of this process: " +  processesInMemory.get(1).getTempVar();
        else{
            toWrite += "No Temp Value Assigned Yet";
        }

        toWrite += "\n";
        toWrite += "Process State: " +  memory[i + 1] + "\n";
        toWrite += "PC VALUE: " + memory[i + 2];
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Process" + pid + ".txt"))) {
            writer.write(toWrite);
            System.out.println("Content written to the file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
        if (processesInMemory.get(0).getPid() == last)
            processesInMemory.remove(0);
        else
            processesInMemory.remove(1);

    }

    public static void loadProcess(int pid , int index){
        Process p = getProcessFromDisk(pid);
        Object[] memory = Memory.getInstance().getMemory();
        processesInMemory.add(p);
        for (int i = 0 ; i < p.getInstructions().size() ; i++){
            memory[i + index] = p.getInstructions().get(i);
        }
        memory[index + 11] = p.getFirstVar();
        memory[index + 12] = p.getSecondVar();
        memory[index + 13] = p.getThirdVar();
        int start = getPCBstart(pid);
        memory[start + 3] = index + "-" + (index + 13);
    }

    public static Process getProcessFromDisk(int pid){
        ArrayList<String> p = readProgram("Process" + pid);
        ArrayList<String> pInstructions = new ArrayList<>();
        int i = 0;
        while (true){
            if (p.get(i + 2).split(" ")[0].equals("Variable"))
                break;
            if (p.get(i+2).equals("semSignal userOutput"))
                System.out.print("");
            pInstructions.add(p.get(i + 2));
            i++;
        }
        // name > 2 , value > 6
        Process process = new Process(pid , pInstructions);
        if (p.get(i + 2).equals("Variable Not Yet Set")){
            i++;
        }
        else{
            String[] arr = p.get(i+2).split(" ");
            Variable variable = new Variable(arr[2] , arr[6]);
            i++;
            process.setFirstVar(variable);
        }
        if (p.get(i + 2).equals("Variable Not Yet Set")){
            i++;
        }
        else {
            String[] arr = p.get(i+2).split(" ");
            Variable variable = new Variable(arr[2] , arr[6]);
            i++;
            process.setSecondVar(variable);
        }
        if (p.get(i + 2).equals("Variable Not Yet Set")){
            i++;
        }
        else{
            String[] arr = p.get(i+2).split(" ");
            Variable variable = new Variable(arr[2] , arr[6]);
            i++;
            process.setThirdVar(variable);
        }
        if (p.get(i+2).equals("No Temp Value Assigned Yet")){
            process.setTempVar(null);
        }
        else {
            process.setTempVar(p.get(i + 2).split(" ")[5]);
        }
        i++;

        process.setState(getState(pid));
        return process;




    }

    public static ProcessState getState(int pid){
        int start = getPCBstart(pid);
        Object[] memory = Memory.getInstance().getMemory();

        if (memory[start + 1].toString().equalsIgnoreCase("new"))
            return ProcessState.NEW;
        if (memory[start + 1].toString().equalsIgnoreCase("ready"))
            return ProcessState.READY;
        if (memory[start + 1].toString().equalsIgnoreCase("running"))
            return ProcessState.RUNNING;
        if (memory[start + 1].toString().equalsIgnoreCase("finished"))
            return ProcessState.FINISHED;
        if (memory[start + 1].toString().equalsIgnoreCase("blocked"))
            return ProcessState.BLOCKED;
        return ProcessState.BLOCKED;
    }

    public static void main(String[] args) {
        schedule();
        System.out.println();
    }
}
