package pc.mainboard;

import os.ProcessAW;

import static pc.mainboard.cpu.Register.*;

public class RandomAccessMemory {
	public final ProcessAW[] memory = new ProcessAW[10];//메모리의 기본 용량은 10

	public void instructionFetch() {
		MBR.data = memory[SP.data].code[MAR.data];
	}

	void data_segment_fetch() {
		ProcessAW processAW = memory[SP.data];
		MBR.data = processAW.data[MAR.data];
	}

	void stack_segment_fetch() {
		ProcessAW processAW = memory[SP.data];
		MBR.data = processAW.stack[ARC.data].local[MAR.data];
	}

	void heap_segment_fetch() {
		ProcessAW processAW = memory[SP.data];
		MBR.data = processAW.heap.get(HSR.data).instance_variables[MAR.data];
	}


	void data_segment_store() {
		ProcessAW processAW = memory[SP.data];
		processAW.data[MAR.data] = MBR.data;
	}


	void stack_segment_store() {
		ProcessAW processAW = memory[SP.data];
		processAW.stack[ARC.data].local[MAR.data] = MBR.data;
	}


	void heap_segment_store() {
		ProcessAW processAW = memory[SP.data];
		processAW.heap.get(HSR.data).instance_variables[MAR.data] = MBR.data;
	}
}
