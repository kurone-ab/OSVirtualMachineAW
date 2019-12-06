package pc.mainboard;

import global.IllegalFileFormatException;
import os.Loader;
import os.OperatingSystem;
import pc.PersistentStorage;
import pc.mainboard.cpu.CentralProcessingUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainBoard {
    public static RandomAccessMemory ram;
    public static MemoryManagementUnit mmu;
    public static PersistentStorage disk;
    public static CentralProcessingUnit cpu;

    public void on() {
        ram = new RandomAccessMemory();
        mmu = new MemoryManagementUnit();
        mmu.connect(ram);
        disk = new PersistentStorage();
        cpu = CentralProcessingUnit.getInstance();
        OperatingSystem os = new OperatingSystem();
        os.on();
    }
}
