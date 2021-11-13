package src.mua;

import java.util.ArrayList;

public class Word {
    private String value;
    private int Type;
    private ArrayList<Word> list = new ArrayList<>();

    //构造函数暂空
    public Word() {

    }
    public void assign(String value, int Type) {
        this.value = value;
        this.Type = Type;
    }
    public void add(Word word) {
        list.add(word);
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
    public ArrayList<String> getList() {
        ArrayList<String> ret = new ArrayList<>();
        for (Word word : list) {
            ret.add(word.getString());
        }
        return ret;
    }
    
    public void print() {
        if(Type == 3)
            printList();
        else
            System.out.println(value);
    }
    public int getType() {
        return Type;
    }
    public void printList() {
        for (Word w : list) {
            w.print();
        }
    }
}