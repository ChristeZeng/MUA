package src.mua;

import java.util.*;
import java.util.regex.*;


public class Interpreter {
    private String cmd; 
    private Scanner scanOu = new Scanner(System.in);
    private Scanner scanIn;
    private ArrayList<String> cmdList = new ArrayList<>();
    //函数的作用域
    private ArrayList<HashMap<String, Word> > ScopeFunc = new ArrayList<>();
    private int ArgsNumber = 0;

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

    public ArrayList<String> SplitLineBySpace(String cmdLine) {
        ArrayList<String> ret = new ArrayList<>();
        //add space to the cmdLine behind the '['
        cmdLine = cmdLine.replaceAll("\\[", " [ ");
        //add space to the cmdLine before the ']'
        cmdLine = cmdLine.replaceAll("\\]", " ] ");
        //spilt the cmdLine by space and tab
        String[] cmds = cmdLine.split("\\s+");
        //add the cmds to the cmdList
        for (String cmd : cmds) {
            ret.add(cmd);
        }
        //delete the NULL cmdList
        if(ret.size() == 1 && ret.get(0).equals("")) {
            return null;
        }
        //delete the "" from cmdList
        if(ret.get(0).equals("")) {
            ret.remove(0);
        }
        return ret;
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
            Word tmp = new Word();
            if(ArgsNumber < cmdList.size()) {
                tmp = getInput();
            }
            else {
                String cmdNextLine = scanOu.nextLine();
                System.out.println(cmdNextLine);
                getLine(cmdNextLine);
                tmp = getInput();
            }
            //Word tmp = getInput();
            ScopeFunc.get(ScopeFunc.size() - 1).put(name, tmp);
            return tmp;
        }
        else if(cmd.equals("erase")) {
            Word tmp = getInput();
            ScopeFunc.get(ScopeFunc.size() - 1).remove(tmp.getString());
            return tmp;
        }
        else if(cmd.equals("isname")) {
            String name = getInput().getString();
            Word tmp = new Word();
            tmp.assign(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name) ? "true" : "false", 2);
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
        else if(cmd.equals("run")) {
            ArrayList<String> TmpcmdList = new ArrayList<>();
            //获取以String类型存储的操作指令
            String runList = getInput().getString();
            //!ArgsNumber可能会有问题
            int TempArgsNumber = ArgsNumber;
            //转存当前命令以执行命令切换
            DeepCopy(cmdList, TmpcmdList);
            //重设命令和命令索引
            getLine(runList);
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
            //执行return后的第一条命令，直接将命令索引置到最后，实现从命令中返回
            Word tmp = getInput();
            ArgsNumber = cmdList.size();
            return tmp;
        }
        else if(cmd.equals("export")) {
            Word tmp = getInput();
            if(ScopeFunc.get(0).containsKey(tmp.getString())) {
                ScopeFunc.get(0).replace(tmp.getString(), ScopeFunc.get(ScopeFunc.size() - 1).get(tmp.getString()));
            }
            else {
                ScopeFunc.get(0).put(tmp.getString(), ScopeFunc.get(ScopeFunc.size() - 1).get(tmp.getString()));
            }
            return ScopeFunc.get(0).get(tmp.getString());
        }
        else if(IsDefinedOperate(cmd)) {
            Word Func = getDefinedFunc(cmd);
            //从String中还原函数的参数列表和执行命令列表
            ArrayList<String> FuncStringsList = SplitLineBySpace(Func.getString());
            int index = 0;
            //获取参数表
            for(index = 0; index < FuncStringsList.size(); index++) {
                if(FuncStringsList.get(index).equals("["))
                    continue;
                if(FuncStringsList.get(index).equals("]"))
                    break;
                Func.ArgList.add(FuncStringsList.get(index));
            }
            //获取执行命令表
            int LeftBracket = 1;
            int RightBracket = 0;
            if(!FuncStringsList.get(index++).equals("[")) {
                Word tmp = new Word();
                tmp.assign("Error", 4);
                return tmp;
            }
            for(; index < FuncStringsList.size(); index++) {
                if(FuncStringsList.get(index).equals("[")) {
                    LeftBracket++;
                }
                else if(FuncStringsList.get(index).equals("]")) {
                    RightBracket++;
                }
                if(LeftBracket - RightBracket == 0) {
                    break;
                }
                Func.addOpString(FuncStringsList.get(index));
            }
            //参数赋值
            for(int i = 0; i < Func.ArgList.size(); i++) {
                Word argment = getInput();
                Func.LoaclVar.put(Func.ArgList.get(i), argment);
            }
            
            ArrayList<String> TmpcmdList = new ArrayList<String>();
            DeepCopy(cmdList, TmpcmdList);
            int TmpArgsNumber = ArgsNumber;

            getLine(Func.getOpString());
            //delete the first element of cmdList
            cmdList.remove(0);
            //delete the last element of cmdList
            cmdList.remove(cmdList.size() - 1);
            //set the scope
            HashMap<String, Word> NewScope = new HashMap<>();
            NewScope.put(cmd, Func);
            ScopeFunc.add(NewScope);

            Word tmp = new Word();
            while(ArgsNumber < cmdList.size()) {
                tmp = getInput();
            }
            
            //函数执行完毕，清除作用域
            ScopeFunc.remove(ScopeFunc.size() - 1);

            cmdList = RestoreFromArrayList(TmpcmdList);
            ArgsNumber = TmpArgsNumber;
            return tmp;
        }
        else {
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
        //初始化init进程
        ScopeFunc.add(new HashMap<String, Word>());
        while(scanOu.hasNextLine()) {
            getLine(scanOu.nextLine());
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

    //闭包处理时需要修改
    public Boolean IsDefinedOperate(String func) {
        if(ScopeFunc.size() > 1 && ScopeFunc.get(ScopeFunc.size() - 1).containsKey(func)) {
            return true;
        }
        else if(ScopeFunc.get(0).containsKey(func)) {
            return true;
        }
        else {
            return false;
        }
    }

    public Word getDefinedFunc(String func) {
        if(ScopeFunc.size() > 1 && ScopeFunc.get(ScopeFunc.size() - 1).containsKey(func)) {
            return ScopeFunc.get(ScopeFunc.size() - 1).get(func);
        }
        else if(ScopeFunc.get(0).containsKey(func)) {
            return ScopeFunc.get(0).get(func);
        }
        else {
            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;
        }
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