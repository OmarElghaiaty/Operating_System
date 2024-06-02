public class Variable {
    private String name;
    private Object Value;

    public Variable(String name, Object value) {
        this.name = name;
        Value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return Value;
    }

    public void setValue(Object value) {
        Value = value;
    }
}
