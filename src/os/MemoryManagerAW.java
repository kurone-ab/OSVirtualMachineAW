package os;

import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.stream.IntStream;

class MemoryManagerAW {
	private Hashtable<Integer, Integer> memoryIndexTable;

	MemoryManagerAW() {
		this.memoryIndexTable = new Hashtable<>();
	}

	synchronized void load(ProcessAW processAW){
		int index = MainBoard.ram.memory.size();
		OperatingSystem.processManagerAW.setProcessID(processAW);
		this.memoryIndexTable.put(processAW.pid, index);
		MainBoard.ram.memory.add(processAW);
		OperatingSystem.processManagerAW.newProcess(index, processAW);
	}

	synchronized int unload(int pid){
		int index = this.memoryIndexTable.get(pid);
		this.memoryIndexTable.remove(pid);
		MainBoard.ram.memory.remove(index);
		reinitialize();
		return index;
	}

	private void reinitialize(){
		IntStream.range(0, MainBoard.ram.memory.size()).forEach(i -> this.memoryIndexTable.replace(MainBoard.ram.memory.get(i).pid, i));
	}

	int processAddress(int pid){
		return this.memoryIndexTable.get(pid);
	}

	int getProcess(int address){
		return MainBoard.ram.memory.get(address).pid;
	}
}
