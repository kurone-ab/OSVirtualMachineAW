package pc.mainboard;

import pc.PersistenceStorage;
import pc.io.KeyBoard;
import pc.io.Mouse;
import pc.mainboard.cpu.CentralProcessingUnit;

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
	}
}
