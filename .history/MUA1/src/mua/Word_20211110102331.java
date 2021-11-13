package src.mua;

import java.util.ArrayList;

public class Word {
    private String value;
    private int Type;
    private ArrayList<Word> List = new ArrayList<>();
    
    //构造函数暂空
    public Word() {

    }
    public void assign(String value, int Type) {
        this.value = value;
        this.Type = Type;
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
    public int getType() {
        return Type;
    }
}