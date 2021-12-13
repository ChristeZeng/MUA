package src.mua;

import java.util.*;
import java.util.regex.*;


public class Interpreter {
    private String cmd; 
    private Scanner scanOu = new Scanner(System.in);
    private Scanner scanIn;
    private HashMap<String, Word> DMem = new HashMap<>();
    private HashMap<String, Word> OpMem = new HashMap<>();
    private ArrayList<String> cmdList = new ArrayList<>();
    //函数的作用域
    private ArrayList<HashMap<String, Word> > ScopeFunc = new ArrayList<>();
    private int ArgsNumber = 0;
    private int Scope = 0;
    private int CurrentScope = 1;

    public void getLine(String cmdLine) {
        //clear the cmdList
        cmdList.clear();
        //add space to the cmdLine behind the '['
        cmdLine = cmdLine.replaceAll("\\[", " [ ");
        //add space to the cmdLine before the ']'
        cmdLine = cmdLine.replaceAll("\\]", " ] ");
        //spilt the cmdLine by space and tab
        String[] cmds = cmdLine.split("\\s+");
        //add the cmds to the cmdList
        for (String cmd : cmds) {
            cmdList.add(cmd);
        }
        //delete the NULL cmdList
        if(cmdList.size() == 1 && cmdList.get(0).equals("")) {
            return;
        }
        //delete the "" from cmdList
        if(cmdList.get(0).equals("")) {
            cmdList.remove(0);
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
        //return the NULL
        if(cmd.length() == 0) {
            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;
        }
        //return the word
        else if(cmd.charAt(0) == '"') {
            //if it is a string
            if(IsNumber(cmd.substring(1))) {
                Word tmp = new Word();
                tmp.assign(cmd.substring(1), 1);
                return tmp;
            }
            //if it is a number
            else {
                Word tmp =  new Word();
                tmp.assign(cmd.substring(1), 0);
                return tmp;
            }
        }
        //如果是数字
        else if(IsNumber(cmd)) {
            Word tmp = new Word();
            tmp.assign(cmd, 1);
            return tmp;
        }
        //列表存储，内容都当作String存储
        else if(cmd.charAt(0) == '[') {
            String ListString = "";
            int LeftBracket = 1;
            int RightBracket = 0;
            //读取此行剩下的全部列表内容
            for( ;ArgsNumber < cmdList.size(); ArgsNumber++) {
                cmd = cmdList.get(ArgsNumber);
                if(cmd.charAt(0) == '[') {
                    LeftBracket++;
                }
                else if(cmd.charAt(0) == ']') {
                    RightBracket++;
                }
                if(LeftBracket == RightBracket) {
                    break;
                }
                else {
                    ListString += cmd + " ";
                    if(ArgsNumber == cmdList.size() - 1) {
                        String nextLine = scanOu.nextLine();
                        getLine(nextLine);
                        ArgsNumber--;
                    }
                }

            }
            Word tmp = new Word();
            tmp.assign(ListString, 3);
            return tmp;
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
            //返回当前作用域中的变量
            if(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name)) {
                return ScopeFunc.get(ScopeFunc.size() - 1).get(name);
            }
            //如果当前作用域中没有，则返回全局变量
            else {
                return ScopeFunc.get(0).get(name);
            }
        }
        else if(cmd.charAt(0) == ':') {
            String name = cmd.substring(1);
            //返回当前作用域中的变量
            if(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name)) {
                return ScopeFunc.get(ScopeFunc.size() - 1).get(name);
            }
            //如果当前作用域中没有，则返回全局变量
            else {
                return ScopeFunc.get(0).get(name);
            }
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
            if(!getInput().getBoolean()) {
                int LeftBracket = 0;
                int RightBracket = 0;
                for(; ArgsNumber < cmdList.size(); ArgsNumber++) {
                    if(cmdList.get(ArgsNumber).equals("["))
                        LeftBracket++;
                    else if(cmdList.get(ArgsNumber).equals("]")) {
                        RightBracket++;
                    }
                    if(LeftBracket - RightBracket == 0) {
                        ArgsNumber++;
                        break;
                    }
                }
                //Word gabige = getInput(); //获取列表1
                ArrayList<String> ifList = getInput().getList();
                ArrayList<String> TmpcmdList = new ArrayList<String>();
                DeepCopy(cmdList, TmpcmdList);
                
                int TmpArgsNumber = ArgsNumber;
                cmdList = ifList;
                ArgsNumber = 0;
                Word tmp = new Word();
                while(ArgsNumber < cmdList.size()) {
                    tmp = getInput();
                }

                cmdList = RestoreFromArrayList(TmpcmdList);
                ArgsNumber = TmpArgsNumber;
                return tmp;
            }
            else
            {
                ArrayList<String> ifList = getInput().getList();
                ArrayList<String> TmpcmdList = new ArrayList<String>();
                DeepCopy(cmdList, TmpcmdList);
                int TmpArgsNumber = ArgsNumber;
                cmdList = ifList;
                ArgsNumber = 0;
                Word tmp = new Word();
                while(ArgsNumber < cmdList.size()) {
                    tmp = getInput();
                }
                cmdList = RestoreFromArrayList(TmpcmdList);
                ArgsNumber = TmpArgsNumber;
                return tmp;
            }
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
            Word tmp = getInput();
            if(Scope != 0) {
                OpMem.get(IntToString(Scope)).LoaclVar.put(name, tmp);
            }
            else
                DMem.put(name, tmp);
            //System.out.println(DMem.get(name).getString());
            return tmp;
        }
        else if(cmd.equals("erase")) {
            Word tmp = getInput();
            if(Scope != 0 && OpMem.get(IntToString(Scope)).LoaclVar.containsKey(tmp.getString())) {
                return OpMem.get(IntToString(Scope)).LoaclVar.get(tmp.getString());
            }
            else
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
        else if(cmd.equals("isbool")) {
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
            ArrayList<String> TmpcmdList = new ArrayList<>();
            ArrayList<String> runList = getInput().getList();

            DeepCopy(cmdList, TmpcmdList);
            cmdList = runList;
            int TempArgsNumber = ArgsNumber;
            ArgsNumber = 0;
            Word tmp = new Word();
            while(ArgsNumber < cmdList.size()) {
                tmp = getInput();
            }
            
            cmdList = RestoreFromArrayList(TmpcmdList);
            ArgsNumber = TempArgsNumber;
            return tmp;
        }
        else if(cmd.equals("return")) {
            Word tmp = getInput();
            //cmdList = TmpcmdList;
            ArgsNumber = cmdList.size();
            return tmp;
        }
        else if(cmd.equals("export")) {
            Word tmp = getInput();
            if(DMem.containsKey(tmp.getString())) {
                //revise the DMem to tmp
                DMem.replace(tmp.getString(), OpMem.get(IntToString(Scope)).LoaclVar.get(tmp.getString()));
            }
            else
                DMem.put(tmp.getString(), OpMem.get(IntToString(Scope)).LoaclVar.get(tmp.getString()));
            return OpMem.get(IntToString(Scope) ).LoaclVar.get(tmp.getString());
        }
        else if(OpMem.containsKey(cmd)) {
            //System.out.println(OpMem.get(cmd).ArgList.size());
            for(int i = 0; i < OpMem.get(cmd).ArgList.size(); i++) {
                Word argment = getInput();
                //System.out.println(argment.getString());
                OpMem.get(cmd).LoaclVar.put(OpMem.get(cmd).ArgList.get(i), argment);
            }
            //Debug print
            //System.out.println("Debug");
            //System.out.println(OpMem.get(cmd).LoaclVar.get("Hello").getString());
            ArrayList<String> TmpcmdList = new ArrayList<String>();
            DeepCopy(cmdList, TmpcmdList);
            int TmpArgsNumber = ArgsNumber;

            getLine(OpMem.get(cmd).getOpString());
            //delete the first element of cmdList
            cmdList.remove(0);
            //delete the last element of cmdList
            cmdList.remove(cmdList.size() - 1);
            //set the scope
            int TempScope = Scope;
            Scope = CurrentScope++;
            Word tmp = new Word();
            while(ArgsNumber < cmdList.size()) {
                tmp = getInput();
            }
            
            Scope = TempScope;

            cmdList = RestoreFromArrayList(TmpcmdList);
            ArgsNumber = TmpArgsNumber;
            return tmp;
        }
        else {
            //System.out.println("Error: " + cmd + " is not a command");
            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;
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
            if(cmdList.size() == 3 && cmdList.get(2).equals("[") || (cmdList.size() == 2 && cmdList.get(0).equals("make"))) {
                int LeftBracket = 1;
                int RightBracket = 0;
                int Differ = LeftBracket - RightBracket;
                Word OpMy = new Word();
                OpMy.assign(cmdList.get(1).substring(1), 5);
                if(cmdList.size() == 2)
                    scanOu.nextLine();
                String ArgListString = scanOu.nextLine();
                //add space to the cmdLine behind the '['
                ArgListString = ArgListString.replaceAll("\\[", " [ ");
                //add space to the cmdLine before the ']'
                ArgListString = ArgListString.replaceAll("\\]", " ] ");
                //split the ArgListString by space and tab
                String[] ArgList = ArgListString.split("\\s+");
                //System.out.println(ArgList[0] + "1");
                for(String Arg : ArgList) {
                    if(Arg.equals("]") || Arg.equals("[") || Arg.equals("")) {
                        continue;
                    }
                    else {
                        //System.out.println(Arg);
                        OpMy.addArg(Arg);
                    }
                }
                //read multiple lines until the number of '[' and ']' is the same
                while(scanOu.hasNextLine()) {
                    String tmp = scanOu.nextLine();
                    if(tmp.equals("]")) {
                        RightBracket++;
                        Differ = LeftBracket - RightBracket;
                        if(Differ == 0) {
                            break;
                        }
                    }
                    else if(tmp.equals("[")) {
                        LeftBracket++;
                        Differ = LeftBracket - RightBracket;
                    }
                    else {
                        OpMy.addOpString(tmp);
                    }
                }
                OpMem.put(cmdList.get(1).substring(1), OpMy);
                continue;
            }
            if(cmdList.size() == 1 && cmdList.get(0).equals("")) {
                continue;
            }
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

    public void DeepCopy(ArrayList<String> src, ArrayList<String> dest) {
        for(String str : src) {
            dest.add(str);
        }
    }

    public ArrayList<String> RestoreFromArrayList(ArrayList<String> src) {
        ArrayList<String> ret = new ArrayList<>();
        for(int i = 0; i < src.size(); i++) {
            ret.add(src.get(i));
        }
        return ret;
    }

    //convert int to String
    public String IntToString(int num) {
        String str = "";
        while(num != 0) {
            str = (num % 10) + str;
            num = num / 10;
        }
        return str;
    }
} 