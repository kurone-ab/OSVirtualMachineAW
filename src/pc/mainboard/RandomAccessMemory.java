package pc.mainboard;

import os.ProcessAW;
import pc.mainboard.cpu.Register;

import java.util.Vector;

public class RandomAccessMemory {
	public static final Vector<ProcessAW> memory = new Vector<>();

	public static int fetchInstruction(int i){
		return memory.get(i).code[Register.mar.data];
	}
}
