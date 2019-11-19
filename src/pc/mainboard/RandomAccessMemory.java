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
			fetchData(temp);
		} else if (Register.MAR.data < temp.stack.size() + temp.data.length) {
			fetchStack(temp);
		} else {
			fetchHeap(temp);
		}
	}

	public synchronized void storeData() {
		ProcessAW temp = memory[Register.SP.data];
		if (Register.MAR.data < temp.data.length) {
			storeData(temp);
		} else if (Register.MAR.data < temp.stack.size() + temp.data.length) {
			storeStack(temp);
		} else {
			storeHeap(temp);
		}
	}

	private void fetchData(@NotNull ProcessAW temp) {
		Register.MBR.data = temp.data[Register.MAR.data];
	}

	private void fetchStack(@NotNull ProcessAW temp) throws StackOverFlowExceptionAW {
		if (Register.MAR.data>temp.stackSize+temp.data.length) throw new StackOverFlowExceptionAW();
		Register.MBR.data = temp.stack.get(Register.MAR.data-temp.data.length);
	}

	private void fetchHeap(@NotNull ProcessAW temp) {
		Register.MBR.data = temp.heap.get(Register.MAR.data-temp.stackSize-temp.data.length);
	}

	private void storeData(@NotNull ProcessAW temp) {
		temp.data[Register.MAR.data] = Register.MBR.data;
	}

	private void storeStack(@NotNull ProcessAW temp) {
		try {
			temp.stack.set(Register.MAR.data-temp.data.length, Register.MBR.data);
		} catch (ArrayIndexOutOfBoundsException e) {
			temp.stack.add(Register.MBR.data);
		}
	}

	private void storeHeap(@NotNull ProcessAW temp) {
		try {
			temp.heap.set(Register.MAR.data-temp.stackSize-temp.data.length, Register.MBR.data);
		} catch (ArrayIndexOutOfBoundsException e) {
			temp.heap.add(Register.MBR.data);
		}
	}
}
