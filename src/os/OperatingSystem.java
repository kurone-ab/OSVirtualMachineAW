package os;

import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;

import java.util.Vector;

public class OperatingSystem {
//	static final int
	static ProcessManagerAW processManagerAW;
	static MemoryManagerAW memoryManagerAW;
	static FileManagerAW fileManagerAW;
	static DeviceManagerAW deviceManagerAW;
	public void on(){
		processManagerAW = new ProcessManagerAW();
		memoryManagerAW = new MemoryManagerAW();
		fileManagerAW = new FileManagerAW();
		deviceManagerAW = new DeviceManagerAW();
	}

	public void processing(){
		try {
			MainBoard.cpu.cycle();
		} catch (StackOverFlowExceptionAW stackOverFlowExceptionAW) {
			stackOverFlowExceptionAW.printStackTrace();
		}
	}


}
