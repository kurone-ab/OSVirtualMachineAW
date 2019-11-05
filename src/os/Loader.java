package os;


import global.ParserAW;
import pc.PersistenceStorage;
import pc.mainboard.MainBoard;
import pc.mainboard.RandomAccessMemory;

import java.util.Vector;

public class Loader {

	public static void load(int index){
		ParserAW.prepareParsing(PersistenceStorage.getFile(index));

		ProcessAW processAW = new ProcessAW();
		processAW.stackSize = ParserAW.stackSize();
		processAW.data = ParserAW.parseData();
		processAW.code = ParserAW.parseCode();
		processAW.stack = new Vector<>();
		processAW.heap = new Vector<>();

		SchedulingQueue.ready.add(MainBoard.ram.load(processAW));
	}
}
