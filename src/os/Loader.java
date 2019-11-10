package os;


import global.ParserAW;
import pc.PersistenceStorage;
import pc.mainboard.MainBoard;
import pc.mainboard.RandomAccessMemory;

import java.util.Vector;

public class Loader {

	public synchronized static void load(int index){
		ParserAW.prepareParsing(MainBoard.disk.getFile(index));

		ProcessAW processAW = new ProcessAW();
		processAW.stackSize = ParserAW.stackSize();
		ParserAW.parse();
		processAW.data = ParserAW.parseData();
		processAW.code = ParserAW.parseCode();
		processAW.stack = new Vector<>();
		processAW.heap = new Vector<>();

		OperatingSystem.memoryManagerAW.load(processAW);
	}
}
