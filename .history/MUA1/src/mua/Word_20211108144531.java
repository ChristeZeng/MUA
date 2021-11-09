package src.mua;

public class Word {
    private String value;
    private enum Type {
        NUMBER, BOOL;
    }

    //构造函数暂空
    public Word() {
        
    }
    public void assign(String value, enum Type) {
        this.value = value;
        this.Type = Type;
        Type = NUMBER;
        
    }
    public String getString() {
        return value;
    }
    public float getNumber() {
        return Float.parseFloat(value);
    }
    public Boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }
    public void print() {
        System.out.println(value);
    }
}