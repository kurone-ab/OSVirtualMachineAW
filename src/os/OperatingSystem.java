package os;

import pc.mainboard.RandomAccessMemory;
import pc.mainboard.cpu.Register;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class OperatingSystem {
	Vector<ProcessControlBlock> pcb;
	private int currentProcess;
	public void on(){
		this.pcb = new Vector<>();
	}

	public void processing(){
		Register.mar.data = Register.pc.data;
		Register.mbr.data = RandomAccessMemory.fetchInstruction(this.currentProcess);
	}


}
