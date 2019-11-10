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
        while (!var.equals(code)) {
            temp = scanner.next();
            dataV.add(temp.matches(".?") ? 0 : Integer.parseInt(temp));
            variables.put(var, address);
            address++;
            System.out.println(var + " " + temp);
            var = scanner.next();
        }
        scanner.nextLine();
        while (scanner.hasNext()) {
            String[] codes = scanner.nextLine().split(" ");
            System.out.println(Arrays.toString(codes));
            int byteCode = decode(codes[0]);
            byteCode = byteCode << 16;
            try {
                temp = codes[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                temp = "0";
            }
            try {
                byteCode += variables.getOrDefault(temp, Integer.parseInt(temp));
            } catch (NumberFormatException e) {
                variables.put(temp, address);
                address++;
            }
            codeV.add(byteCode);
        }
        if (size == 0) size = 10;//stack size is 10 if user doesn't determined stack size.
    }

    private static int decode(String instruction) {
        for (CentralProcessingUnit.Instruction inst : CentralProcessingUnit.Instruction.values()) {
            if (inst.name().equals(instruction))
                return inst.ordinal();
        }
        throw new IllegalStateException(instruction);
    }

    public static int stackSize() {
        return size;
    }

    public static int[] parseCode() {
        return new int[1];
    }

    public static int[] parseData() {
        while (scanner.nextLine().equals(data)) {
        }
        while (scanner.hasNextLine()) {
            variables.put(scanner.next(), scanner.nextInt());
        }

        return new int[1];
    }

}
