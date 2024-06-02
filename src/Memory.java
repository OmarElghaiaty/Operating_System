public class Memory {
    private static Memory instance;
    private Object[] memory;

    private Memory() {
        memory = new Object[40];
    }

    public static Memory getInstance() {
        if (instance == null) {
            instance = new Memory();
        }
        return instance;
    }

    public Object[] getMemory() {
        return memory;
    }
}
