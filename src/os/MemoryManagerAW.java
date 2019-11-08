package os;

import com.sun.tools.javac.Main;
import pc.mainboard.MainBoard;

public class MemoryManagerAW {

	public void load(ProcessAW processAW){
		int index = MainBoard.ram.memory.size();
		MainBoard.ram.memory.add(processAW);
		OperatingSystem.processManagerAW.newProcess(index, processAW);
	}

	public int unload(ProcessAW processAW){
		int index = MainBoard.ram.memory.indexOf(processAW);
		MainBoard.ram.memory.remove(index);
		return index;
	}

	public int processAddress(ProcessAW processAW){
		return MainBoard.ram.memory.indexOf(processAW);
	}

	public ProcessAW getProcess(int address){
		return MainBoard.ram.memory.get(address);
	}
}
