package pc.mainboard;

import global.StackOverFlowExceptionAW;
import org.jetbrains.annotations.NotNull;
import os.ProcessAW;
import pc.mainboard.cpu.Register;

import java.lang.reflect.Array;
import java.util.Vector;

public class RandomAccessMemory {
	public final Vector<ProcessAW> memory = new Vector<>();

	public void fetchInstruction() {
		Register.mbr.data = memory.get(Register.sp.data).code[Register.mar.data];
	}

	public synchronized void fetchData() throws StackOverFlowExceptionAW {
		ProcessAW temp = memory.get(Register.sp.data);
		if (Register.mar.data < temp.data.length) {
			fetchData(temp);
		} else if (Register.mar.data < temp.stack.size() + temp.data.length) {
			fetchStack(temp);
		} else {
			fetchHeap(temp);
		}
	}

	public synchronized void storeData() {
		ProcessAW temp = memory.get(Register.sp.data);
		if (Register.mar.data < temp.data.length) {
			storeData(temp);
		} else if (Register.mar.data < temp.stack.size() + temp.data.length) {
			storeStack(temp);
		} else {
			storeHeap(temp);
		}
	}

	private void fetchData(@NotNull ProcessAW temp) {
		Register.mbr.data = temp.data[Register.mar.data];
	}

	private void fetchStack(@NotNull ProcessAW temp) throws StackOverFlowExceptionAW {
		if (Register.mar.data>temp.stackSize+temp.data.length) throw new StackOverFlowExceptionAW();
		Register.mbr.data = temp.stack.get(Register.mar.data-temp.data.length);
	}

	private void fetchHeap(@NotNull ProcessAW temp) {
		Register.mbr.data = temp.heap.get(Register.mar.data-temp.stackSize-temp.data.length);
	}

	private void storeData(@NotNull ProcessAW temp) {
		temp.data[Register.mar.data] = Register.mbr.data;
	}

	private void storeStack(@NotNull ProcessAW temp) {
		try {
			temp.stack.set(Register.mar.data-temp.data.length, Register.mbr.data);
		} catch (ArrayIndexOutOfBoundsException e) {
			temp.stack.add(Register.mbr.data);
		}
	}

	private void storeHeap(@NotNull ProcessAW temp) {
		try {
			temp.heap.set(Register.mar.data-temp.stackSize-temp.data.length, Register.mbr.data);
		} catch (ArrayIndexOutOfBoundsException e) {
			temp.heap.add(Register.mbr.data);
		}
	}
}
