package src.mua;

import java.util.ArrayList;
import java.util.HashMap;

/*
Type = 0 : Word
Type = 1 : Number
Type = 2 : Boolean
Type = 3 : List
Type = 4 : NULL
*/

public class Word {
    private String value;
    private int Type;
    private ArrayList<Word> list = new ArrayList<>();
    public ArrayList<String> ArgList = new ArrayList<>();
    public HashMap<String, Word> LoaclVar = new HashMap<>();

    private String OpString;
    //构造函数暂空
    public Word() {

    }
    public void assign(String value, int Type) {
        this.value = value;
        this.Type = Type;
        OpString = "";
    }
    public void addArg(String arg) {
        ArgList.add(arg);
    }
    public void addOpString(String op) {
        OpString += op;
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
    public String getOpString() {
        return OpString;
    }
    public void print() {
        if(Type == 3)
            printList();
        if(Type == 5) {
            for(String str : ArgList) {
                System.out.print(str + " ");
            }
            System.out.println(OpString);
        }
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