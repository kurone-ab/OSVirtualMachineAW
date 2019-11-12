package pc.mainboard;

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
            Scanner scanner = new Scanner(new File("exe/test.exw"));
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine())
                builder.append(scanner.nextLine()).append("\r\n");
            disk.saveFile(builder.toString());
            disk.saveFile(builder.toString());
            scanner = new Scanner(new File("exe/test2.exw"));
            builder = new StringBuilder();
            while (scanner.hasNextLine())
                builder.append(scanner.nextLine()).append("\r\n");
            disk.saveFile(builder.toString());
            Loader.load(0);
			Loader.load(1);
			Loader.load(2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
