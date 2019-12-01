package os;


import pc.mainboard.MainBoard;

public class Loader {

	public synchronized static void load(int index){
		System.out.println("process load");
		CompilerAW.prepareParsing(MainBoard.disk.getFile(index));
		CompilerAW.parse();

		ProcessAW processAW = new ProcessAW();
		processAW.data = CompilerAW.parseData();
		processAW.code = CompilerAW.parseCode();
		processAW.stack = new int[CompilerAW.stackSize()];
		processAW.heap = new int[10];

		OperatingSystem.memoryManagerAW.load(processAW);
	}
}
