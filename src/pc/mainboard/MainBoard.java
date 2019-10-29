package pc.mainboard;

import pc.PersistenceStorage;
import pc.io.KeyBoard;
import pc.io.Mouse;
import pc.mainboard.cpu.CentralProcessingUnit;

import java.util.Random;

public class MainBoard {
	private RandomAccessMemory ram;
	private PersistenceStorage disk;
	private Mouse mouse;
	private KeyBoard keyBoard;
	private CentralProcessingUnit cpu;

	public void on() {
		ram = new RandomAccessMemory();
		disk = new PersistenceStorage();
		mouse = new Mouse();
		keyBoard = new KeyBoard();
		cpu = CentralProcessingUnit.getInstance();
	}
}
