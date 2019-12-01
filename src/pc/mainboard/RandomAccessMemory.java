package pc.mainboard;

import global.StackOverFlowExceptionAW;
import org.jetbrains.annotations.NotNull;
import os.ProcessAW;
import pc.mainboard.cpu.Register;

public class RandomAccessMemory {
	public final ProcessAW[] memory = new ProcessAW[10];//메모리의 기본 용량은 10

	public void fetchInstruction() {
		Register.MBR.data = memory[Register.SP.data].code[Register.MAR.data];
	}

	public synchronized void fetchData() throws StackOverFlowExceptionAW {
		ProcessAW temp = memory[Register.SP.data];
		if (Register.MAR.data < temp.data.length) {
			Register.MBR.data = temp.data[Register.MAR.data];
		} else if (Register.MAR.data < temp.stack.length + temp.data.length) {
//			Register.MBR.data = temp.stack[Register.MAR.data - temp.data.length];
		} else {
			if (Register.MAR.data > temp.stack.length + temp.data.length) throw new StackOverFlowExceptionAW();
//			Register.MBR.data = temp.heap[Register.MAR.data - temp.stack.length - temp.data.length];
		}
	}

	public synchronized void storeData() {
		ProcessAW temp = memory[Register.SP.data];
		if (Register.MAR.data < temp.data.length) {
			temp.data[Register.MAR.data] = Register.MBR.data;
		} else if (Register.MAR.data < temp.stack.length + temp.data.length) {
//			temp.stack[Register.MAR.data - temp.data.length] = Register.MBR.data;
		} else {
//			temp.heap[Register.MAR.data - temp.stack.length - temp.data.length] = Register.MBR.data;
		}
	}
}
