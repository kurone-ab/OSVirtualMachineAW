package pc.mainboard;

import os.CompilerAW;
import os.ProcessAW;
import pc.mainboard.cpu.Register;

public class RandomAccessMemory {
	public final ProcessAW[] memory = new ProcessAW[10];//메모리의 기본 용량은 10

	public void fetchInstruction() {
		Register.MBR.data = memory[Register.SP.data].code[Register.MAR.data];
	}

	public synchronized void fetchData() {
		ProcessAW processAW = memory[Register.SP.data];
		int segment = (Register.MAR.data >>> CompilerAW.segment_bit) & 0x0000000f;
		int correction = (Register.MAR.data >>> CompilerAW.correction_bit) & 0x000000ff;
		int address = Register.MAR.data & 0x00000fff;
		switch (segment) {
			case CompilerAW.dataSegment:
				Register.MBR.data = processAW.data[address];
				break;
			case CompilerAW.stackSegment:
				Register.MBR.data = processAW.stack[Register.ARC.data].local[address];
				break;
			case CompilerAW.heapSegment:
				Register.MBR.data = processAW.heap.get(correction).instance_variables[address];
				break;
			case CompilerAW.constant:
				Register.MBR.data = address;
		}
	}

	public synchronized void storeData() {
		ProcessAW processAW = memory[Register.SP.data];
		int segment = (Register.MAR.data >>> CompilerAW.segment_bit) & 0x0000000f;
		int correction = (Register.MAR.data >>> CompilerAW.correction_bit) & 0x000000ff;
		int address = Register.MAR.data & 0x00000fff;
		switch (segment) {
			case CompilerAW.dataSegment:
				processAW.data[address] = Register.MBR.data;
				break;
			case CompilerAW.stackSegment:
				processAW.stack[Register.ARC.data].local[address] = Register.MBR.data;
				break;
			case CompilerAW.heapSegment:
				processAW.heap.get(correction).instance_variables[address] = Register.MBR.data;
				break;
			default:
				break;
		}
	}
}
