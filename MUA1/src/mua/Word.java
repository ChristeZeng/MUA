package src.mua;

public class Word {
    private String value;
    private int Type;

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

    /*运算符*/
    public Word add(Word oparetor) {
        Word res = new Word();
        res.value = Float.toString(oparetor.getNumber() + getNumber());
        return res;
    }
    
    public Word mul(Word oparetor) {
        Word res = new Word();
        res.value = Float.toString(oparetor.getNumber() * getNumber());
        return res;
    }

    public Word div(Word oparetor) {
        Word res = new Word();
        res.value = Float.toString(getNumber() / oparetor.getNumber());
        return res;
    }

    public Word mod(Word oparetor) {
        Word res = new Word();
        res.value = Float.toString(getNumber() % oparetor.getNumber());
        return res;
    }
}