package src.mua;

import java.util.*;
import java.util.regex.*;


public class Interpreter {
    private String cmd; 
    private Scanner scanOu = new Scanner(System.in);
    private Scanner scanIn;
    private HashMap<String, Word> DMem = new HashMap<>();
    private List<String> cmdList = new ArrayList<>();
    private int ArgsNumber = 0;

    public void getLine(String cmdLine) {
        //clear the cmdList
        cmdList.clear();
        //spilt the cmdLine using space
        String[] cmds = cmdLine.split(" ");
        for (String cmd : cmds) {
            cmdList.add(cmd);
        }
        //set the ArgsNumber
        ArgsNumber = 0;
    }

    public Word getInput() {
        cmd = "";
        cmd = cmdList.get(ArgsNumber);
        ArgsNumber++;
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
        else if(cmd.charAt(0) == '"') {
            //返回一个字符串
            Word tmp =  new Word();
            tmp.assign(cmd.substring(1), 0);
            return tmp;
        }
        //如果是数字
        else if(IsNumber(cmd)) {
            Word tmp = new Word();
            tmp.assign(cmd, 1);
            return tmp;
        }
        //如果是列表
        else if(cmd.charAt(0) == '[') {
            Word tmp = new Word();
            tmp.assign("", 3);
            while(true) {
                Word next;
                String nextStr = cmdList.get(ArgsNumber++);
                if(nextStr.charAt(0) == '[') {
                    next = parse(nextStr);
                }
                else if(nextStr.charAt(0) == ']') {
                    return tmp;
                }
                else {     
                    if(IsNumber(nextStr)) {
                        next = new Word();
                        next.assign(nextStr, 1);
                    }
                    else {
                        next = new Word();
                        next.assign(nextStr, 0);
                    }
                }
                tmp.add(next);
            }
        }
        else if(cmd.equals("read")) {
            Scanner ReadScan = scanIn;
            scanIn = new Scanner(scanOu.nextLine());
            Word tmp = new Word();
            tmp.assign(scanIn.nextLine(), 0);
            scanIn = ReadScan;
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
            Float res = getInput().getNumber() + getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("sub")) {
            Float res = getInput().getNumber() - getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("mul")) {
            Float res = getInput().getNumber() * getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("div")) {
            Float res = getInput().getNumber() / getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("mod")) {
            Float res = getInput().getNumber() % getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("if")) {
            if(getInput().getBoolean()) {
                Word tmp1 = getInput(); //获取列表1
                cmdList = getInput().getList();
                ArgsNumber = 0;
                Word tmp = getInput();
                return tmp;
            }
            else
                return getInput();
        }
        else if(cmd.equals("eq")) {
            Word tmp = new Word();
            if(Compare(getInput(), getInput()) == 0) {
                tmp.assign("true", 2);
            }
            else {
                tmp.assign("false", 2);
            }
            return tmp;
        }
        else if(cmd.equals("gt")) {
            Word tmp = new Word();
            if(Compare(getInput(), getInput()) == 1) {
                tmp.assign("true", 2);
            }
            else {
                tmp.assign("false", 2);
            }
            return tmp;
        }
        else if(cmd.equals("lt")) {
            Word tmp = new Word();
            if(Compare(getInput(), getInput()) == -1) {
                tmp.assign("true", 2);
            }
            else {
                tmp.assign("false", 2);
            }
            return tmp;
        }
        else if(cmd.equals("and")) {
            Word tmp = new Word();
            if(getInput().getString().equals("true") && getInput().getString().equals("true")) {
                tmp.assign("true", 2);
            }
            else {
                tmp.assign("false", 2);
            }
            return tmp;
        }
        else if(cmd.equals("or")) {
            Word tmp = new Word();
            if(getInput().getString().equals("true") || getInput().getString().equals("true")) {
                tmp.assign("true", 2);
            }
            else {
                tmp.assign("false", 2);
            }
            return tmp;
        }
        else if(cmd.equals("not")) {
            Word tmp = new Word();
            if(getInput().getString().equals("true")) {
                tmp.assign("false", 2);
            }
            else {
                tmp.assign("true", 2);
            }
            return tmp;
        }
        else if(cmd.equals("print")) {
            Word tmp = getInput();
            tmp.print();
            return tmp;
        }
        else if(cmd.equals("make")) {
            String name = getInput().getString();
            //System.out.println(name);
            Word tmp = getInput();
            DMem.put(name, tmp);
            //System.out.println(DMem.get(name).getString());
            return tmp;
        }
        else if(cmd.equals("erase")) {
            Word tmp = getInput();
            DMem.remove(tmp.getString());
            return tmp;
        }
        else if(cmd.equals("isname")) {
            String name = getInput().getString();
            Word tmp = new Word();
            tmp.assign(DMem.containsKey(name) ? "true" : "false", 2);
            return tmp;
        }
        else if(cmd.equals("isword")) {
            int Type = getInput().getType();
            Word tmp = new Word();
            tmp.assign(Type == 0 ? "true" : "false", 2);
            return tmp;
        }
        else if(cmd.equals("isnumber")) {
            int Type = getInput().getType();
            Word tmp = new Word();
            tmp.assign(Type == 1 ? "true" : "false", 2);
            return tmp;
        }
        else if(cmd.equals("islist")) {
            int Type = getInput().getType();
            Word tmp = new Word();
            tmp.assign(Type == 3 ? "true" : "false", 2);
            return tmp;
        }
        else if(cmd.equals("isboolean")) {
            int Type = getInput().getType();
            Word tmp = new Word();
            tmp.assign(Type == 2 ? "true" : "false", 2);
            return tmp;
        }
        else if(cmd.equals("isempty")) {
            int Type = getInput().getType();
            Word tmp = new Word();
            tmp.assign(Type == 4 ? "true" : "false", 2);
            return tmp;
        }
        else if(cmd.equals("run")) {
            cmdList = getInput().getList();
            ArgsNumber = 0;
            Word tmp = getInput();
            // Word ret = new Word();
            // for(Word w : tmp.getList()) {
            //     ret = parse(w.getString());
            // }
            return tmp;
        }
        else {
            return null;
        }
    }

    public int Compare(Word a, Word b) {
        if(a.getType() == 0 && b.getType() == 0) {
            return a.getString().compareTo(b.getString());
        }
        else if(a.getType() == 1 && b.getType() == 1) {
            return Float.compare(a.getNumber(), b.getNumber());
        }
        else
            return 0;
    }
    public void Execute() {
        while(scanOu.hasNextLine()) {
            getLine(scanOu.nextLine());
            //scanIn = new Scanner(scanOu.nextLine());
            getInput();
        }
    }

    public Boolean IsNumber(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
} 