package pc.mainboard;

import global.DuplicateVariableException;
import global.IllegalFormatException;
import global.IllegalInstructionException;
import os.CompilerAW;
import os.Loader;
import os.OperatingSystem;
import pc.PersistenceStorage;
import pc.io.KeyBoard;
import pc.io.Mouse;
import pc.mainboard.cpu.CentralProcessingUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainBoard {
    public static RandomAccessMemory ram;
    public static PersistenceStorage disk;
    public static Mouse mouse;
    public static KeyBoard keyBoard;
    public static CentralProcessingUnit cpu;

    public void on() {
        ram = new RandomAccessMemory();
        disk = new PersistenceStorage();
        mouse = new Mouse();
        keyBoard = new KeyBoard();
        cpu = CentralProcessingUnit.getInstance();
        OperatingSystem os = new OperatingSystem();
        os.on();

        try {
            Scanner scanner;
            StringBuilder builder;
            scanner = new Scanner(new File("exe/test2.awx"));
            builder = new StringBuilder();
            while (scanner.hasNextLine())
                builder.append(scanner.nextLine()).append("\r\n");
            CompilerAW compilerAW = new CompilerAW(builder.toString());
            compilerAW.initialize("test2");
            compilerAW.parse();
        } catch (FileNotFoundException | DuplicateVariableException | IllegalFormatException | IllegalInstructionException e) {
            e.printStackTrace();
        }
    }
}
