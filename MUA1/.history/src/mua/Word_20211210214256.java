package src.mua;

import java.util.HashMap;

// import java.util.ArrayList;
// import java.util.HashMap;

/*
Type = 0 : Word
Type = 1 : Number
Type = 2 : Boolean
Type = 3 : List
Type = 4 : NULL
Type = 5 : Function
*/

public class Word {
    private String value;
    private int Type;
    private HashMap<String, Word> MakeScopeFunc = new HashMap<String, Word>();
    //private ArrayList<Word> list = new ArrayList<>();
    //函数的参数表
    //public ArrayList<String> ArgList = new ArrayList<>();
    //函数的本地变量表
    //public HashMap<String, Word> LoaclVar = new HashMap<>();
    //函数的操作表
    //private String OpString;
    //构造函数暂空
    public Word() {

    }
    public void assign(String value, int Type) {
        this.value = value;
        this.Type = Type;
        //OpString = "";
    }
    // public void addArg(String arg) {
    //     ArgList.add(arg);
    // }
    // public void addOpString(String op) {
    //     OpString += op + " ";
    // }

    public String getString() {
        return value;
    }
    public float getNumber() {
        return Float.parseFloat(value);
    }
    public Boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }
    // public String getOpString() {
    //     return OpString;
    // }
    public void print() {
        System.out.println(value);
    }
    public int getType() {
        return Type;
    }
    // public HashMap<String, Word> getMakeScopeFunc() {
    //     HashMap<String, Word> TmpScopeFunc = new HashMap<String, Word>(MakeScopeFunc);
    //     return TmpScopeFunc;
    // }
    // public void setMakeScopeFunc(HashMap<String, Word> MakeScopeFunc) {
    //     HashMap<String, Word> TmpScopeFunc = new HashMap<String, Word>(MakeScopeFunc);
    //     this.MakeScopeFunc = TmpScopeFunc;
    // }
}