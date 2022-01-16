package mua;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class Interpreter {
    private String cmd; 
    private Scanner scanOu = new Scanner(System.in);
    private Scanner scanIn;
    private ArrayList<String> cmdList = new ArrayList<>();
    private ArrayList<HashMap<String, Word> > ScopeFunc = new ArrayList<>();
    private int ArgsNumber = 0;
    private ArrayList<Integer> Scope = new ArrayList<>();
    private ArrayList<Integer> LScope = new ArrayList<>();
    private boolean file = false;
    private FileReader fr;
    private BufferedReader br;

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
        else if(IsNumber(cmd)) {
            Word tmp = new Word();
            tmp.assign(cmd, 1);
            return tmp;
        }
        else if(cmd.equals("true") || cmd.equals("false")) {
            Word tmp = new Word();
            tmp.assign(cmd, 2);
            return tmp;
        }
        else if(cmd.charAt(0) == '[') {
            String ListString = "";
            int LeftBracket = 0;
            int RightBracket = 0;
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
                        if((ListString.length() != 0 && ListString.charAt(ListString.length() - 1) == '[') || cmd.equals("]")) {
                            ListString += cmd;
                        } 
                        else {
                            if(ListString.length() != 0) {
                                ListString += " " + cmd;
                            }
                            else {
                                ListString += cmd;
                            }
                        }
                        //System.out.println(ListString);
                    }
                    if(ArgsNumber == cmdList.size() - 1) {
                        String nextLine;
                        if(file) {
                            try {
                                nextLine = br.readLine();
                            } catch (IOException e) {
                                System.out.println("Error: IOException");
                                return null;
                            }
                        }
                        else {
                            nextLine = scanOu.nextLine();
                        }
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
            
            if(ScopeFunc.get(Scope.get(Scope.size() - 1)).containsKey(name)) {
                return ScopeFunc.get(Scope.get(Scope.size() - 1)).get(name);
            }
            else {
                for(int i = LScope.size() - 1; i >= 0; i--) {
                    if(ScopeFunc.get(LScope.get(i)).containsKey(name)) {
                        return ScopeFunc.get(LScope.get(i)).get(name);
                    }
                }
                for(int i = Scope.size() - 1; i >= 0; i--) {
                    if(ScopeFunc.get(Scope.get(i)).containsKey(name)) {
                        return ScopeFunc.get(Scope.get(i)).get(name);
                    }
                }
                if(ScopeFunc.get(0).containsKey(name)) {
                    return ScopeFunc.get(0).get(name);
                }
            }

            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;
        }
        else if(cmd.charAt(0) == ':') {
            String name = cmd.substring(1);
            if(ScopeFunc.get(Scope.get(Scope.size() - 1)).containsKey(name)) {
                return ScopeFunc.get(Scope.get(Scope.size() - 1)).get(name);
            }
            else {
                for(int i = LScope.size() - 1; i >= 0; i--) {
                    if(ScopeFunc.get(LScope.get(i)).containsKey(name)) {
                        return ScopeFunc.get(LScope.get(i)).get(name);
                    }
                }
                for(int i = Scope.size() - 1; i >= 0; i--) {
                    if(ScopeFunc.get(Scope.get(i)).containsKey(name)) {
                        return ScopeFunc.get(Scope.get(i)).get(name);
                    }
                }
                if(ScopeFunc.get(0).containsKey(name)) {
                    return ScopeFunc.get(0).get(name);
                }
            }
            
            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;
        }
        else if(cmd.equals("add")) {
            Float res = getInput().getNumber() + getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("sub")) {
            Float A = getInput().getNumber();
            Float B = getInput().getNumber();
            Float res = A - B;

            //Debug
            //System.out.println(A + " - " + B);
            Word tmp = new Word();
            tmp.assign(Float.toString(res), 1);
            return tmp;
        }
        else if(cmd.equals("mul")) {
            Float A = getInput().getNumber();
            Float B = getInput().getNumber();
            Float res = A * B;

            //Debug
            // System.out.println("/n");
            // System.out.println(A + " * " + B + " = " + res);
            // //print the Scope 
            // String ScopeString = "";
            // for(int i = 0; i < Scope.size(); i++) {
            //     ScopeString += Scope.get(i) + " ";
            // }
            // System.out.println("Scope: " + ScopeString);
            // //print the LScope
            // String LScopeString = "";
            // for(int i = 0; i < LScope.size(); i++) {
            //     LScopeString += LScope.get(i) + " ";
            // }
            // System.out.println("LScope: " + LScopeString);

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
            if(Scope.size() != 0) {
                ScopeFunc.get(Scope.get(Scope.size() - 1)).put(name, tmp);
            }
            else {
                ScopeFunc.get(0).put(name, tmp);
            }
            return tmp;
        }
        else if(cmd.equals("erase")) {
            Word tmp = getInput();
            if(ScopeFunc.size() > 1) {
                ScopeFunc.get(Scope.get(Scope.size() - 1)).remove(tmp.getString());
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
                if(ScopeFunc.get(Scope.get(Scope.size() - 1)).containsKey(name)) {
                    tmp.assign("true", 2);
                }
                else {
                    tmp.assign("false", 2);
                }
            }
            else {
                tmp.assign(ScopeFunc.get(Scope.get(Scope.size() - 1)).containsKey(name) ? "true" : "false", 2);
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
            Word op = getInput();
            Word tmp = new Word();
            if(op.getType() == 3) {
                tmp.assign(op.getString().length() == 0 ? "true" : "false", 2);
            }
            else {
                tmp.assign(op.getType() == 4 ? "true" : "false", 2);
            }
            return tmp;
        }
        else if(cmd.equals("readlist")) {
            Scanner ReadScan = scanIn;
            scanIn = new Scanner(scanOu.nextLine());

            Word tmp = new Word();
            tmp.assign(scanIn.nextLine(), 3);

            tmp.ScopeIndex.clear();
            DeepCopyInt(Scope, tmp.ScopeIndex);

            scanIn = ReadScan;
            return tmp;
        }
        else if(cmd.equals("word")) {
            Word tmp = new Word();
            tmp.assign(getInput().getString() + getInput().getString(), 0);
            return tmp;
        }
        else if(cmd.equals("sentence")) {
            Word tmp = new Word();
            tmp.assign(getInput().getString() + " " + getInput().getString(), 3);
            tmp.ScopeIndex.clear();
            DeepCopyInt(Scope, tmp.ScopeIndex);
            return tmp;
        }
        else if(cmd.equals("list")) {
            Word A = getInput();
            Word B = getInput();
            if(A.getType() == 3) {
                A.assign('[' + A.getString() + ']', 3);
            }
            if(B.getType() == 3) {
                B.assign('[' + B.getString() + ']', 3);
            }
            Word tmp = new Word();
            tmp.assign(A.getString() + " " + B.getString(), 3);
            tmp.ScopeIndex.clear();
            DeepCopyInt(Scope, tmp.ScopeIndex);
            return tmp;
        }
        else if(cmd.equals("join")) {
            Word A = getInput();
            Word B = getInput();
            if(B.getType() == 3) {
                B.assign('[' + B.getString() + ']', 3);
            }
            Word tmp = new Word();
            tmp.assign(A.getString() + " " + B.getString(), 3);
            tmp.ScopeIndex.clear();
            DeepCopyInt(Scope, tmp.ScopeIndex);
            return tmp;
        }
        else if(cmd.equals("first")) {
            Word op = getInput();
            Word tmp = new Word();
            if(op.getType() == 3) {
                if(op.getString().charAt(0) != '[') { 
                    String[] listelement = op.getString().split("\\s+"); 
                    tmp.assign(listelement[0], 0);
                }
                else {
                    String list = op.getString();
                    ArrayList<String> listElement = SplitLineBySpace(list);
                    int leftblank = 0;
                    int rightblank = 0;
                    String tmpString = "";
                    for(int i = 0; i < listElement.size(); i++) {
                        if(listElement.get(i).charAt(0) == '[') {
                            leftblank++;
                        }
                        else if(listElement.get(i).charAt(listElement.get(i).length() - 1) == ']') {
                            rightblank++;
                        }
                        
                        if(tmpString.length() == 0) {
                            tmpString += listElement.get(i);
                        }
                        else {
                            if(tmpString.charAt(tmpString.length() - 1) == '[' || listElement.get(i).equals("]")) {
                                tmpString += listElement.get(i);
                            }
                            else {
                                tmpString += " " + listElement.get(i);
                            }
                        }
                        if(leftblank == rightblank) {
                            break;
                        }
                    }
                    tmpString = tmpString.substring(1, tmpString.length() - 1);
                    tmp.assign(tmpString, 3);
                    tmp.ScopeIndex.clear();
                    DeepCopyInt(Scope, tmp.ScopeIndex);
                }
            }
            else {
                tmp.assign(Character.toString(op.getString().charAt(0)), 0);
            }
            //System.out.println("After first: " + tmp.getString() + " " + tmp.getType());
            return tmp;
        }
        else if(cmd.equals("last")) {
            Word op = getInput();
            Word tmp = new Word();
            if(op.getType() == 3) {
                if(op.getString().charAt(op.getString().length() - 1) != ']') { 
                    String[] listelement = op.getString().split("\\s+"); 
                    tmp.assign(listelement[listelement.length - 1], 0);
                }
                else {
                    String list = op.getString();
                    ArrayList<String> listElement = SplitLineBySpace(list);
                    int leftblank = 0;
                    int rightblank = 0;
                    String tmpString = "";
                    for(int i = listElement.size() - 1; i >= 0; i--) {
                        if(listElement.get(i).charAt(0) == '[') {
                            leftblank++;
                        }
                        else if(listElement.get(i).charAt(listElement.get(i).length() - 1) == ']') {
                            rightblank++;
                        }
                        
                        if(tmpString.length() == 0) {
                            tmpString += listElement.get(i);
                        }
                        else {
                            if(tmpString.charAt(tmpString.length() - 1) == ']' || listElement.get(i).equals("[")) {
                                tmpString += listElement.get(i);
                            }
                            else {
                                tmpString += " " + listElement.get(i);
                            }
                        }
                        if(leftblank == rightblank) {
                            break;
                        }
                    }
                    //reverse the string
                    StringBuilder sb = new StringBuilder(tmpString);
                    sb.reverse();
                    tmp.assign(sb.toString().substring(1, sb.toString().length() -1), 3);
                    tmp.ScopeIndex.clear();
                    DeepCopyInt(Scope, tmp.ScopeIndex);
                }
            }
            else {
                tmp.assign(Character.toString(op.getString().charAt(op.getString().length() - 1)), 0);
            }
            return tmp;
        }
        else if(cmd.equals("butfirst")) {
            Word op = getInput();
            Word tmp = new Word();
            if(op.getType() == 3) {
                if(op.getString().charAt(0) != '[') { 
                    String[] listelement = op.getString().split("\\s+"); 
                    String tmpString = "";
                    for(int i = 1; i < listelement.length; i++) {
                        tmpString += listelement[i] + " ";
                    }
                    tmp.assign(tmpString, 3);
                    tmp.ScopeIndex.clear();
                    DeepCopyInt(Scope, tmp.ScopeIndex);
                }
                else {
                    String list = op.getString();
                    ArrayList<String> listElement = SplitLineBySpace(list);
                    int leftblank = 0;
                    int rightblank = 0;
                    String tmpString = "";
                    for(int i = 0; i < listElement.size(); i++) {
                        if(listElement.get(i).charAt(0) == '[') {
                            leftblank++;
                        }
                        else if(listElement.get(i).charAt(listElement.get(i).length() - 1) == ']') {
                            rightblank++;
                        }
                        
                        if(tmpString.length() == 0) {
                            tmpString += listElement.get(i);
                        }
                        else {
                            if(tmpString.charAt(tmpString.length() - 1) == '[' || listElement.get(i).equals("]")) {
                                tmpString += listElement.get(i);
                            }
                            else {
                                tmpString += " " + listElement.get(i);
                            }
                        }
                        if(leftblank == rightblank) {
                            break;
                        }
                    }
                    //list - tmpString
                    int length = tmpString.length();
                    list = list.substring(1 + length);
                    //list = '[' + list; 
                    tmp.assign(list, 3);
                    tmp.ScopeIndex.clear();
                    DeepCopyInt(Scope, tmp.ScopeIndex);
                }
            }
            else {
                tmp.assign(op.getString().substring(1), 0);
            }
            //System.out.println("After butfast: " + tmp.getString());
            return tmp;
        }
        else if(cmd.equals("butlast")) {
            Word op = getInput();
            Word tmp = new Word();
            if(op.getType() == 3) {
                if(op.getString().charAt(op.getString().length() - 1) != ']') { 
                    String[] listelement = op.getString().split("\\s+"); 
                    String tmpString = "";
                    for(int i = 0; i < listelement.length - 1; i++) {
                        tmpString += listelement[i] + " ";
                    }
                    tmp.assign(tmpString, 3);
                    tmp.ScopeIndex.clear();
                    DeepCopyInt(Scope, tmp.ScopeIndex);
                }
                else {
                    String list = op.getString();
                    //System.out.println(list);
                    ArrayList<String> listElement = SplitLineBySpace(list);
                    int leftblank = 0;
                    int rightblank = 0;
                    String tmpString = "";
                    for(int i = listElement.size() - 1; i >= 0; i--) {
                        if(listElement.get(i).charAt(0) == '[') {
                            leftblank++;
                        }
                        else if(listElement.get(i).charAt(listElement.get(i).length() - 1) == ']') {
                            rightblank++;
                        }
                        
                        if(tmpString.length() == 0) {
                            tmpString += listElement.get(i);
                        }
                        else {
                            if(tmpString.charAt(tmpString.length() - 1) == ']' || listElement.get(i).equals("[")) {
                                tmpString += listElement.get(i);
                            }
                            else {
                                tmpString += " " + listElement.get(i);
                            }
                        }
                        if(leftblank == rightblank) {
                            break;
                        }
                    }
                    //list - tmpString
                    int length = tmpString.length();
                    //System.out.println(tmpString);
                    list = list.substring(0, list.length() - length);
                    tmp.assign(list, 3);
                    tmp.ScopeIndex.clear();
                    DeepCopyInt(Scope, tmp.ScopeIndex);
                }
            }
            else {
                tmp.assign(op.getString().substring(0, op.getString().length() - 1), 0);
            }
            return tmp;
        }
        else if(cmd.equals("save")) {
            String filename = getInput().getString();
            //System.out.println("Save to file: " + filename);
            //Save all the element in ScopeFunc.get(0) to file
            try {
                FileWriter fw = new FileWriter(filename);
                BufferedWriter bw = new BufferedWriter(fw);
                for(String key : ScopeFunc.get(0).keySet()) {
                    int Type = ScopeFunc.get(0).get(key).getType();
                    if(Type == 1 || Type == 2 || Type == 4) {
                        bw.write("make " + '"' + key + " " + ScopeFunc.get(0).get(key).getString() + "\n");
                    }
                    else if(Type == 0) {
                        bw.write("make " + '"' + key + " " + '"' + ScopeFunc.get(0).get(key).getString() + "\n");
                    }
                    else {
                        bw.write("make " + '"' + key + " " + '[' + ScopeFunc.get(0).get(key).getString() + ']' + "\n");
                    }
                }
                bw.close();
            }
            catch(IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            Word tmp = new Word();
            tmp.assign(filename, 0);
            return tmp;
        }
        else if(cmd.equals("load")) {
            String filename = getInput().getString();
            //Load all the element in file to ScopeFunc.get(0)
            file = true;
            try {
                fr = new FileReader(filename);
                br = new BufferedReader(fr);
                String line = "";
                while((line = br.readLine()) != null) {
                    ArrayList<String> TmpcmdList = new ArrayList<>();
                    int TempArgsNumber = ArgsNumber;
                    DeepCopy(cmdList, TmpcmdList);

                    //System.out.println("Load: " + line);
                    getLine(line);
                    ArgsNumber = 0;

                    while(ArgsNumber < cmdList.size()) {
                        getInput();
                    }
            
                    cmdList = RestoreFromArrayList(TmpcmdList);
                    ArgsNumber = TempArgsNumber;
                }
                br.close();
            }
            catch(IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            file = false;
            Word tmp = new Word();
            tmp.assign(filename, 0);
            return tmp;
        }
        else if(cmd.equals("erall")) {
            ScopeFunc.get(Scope.get(Scope.size() - 1)).clear();
            Word tmp = new Word();
            tmp.assign("true", 2);
            return tmp;
        }
        else if(cmd.equals("random")) {
            float number = getInput().getNumber();
            //return a random number between 0 and number
            Word tmp = new Word();
            tmp.assign(String.valueOf(Math.random() * number), 0);
            return tmp;
        }
        else if(cmd.equals("int")) {
            float number = getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(String.valueOf((int)number), 0);
            return tmp;
        }
        else if(cmd.equals("sqrt")) {
            float number = getInput().getNumber();
            Word tmp = new Word();
            tmp.assign(String.valueOf(Math.sqrt(number)), 0);
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
            String runList = getInput().getString();
            int TempArgsNumber = ArgsNumber;
            DeepCopy(cmdList, TmpcmdList);
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
            Word tmp = getInput();
            ArgsNumber = cmdList.size();

            //Debug
            //print the cmdList in one line
            // for(int i = 0; i < cmdList.size(); i++) {
            //     System.out.print(cmdList.get(i) + " ");
            // }
            // tmp.print();
            // if(ScopeFunc.get(Scope.get(Scope.size() - 1)).containsKey("x")) {
            //     ScopeFunc.get(Scope.get(Scope.size() - 1)).get("x").print();
            // }
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

            ArrayList<String> FuncStringsList = SplitLineBySpace(Func.getString());
            
            //set the scope
            HashMap<String, Word> NewScope = new HashMap<>();

            int index = 0;
            ArrayList<String> ArgList = new ArrayList<>();
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

            for(int i = 0; i < ArgList.size(); i++) {
                Word argment = getInput();
                NewScope.put(ArgList.get(i), argment);
                //ScopeFunc.get(Scope.get(Scope.size() - 1)).put(ArgList.get(i), argment);
            }
            
            ArrayList<Integer> TmpScope = new ArrayList<>();
            DeepCopyInt(Scope, TmpScope);
            ScopeFunc.add(NewScope);
            Scope.add(ScopeFunc.size() - 1);

            ArrayList<Integer> tmpLScope = new ArrayList<>();
            DeepCopyInt(LScope, tmpLScope);
            LScope.clear();
            DeepCopyInt(Func.ScopeIndex, LScope);
            LScope.remove(0);

            ArrayList<String> TmpcmdList = new ArrayList<String>();
            DeepCopy(cmdList, TmpcmdList);
            int TmpArgsNumber = ArgsNumber;
            
            getLine(OpString);
            
            Word tmp = new Word();
            while(ArgsNumber < cmdList.size()) {
                tmp = getInput();
            }
            
            //tmp.ScopeIndex.clear();
            //DeepCopyInt(Scope, tmp.ScopeIndex);

            cmdList = RestoreFromArrayList(TmpcmdList);
            Scope = RestoreFromArrayListInt(TmpScope);
            LScope = RestoreFromArrayListInt(tmpLScope);
            ArgsNumber = TmpArgsNumber;
            return tmp;
        }
        else {
            Word tmp = new Word();
            tmp.assign("", 4);
            return tmp;
        }
    }

    //maybe bug
    public int Compare(Word a, Word b) {
        //System.out.println("Compare " + a.getString() + a.getType() + " " + b.getString() + b.getType());
        if(a.getType() == 0 && b.getType() == 0) {
            return a.getString().compareTo(b.getString());
        }
        else if(a.getType() == 1 && b.getType() == 1) {
            return Float.compare(a.getNumber(), b.getNumber());
        }
        else {
            //return 0;
            return a.getString().compareTo(b.getString());
        }
        //return a.getString().compareTo(b.getString());
    }

    public void Execute() {
        ScopeFunc.add(new HashMap<String, Word>());
        Scope.add(0);
        Word pi = new Word();
        pi.assign("3.14159", 1);
        ScopeFunc.get(0).put("pi", pi);
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

    public Boolean IsDefinedOperate(String func) {

        if(ScopeFunc.get(Scope.get(Scope.size() - 1)).containsKey(func)) {
            return true;
        }
        else {
            for(int i = LScope.size() - 1; i >= 0; i--) {
                if(ScopeFunc.get(LScope.get(i)).containsKey(func)) {
                    return true;
                }
            }
            for(int i = Scope.size() - 1; i >= 0; i--) {
                if(ScopeFunc.get(Scope.get(i)).containsKey(func)) {
                    return true;
                }
            }
            if(ScopeFunc.get(0).containsKey(func)) {
                return true;
            }
        }
        return false;
    }

    public Word getDefinedFunc(String func) {

        if(ScopeFunc.get(Scope.get(Scope.size() - 1)).containsKey(func)) {
            return ScopeFunc.get(Scope.get(Scope.size() - 1)).get(func);
        }
        else {
            for(int i = LScope.size() - 1; i >= 0; i--) {
                if(ScopeFunc.get(LScope.get(i)).containsKey(func)) {
                    return ScopeFunc.get(LScope.get(i)).get(func);
                }
            }
            for(int i = Scope.size() - 1; i >= 0; i--) {
                if(ScopeFunc.get(Scope.get(i)).containsKey(func)) {
                    return ScopeFunc.get(Scope.get(i)).get(func);
                }
            }
            if(ScopeFunc.get(0).containsKey(func)) {
                return ScopeFunc.get(0).get(func);
            }
        }
        Word tmp = new Word();
        tmp.assign("", 4);
        return tmp; 
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
