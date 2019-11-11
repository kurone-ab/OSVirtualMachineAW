package os;

import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;

public class OperatingSystem {
	static final long TIME_SLICE = 200000/*15000000-15ms*/;
	static ProcessManagerAW processManagerAW;
	static MemoryManagerAW memoryManagerAW;
	static FileManagerAW fileManagerAW;
	static DeviceManagerAW deviceManagerAW;
	static InterruptServiceRoutine isr;

	public void on(){
		processManagerAW = new ProcessManagerAW();
		memoryManagerAW = new MemoryManagerAW();
		fileManagerAW = new FileManagerAW();
		deviceManagerAW = new DeviceManagerAW();
	}

}
