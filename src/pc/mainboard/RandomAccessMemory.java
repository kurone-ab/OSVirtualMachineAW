package pc.mainboard;

import global.StackOverFlowExceptionAW;
import os.ProcessAW;
import pc.mainboard.cpu.Register;

import java.util.Vector;

public class RandomAccessMemory {
	private static final Vector<ProcessAW> memory = new Vector<>();

	public int load(ProcessAW processAW){
		memory.add(processAW);
		return memory.size()-1;
	}

	public void unload(int process){
		memory.remove(process);
	}

	public void fetchInstruction() {
		Register.mbr.data = memory.get(Register.sp.data).code[Register.mar.data];
	}

	public void fetchData() throws StackOverFlowExceptionAW {
		ProcessAW temp = memory.get(Register.sp.data);
		if (Register.mar.data < temp.data.length) {
			fetchData(temp);
		} else if (Register.mar.data < temp.stack.size() + temp.data.length) {
			fetchStack(temp);
		} else {
			fetchHeap(temp);
		}
	}

	public void storeData() {
		ProcessAW temp = memory.get(Register.sp.data);
		if (Register.mar.data < temp.data.length) {
			storeData(temp);
		} else if (Register.mar.data < temp.stack.size() + temp.data.length) {
			storeStack(temp);
		} else {
			storeHeap(temp);
		}
	}

	private void fetchData(ProcessAW temp) {
		Register.mbr.data = temp.data[Register.mar.data];
	}

	private void fetchStack(ProcessAW temp) throws StackOverFlowExceptionAW {
		if (Register.mar.data>temp.stackSize+temp.data.length) throw new StackOverFlowExceptionAW();
		Register.mbr.data = temp.stack.get(Register.mar.data);
	}

	private void fetchHeap(ProcessAW temp) {
		Register.mbr.data = temp.heap.get(temp.stackSize-Register.mar.data);
	}

	private void storeData(ProcessAW temp) {
		temp.data[Register.mar.data] = Register.mbr.data;
	}

	private void storeStack(ProcessAW temp) {
		temp.stack.set(Register.mar.data, Register.mbr.data);
	}

	private void storeHeap(ProcessAW temp) {
		temp.stack.set(Register.mar.data, Register.mbr.data);
	}
}
