package src.mua;

import java.util.*;

public class Interpreter {
    private String cmd; 
    private Scanner scan = new Scanner(System.in);
    private HashMap<String, Word> DMem = new HashMap<>();

    public Word getInput() {
        cmd = "";
        cmd = scan.next();
        if(cmd != null) {
            return parse(cmd);
        }
        else {
            return null;
        }
    }

    public Word parse(String cmd) {
        
        if(cmd == null)
            return null;

        //如果是字面量
        if(cmd.charAt(0) == '"') {
            //返回一个字符串
            Word tmp =  new Word();
            tmp.assign(cmd.substring(1), 0);
            return tmp;
        }
        else if(cmd.equals("read")) {
            Scanner ReadScan = new Scanner(scan.nextLine());
            Word tmp = new Word();
            tmp.assign(scan.nextLine(), 0);
            scan = ReadScan;
            return tmp;
        }
        else if(cmd.equals("thing")) {
            String name = getInput().getString();
            return DMem.get(name);
        }
        else if(cmd.charAt(0) == ':') {
            String name = cmd.substring(1);
            return DMem.get(name);
        }
        else if(cmd.equals("add")) {
            Float res = DMem.get(getInput().getString()).getNumber() + DMem.get(getInput().getString()).getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("sub")) {
            Float res = DMem.get(getInput().getString()).getNumber() - DMem.get(getInput().getString()).getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("mul")) {
            Float res = DMem.get(getInput().getString()).getNumber() * DMem.get(getInput().getString()).getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("div")) {
            Float res = DMem.get(getInput().getString()).getNumber() / DMem.get(getInput().getString()).getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("mod")) {
            Float res = DMem.get(getInput().getString()).getNumber() % DMem.get(getInput().getString()).getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("print")) {
            Word tmp = getInput();
            tmp.print();
            return tmp;
        }
        else if(cmd.equals("make")) {
            String name = getInput().getString().substring(1);
            Word tmp = getInput();
            DMem.put(name, tmp);
            return tmp;
        }
        else {
            return null;
        }
    }

    public void Execute() {
        
    }
} 