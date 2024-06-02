import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ExecuteInstruction {

    public static void execute(String instruction , Process p){
        String[] ins = instruction.split(" ");
        if (ins[0].equals("semWait")){
            if (ins[1].equals("userInput")){
                UserInputMutex u = UserInputMutex.getInstance();
                u.semWait(p);
            }
            else if (ins[1].equals("userOutput")){
                UserOutputMutex u = UserOutputMutex.getInstance();
                u.semWait(p);
            }
            else{
                FileMutex f = FileMutex.getInstance();
                f.semWait(p);
            }
        }
        else if (ins[0].equals("semSignal")){
            if (ins[1].equals("userInput")){
                UserInputMutex u = UserInputMutex.getInstance();
                u.semSignal(p);
            }
            else if (ins[1].equals("userOutput")){
                UserOutputMutex u = UserOutputMutex.getInstance();
                u.semSignal(p);
            }
            else{
                FileMutex f = FileMutex.getInstance();
                f.semSignal(p);
            }
        }
        else if (ins[0].equals("printFromTo")){
            int first = Integer.parseInt(Scheduler.getVariable(p.getPid() , ins[1]).getValue().toString());
            int second = Integer.parseInt(Scheduler.getVariable(p.getPid() , ins[2]).getValue().toString());
            //switch (ins[1]){
              //  case "a" : first = Integer.parseInt(p.getA() + "");break;
                //case "b" : first = Integer.parseInt(p.getB() + "");break;
               // default: first = Integer.parseInt(p.getTemp() + "");break;
            //}
            //switch (ins[2]){
              //  case "a" : second = Integer.parseInt(p.getA() + "");break;
                //case "b" : second = Integer.parseInt(p.getB() + "");break;
                //default:  second = Integer.parseInt(p.getTemp() + "");break;
            //}
            printFromTo(first , second);
        }
        else if(ins[0].equals("readFile")){
            String s = readFile(ins[1] , p);
//            Scheduler.assignProcessVariable(p , s , "temp");
        }
        else if (ins[0].equals("writeFile")){
            writeFile(Scheduler.getVariable(p.getPid() , ins[1]).getValue().toString() , Scheduler.getVariable(p.getPid() , ins[2]).getValue().toString() , p);
        }
        else if (ins[0].equals("assign")){
            if (ins[2].equals("input")){
                if (p.getTempVar() == null) {
                    Scanner sc = new Scanner(System.in);
                    int pc = Scheduler.getPCOfProcess(p);
                    String instructions = p.getInstructions().get(pc);
                    System.out.println(instructions);
                    System.out.println(p.getPid());
                    System.out.print("PLEASE ENTER VALUE : ");
                    String in = Scheduler.gui.getUserInput();
                    p.setTempVar(in);
                    return;
                }
                else{
                    Scheduler.assignProcessVariable(p , p.getTempVar() , ins[1]);
                    Scheduler.inc = true;
                    p.setTempVar(null);
                }
            }
            else if (ins[2].equals("readFile")){ // assign a readFile b
                if (p.getTempVar() == null){
                    String s = readFile(Scheduler.getVariable(p.getPid() , ins[3]).getValue().toString() , p);
                    p.setTempVar(s);
                }
                else {
                    Scheduler.assignProcessVariable(p, p.getTempVar(), ins[1]);
                    Scheduler.inc = true;
                    p.setTempVar(null);
                }
            }
        }
        else if (ins[0].equals("print")){
            print(Scheduler.getVariable(p.getPid() , ins[1]).getValue().toString());
        }



    }

    private static void print(String in) {
        System.out.println(in);
    }

    private static void writeFile(String filename, String text , Process p) {
        try {

            FileWriter writer = new FileWriter(filename);
            writer.write(text);
            writer.close();
            System.out.println("Successfully wrote to file: " + filename);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file: " + filename);
            e.printStackTrace();
        }
    }

    private static String readFile(String in , Process p) {
        BufferedReader br = null;
        String itemsPath = in;
        String line = null;
        String res = "";
        try {
            br = new BufferedReader(new FileReader(itemsPath));
            line = br.readLine();
            int i = 0;
            while (line != null) {
                if (i > 0)
                    res += "\n";
                res += line;
                line = br.readLine();
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private static void printFromTo(int first, int second) {
        for (int i = first ; i <= second ; i++){
            System.out.print(i + " ");
        }
    }
}
