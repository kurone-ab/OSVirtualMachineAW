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

	synchronized void load(ProcessAW processAW) {
		int bound = MainBoard.ram.memory.length;
		for (int i = 0; i < bound; i++) {
			if (MainBoard.ram.memory[i] == null) {
				OperatingSystem.processManagerAW.setProcessID(processAW);
				this.memoryIndexTable.put(processAW.pid, i);
				MainBoard.ram.memory[i] = processAW;
				OperatingSystem.processManagerAW.newProcess(i, processAW);
				return;
			}
		}

	}

	synchronized void unload(int pid) {
		int index = this.memoryIndexTable.get(pid);
		this.memoryIndexTable.remove(pid);
		MainBoard.ram.memory[index] = null;
		System.gc();
	}

	int processAddress(int pid) {
		return this.memoryIndexTable.get(pid);
	}

}
