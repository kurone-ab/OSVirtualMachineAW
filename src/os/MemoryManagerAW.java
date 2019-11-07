package os;

import pc.mainboard.MainBoard;

public class MemoryManagerAW {

	public void load(ProcessAW processAW){
		int index = MainBoard.ram.memory.size();
		MainBoard.ram.memory.add(processAW);
		OperatingSystem.processManagerAW.newProcess(index, processAW);

	}

	public void unload(){

	}
}
