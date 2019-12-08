package pc.mainboard;

import os.OperatingSystem;
import pc.PersistentStorage;
import pc.io.ConsoleAW;
import pc.io.NetworkAW;
import pc.mainboard.cpu.CentralProcessingUnit;

public class MainBoard {
    public static RandomAccessMemory ram;
    public static MemoryManagementUnit mmu;
    public static PersistentStorage disk;
    public static CentralProcessingUnit cpu;
    public static ReadOnlyMemory rom;
    public static ConsoleAW consoleAW;
    public static NetworkAW networkAW;

    public void on() {
        ram = new RandomAccessMemory();
        mmu = new MemoryManagementUnit();
        mmu.connect(ram);
        disk = new PersistentStorage();
        cpu = CentralProcessingUnit.getInstance();
        rom = new ReadOnlyMemory();
        consoleAW = new ConsoleAW();
        networkAW = new NetworkAW();
        rom.on();
    }
}
