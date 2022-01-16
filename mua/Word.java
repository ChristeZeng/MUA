package mua;

import java.util.ArrayList;

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
    public ArrayList<Integer> ScopeIndex = new ArrayList<>();
    
    public Word() {
        ScopeIndex.add(0);
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
