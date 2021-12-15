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
    // 当前作用域
    private ArrayList<Integer> Scope = new ArrayList<>();
    private ArrayList<Integer> LScope = new ArrayList<>();

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
            int LeftBracket = 0;
            int RightBracket = 0;
            //读取此行剩下的全部列表内容
            ArgsNumber--;
            Boolean flag = false;
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
                    if(flag) {
                        ListString += cmd + " ";
                    }
                    if(ArgsNumber == cmdList.size() - 1) {
                        String nextLine = scanOu.nextLine();
                        getLine(nextLine);
                        ArgsNumber--;
                    }
                }
                flag = true;
            }
            ArgsNumber++;
            Word tmp = new Word();
            tmp.assign(ListString, 3);

            tmp.ScopeIndex.clear();
            DeepCopyInt(Scope, tmp.ScopeIndex);

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
            for(int i = Scope.size() - 1; i >= 0; i--) {
                if(ScopeFunc.get(Scope.get(i)).containsKey(name)) {
                    return ScopeFunc.get(Scope.get(i)).get(name);
                }
            }
            
            //如果没有返回NULL
            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;
            /*
            //返回当前作用域中的变量
            if(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name)) {
                return ScopeFunc.get(ScopeFunc.size() - 1).get(name);
            }
            //如果没有返回定义函数的作用域中的参数
            else if(ScopeFunc.size() > 2 && ScopeFunc.get(ScopeFunc.size() - 2).containsKey(name)) {
                return ScopeFunc.get(ScopeFunc.size() - 2).get(name);
            }
            //如果当前作用域中没有，则返回全局变量
            else {
                return ScopeFunc.get(0).get(name);
            }
            */
        }
        else if(cmd.charAt(0) == ':') {
            String name = cmd.substring(1);
            //返回当前作用域中的变量
            if(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name)) {
                return ScopeFunc.get(ScopeFunc.size() - 1).get(name);
            }
            else {
                for(int i = 0; i < LScope.size() - 1; i++) {
                    if(ScopeFunc.get(LScope.get(i)).containsKey(name)) {
                        return ScopeFunc.get(LScope.get(i)).get(name);
                    }
                }
                for(int i = 0; i < Scope.size() - 1; i++) {
                    if(ScopeFunc.get(Scope.get(i)).containsKey(name)) {
                        return ScopeFunc.get(Scope.get(i)).get(name);
                    }
                }
            }
            // for(int i = Scope.size() - 1; i >= 0; i--) {
            //     if(ScopeFunc.get(Scope.get(i)).containsKey(name)) {
            //         return ScopeFunc.get(Scope.get(i)).get(name);
            //     }
            // }
            
            //如果没有返回NULL
            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;

            /*
            //返回当前作用域中的变量
            if(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name)) {
                return ScopeFunc.get(ScopeFunc.size() - 1).get(name);
            }
            //如果没有返回定义函数的作用域中的参数
            // else if(ScopeFunc.size() > 2 && ScopeFunc.get(ScopeFunc.size() - 2).containsKey(name)) {
            //     return ScopeFunc.get(ScopeFunc.size() - 2).get(name);
            // }
            else if(ScopeFunc.size() > 2 && ScopeFunc.get(ScopeFunc.size() - 2).containsKey(name)) {
                return ScopeFunc.get(ScopeFunc.size() - 2).get(name);
            }
            //如果当前作用域中没有，则返回全局变量
            else {
                return ScopeFunc.get(0).get(name);
            }
            */
            
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
                getLine(cmdNextLine);
                tmp = getInput();
            }
            // tmp.ScopeIndex.clear();
            // DeepCopyInt(Scope, tmp.ScopeIndex);
            //如果当前是在某函数中进行Make，应该进行Make到当前作用域中
            /*
            if(ScopeFunc.size() > 1) {
                ScopeFunc.get(ScopeFunc.size() - 1).put(name, tmp);
            }
            else {
                ScopeFunc.get(0).put(name, tmp);
            }
            */
            ScopeFunc.get(Scope.get(Scope.size() - 1)).put(name, tmp);
            return tmp;
        }
        else if(cmd.equals("erase")) {
            Word tmp = getInput();
            if(ScopeFunc.size() > 1) {
                ScopeFunc.get(ScopeFunc.size() - 1).remove(tmp.getString());
            }
            else {
                ScopeFunc.get(0).remove(tmp.getString());
            }
            //ScopeFunc.get(ScopeFunc.size() - 1).remove(tmp.getString());
            return tmp;
        }
        else if(cmd.equals("isname")) {
            String name = getInput().getString();
            Word tmp = new Word();
            if(ScopeFunc.size() > 1) {
                if(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name)) {
                    tmp.assign("true", 2);
                }
                else {
                    tmp.assign("false", 2);
                }
            }
            else {
                tmp.assign(ScopeFunc.get(ScopeFunc.size() - 1).containsKey(name) ? "true" : "false", 2);
            }
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
                
                String IfOpString = getInput().getString();
                int TmpArgsNumber = ArgsNumber;
                
                ArrayList<String> TmpcmdList = new ArrayList<String>();
                DeepCopy(cmdList, TmpcmdList);
                
                getLine(IfOpString);
                
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
                String IfOpString = getInput().getString();
                ArrayList<String> TmpcmdList = new ArrayList<String>();
                DeepCopy(cmdList, TmpcmdList);
                //int TmpArgsNumber = ArgsNumber;
                
                getLine(IfOpString);
                
                Word tmp = new Word();
                while(ArgsNumber < cmdList.size()) {
                    tmp = getInput();
                }

                cmdList = RestoreFromArrayList(TmpcmdList);
                ArgsNumber = cmdList.size();
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

            //Debug
            System.out.println("******runing function: " + cmd);
            System.out.println("函数内容:" + Func.getString());
            System.out.println("函数本身原携带Scope:");
            for(int i = 0; i < Func.ScopeIndex.size(); i++) {
                System.out.println(Func.ScopeIndex.get(i));
            }
            //Debug
            LScope.clear();
            DeepCopyInt(Func.ScopeIndex, LScope);

            //从String中还原函数的参数列表和执行命令列表
            ArrayList<String> FuncStringsList = SplitLineBySpace(Func.getString());
            //Debug
            // for(int i = 0; i < FuncStringsList.size(); i++) {
            //     System.out.println(FuncStringsList.get(i));
            // }
            int index = 0;
            ArrayList<String> ArgList = new ArrayList<>();
            //获取参数表
            for(index = 0; index < FuncStringsList.size(); index++) {
                if(FuncStringsList.get(index).equals("[")) {
                    continue;
                }
                if(FuncStringsList.get(index).equals("]")) {
                    index++;
                    break;
                }
                ArgList.add(FuncStringsList.get(index));
            }
            //打印参数表
            // for(int i = 0; i < Func.ArgList.size(); i++) {
            //     System.out.println(ArgList.get(i));
            // }
            //获取执行命令表
            String OpString = "";
            int LeftBracket = 1;
            int RightBracket = 0;
            if(!FuncStringsList.get(index++).equals("[")) {
                Word tmp = new Word();
                tmp.assign("", 4);
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
                OpString += FuncStringsList.get(index) + " ";
            }
            //System.out.println(OpString);

            //set the scope
            HashMap<String, Word> NewScope = new HashMap<>();

            //参数赋值
            System.out.println("******The arguments are: ");
            for(int i = 0; i < ArgList.size(); i++) {
                Word argment = getInput();
                NewScope.put(ArgList.get(i), argment);
                //Debug
                System.out.println(ArgList.get(i) + " " + argment.getString());
                if(argment.getType() == 3) {
                    argment.ScopeIndex.clear();
                    DeepCopyInt(Scope, argment.ScopeIndex);
                    //argment.ScopeIndex.add(ScopeFunc.size() - 1);
                    System.out.println("******The argument is a function: 所含作用域");
                    for(int j = 0; j < argment.ScopeIndex.size(); j++) {
                        System.out.println(argment.ScopeIndex.get(j));
                    }
                }
            }
            ScopeFunc.add(NewScope);
            
            ArrayList<Integer> TmpScope = new ArrayList<>();
            ArrayList<String> TmpcmdList = new ArrayList<String>();
            DeepCopy(cmdList, TmpcmdList);
            DeepCopyInt(Scope, TmpScope);

            int TmpArgsNumber = ArgsNumber;
            getLine(OpString);
            //print the cmdList
            // for(int i = 0; i < cmdList.size(); i++) {
            //     System.out.println(cmdList.get(i));
            // }
            //嵌套定义的Scope被改变
            Scope.add(ScopeFunc.size() - 1);
            
            // Func.ScopeIndex.add(ScopeFunc.size() - 1);
            // Scope.clear();
            // DeepCopyInt(Func.ScopeIndex, Scope);
            // Scope.clear();
            // DeepCopyInt(Func.ScopeIndex, Scope);

            Word tmp = new Word();
            while(ArgsNumber < cmdList.size()) {
                tmp = getInput();
            }
            
            //函数执行完毕，清除作用域
            //ScopeFunc.remove(ScopeFunc.size() - 1);
            //函数执行完毕，返回闭包
            tmp.ScopeIndex.clear();
            DeepCopyInt(Scope, tmp.ScopeIndex);

            //Debug
            System.out.println("Function Return所携带Scope:");
            for(int i = 0; i < tmp.ScopeIndex.size(); i++) {
                System.out.println(tmp.ScopeIndex.get(i));
            }
            System.out.println("返回值:" + tmp.getString());
            //tmp.ScopeIndex.add(ScopeFunc.size() - 1);
            cmdList = RestoreFromArrayList(TmpcmdList);
            Scope = RestoreFromArrayListInt(TmpScope);
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
        //初始化作用域
        Scope.add(0);
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
        for(int i = 0; i < Scope.size(); i++) {
            if(ScopeFunc.get(Scope.get(i)).containsKey(func)) {
                return true;
            }
        }
        return false;
        // if(ScopeFunc.size() > 1 && ScopeFunc.get(ScopeFunc.size() - 1).containsKey(func)) {
        //     return true;
        // }
        // else if(ScopeFunc.get(0).containsKey(func)) {
        //     return true;
        // }
        // else {
        //     return false;
        // }
    }

    public Word getDefinedFunc(String func) {
        for(int i = 0; i < Scope.size(); i++) {
            if(ScopeFunc.get(Scope.get(i)).containsKey(func)) {
                return ScopeFunc.get(Scope.get(i)).get(func);
            }
        }
        Word tmp = new Word();
        tmp.assign("", 4);
        return tmp; 
        // if(ScopeFunc.size() > 1 && ScopeFunc.get(ScopeFunc.size() - 1).containsKey(func)) {
        //     return ScopeFunc.get(ScopeFunc.size() - 1).get(func);
        // }
        // else if(ScopeFunc.get(0).containsKey(func)) {
        //     return ScopeFunc.get(0).get(func);
        // }
        // else {
        //     Word tmp = new Word();
        //     tmp.assign("", 4);
        //     return tmp;
        // }
    }

    public void DeepCopy(ArrayList<String> src, ArrayList<String> dest) {
        for(String str : src) {
            dest.add(str);
        }
    }

    public void DeepCopyInt(ArrayList<Integer> src, ArrayList<Integer> dest) {
        for(Integer str : src) {
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

    public ArrayList<Integer> RestoreFromArrayListInt(ArrayList<Integer> src) {
        ArrayList<Integer> ret = new ArrayList<>();
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