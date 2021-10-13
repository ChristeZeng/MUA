package src.mua;

public class Main {

    public static void main(String[] args)  {
        // Interpreter MuaInterpreter = new Interpreter();
        // MuaInterpreter.Run();
        // Word t = new Word();
        // Word a = new Word();

        // t.assign("123");
        // a.assign("2");
        // Word c = t.mod(a);
        // System.out.println(c.getNumber());
        Interpreter i = new Interpreter();
        i.Execute();
    }
}
