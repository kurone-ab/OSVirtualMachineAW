package global;

import pc.mainboard.cpu.CentralProcessingUnit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class ParserAW {
    private static final String stack = ".STACK", data = ".DATA", code = ".CODE", annotation = "/--";
    private static String sentence;
    private static Scanner scanner;
    private static HashMap<String, Integer> variables;
    private static Vector<Integer> codeV, dataV;
    private static int size, address;

    public static void prepareParsing(String sentence) {
        ParserAW.sentence = sentence;
        scanner = new Scanner(sentence);
        variables = new HashMap<>();
        codeV = new Vector<>();
        dataV = new Vector<>();
    }

    public static void parse() {
        String temp;
        String var = scanner.next();
        while (!var.equals(data)) {
            if (var.equals(stack)) size = scanner.nextInt();
            var = scanner.nextLine();
        }
        if (size == 0) size = 10;
        var = scanner.next();
        while (!var.equals(code)) {//parsing data area
            temp = scanner.next();
            if (!var.matches("^[a-zA-Z][0-9|a-zA-Z]*")) {//variable name check with regex
                var = scanner.next();//variable names cannot begin with numbers.
                continue;
            }
            dataV.add(temp.matches(".?") ? 0 : Integer.parseInt(temp));
            variables.put(var, address++);
            var = scanner.next();
        }
        scanner.nextLine();
        while (scanner.hasNext()) {//parsing code area
            String[] codes = scanner.nextLine().split(" ");
            System.out.println(Arrays.toString(codes));
            int byteCode = decode(codes[0]);
            byteCode = byteCode << 16;
            if (codes.length==1) temp = "0";
            else temp = codes[1];
            if (temp.matches("^[a-zA-Z][0-9|a-zA-Z]*")) {//variable name check with regex
                variables.putIfAbsent(temp, address++);
                byteCode += variables.get(temp);
            }else if (temp.matches("[0-9]+"))
                byteCode += Integer.parseInt(temp);
            else continue;
            codeV.add(byteCode);
        }
        if (size == 0) size = 10;//stack size is 10 if user doesn't determined stack size.
    }

    private static int decode(String instruction) {
        for (CentralProcessingUnit.Instruction inst : CentralProcessingUnit.Instruction.values()) {
            if (inst.name().equals(instruction))
                return inst.ordinal();
        }
        throw new IllegalStateException();
    }

    public static int stackSize() {
        return size;
    }

    public static int[] parseCode() {
        int i = 0;
        int[] array = new int[codeV.size()];
        for (int a:codeV) array[i++] = a;
        return array;
    }

    public static int[] parseData() {
        int i = 0;
        int[] array = new int[dataV.size()];
        for (int a:dataV) array[i++] = a;
        return array;
    }

}
