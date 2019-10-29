package os;

import java.util.Vector;

public class OperatingSystem {
	Vector<ProcessControlBlock> pcb;
	public void on(){
		this.pcb = new Vector<>();
	}
}
