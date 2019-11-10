package pc;

import global.DoubleCircularLinkedList;
import global.ParserAW;
import pc.mainboard.MainBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PowerAW {
    public static void main(String[] args) {
//		MainBoard mainBoard = new MainBoard();
//		mainBoard.on();
        StringBuilder builder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File("exe/test.exw"));
            while (scanner.hasNextLine())
                builder.append(scanner.nextLine()).append("\r\n");
			ParserAW.prepareParsing(builder.toString());
			ParserAW.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
