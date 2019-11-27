package os;


import pc.mainboard.MainBoard;

public class Loader {

	public synchronized static void load(int index){
		System.out.println("process load");
		ParserAW.prepareParsing(MainBoard.disk.getFile(index));
		ParserAW.parse();

		ProcessAW processAW = new ProcessAW();
		processAW.data = ParserAW.parseData();
		processAW.code = ParserAW.parseCode();
		processAW.stack = new int[ParserAW.stackSize()];
		processAW.heap = new int[10];

		OperatingSystem.memoryManagerAW.load(processAW);
	}
}
