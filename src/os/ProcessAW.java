package os;

import java.util.Stack;
import java.util.Vector;

public class ProcessAW {
	ProcessControlBlock pcb;
	public int stackSize;
	public int[] code, data;
	public Vector<Integer> stack, heap;
}