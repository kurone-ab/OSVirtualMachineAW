package os;

import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;

import java.util.Vector;

public class OperatingSystem {
	Vector<ProcessControlBlock> pcb;
	public void on(){
		this.pcb = new Vector<>();
	}

	public void processing(){
		try {
			MainBoard.cpu.cycle();
		} catch (StackOverFlowExceptionAW stackOverFlowExceptionAW) {
			stackOverFlowExceptionAW.printStackTrace();
		}
	}


}
