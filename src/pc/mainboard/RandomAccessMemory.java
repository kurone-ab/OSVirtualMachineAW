package pc.mainboard;

import os.ProcessAW;

import static pc.mainboard.cpu.Register.*;

public class RandomAccessMemory {
	public final ProcessAW[] memory = new ProcessAW[10];//메모리의 기본 용량은 10

	public void instructionFetch() {
		MBR.data = memory[SP.data].code[MAR.data];
	}

	void data_segment_fetch() {
		synchronized (memory) {
			ProcessAW processAW = memory[SP.data];
			MBR.data = processAW.data[MAR.data];
		}
	}

	void stack_segment_fetch() {
		synchronized (memory) {
			ProcessAW processAW = memory[SP.data];
			MBR.data = processAW.stack[CSR.data].local[MAR.data];
		}
	}

	void heap_segment_fetch() {
		synchronized (memory) {
			ProcessAW processAW = memory[SP.data];
			MBR.data = processAW.heap.get(HSR.data).instance_variables[MAR.data];
		}
	}


	void data_segment_store() {
		synchronized (memory) {
			ProcessAW processAW = memory[SP.data];
			processAW.data[MAR.data] = MBR.data;
		}
	}


	void stack_segment_store() {
		synchronized (memory) {
			ProcessAW processAW = memory[SP.data];
			processAW.stack[CSR.data].local[MAR.data] = MBR.data;
		}
	}


	void heap_segment_store() {
		synchronized (memory) {
			ProcessAW processAW = memory[SP.data];
			processAW.heap.get(HSR.data).instance_variables[MAR.data] = MBR.data;
		}
	}

	int data_segment_fetch(int sp, int address) {
		synchronized (memory) {
			ProcessAW processAW = memory[sp];
			return processAW.data[address];
		}
	}

	int stack_segment_fetch(int sp, int csr, int address) {
		synchronized (memory) {
			ProcessAW processAW = memory[sp];
			return processAW.stack[csr].local[address];
		}
	}

	int heap_segment_fetch(int sp, int hsr, int address) {
		synchronized (memory) {
			ProcessAW processAW = memory[sp];
			return processAW.heap.get(hsr).instance_variables[address];
		}
	}


	void data_segment_store(int sp, int data, int address) {
		synchronized (memory) {
			ProcessAW processAW = memory[sp];
			processAW.data[address] = data;
		}
	}


	void stack_segment_store(int sp, int data, int address, int csr) {
		synchronized (memory) {
			ProcessAW processAW = memory[sp];
			processAW.stack[csr].local[address] = data;
		}
	}


	void heap_segment_store(int sp, int data, int address, int hsr) {
		synchronized (memory) {
			ProcessAW processAW = memory[sp];
			processAW.heap.get(hsr).instance_variables[address] = data;
		}
	}
}
