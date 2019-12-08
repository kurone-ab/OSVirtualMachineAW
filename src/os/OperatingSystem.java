package os;

public class OperatingSystem {
	static final long TIME_SLICE = 1000000000/*15000000-15ms*/;
	static ProcessManagerAW processManagerAW;
	static MemoryManagerAW memoryManagerAW;
	public static FileManagerAW fileManagerAW;
	static DeviceManagerAW deviceManagerAW;
	static UXManagerAW uxManagerAW;
	static InterruptVectorTable interruptVectorTable;
	static Driver consoleDriver, networkDriver;

	public void on(){
		processManagerAW = new ProcessManagerAW();
		memoryManagerAW = new MemoryManagerAW();
		fileManagerAW = new FileManagerAW();
		deviceManagerAW = new DeviceManagerAW();
		uxManagerAW = new UXManagerAW();
		interruptVectorTable = new InterruptVectorTable();
		fileManagerAW.on();
		memoryManagerAW.on();
		uxManagerAW.on();
	}

	public void connect(){
		consoleDriver = deviceManagerAW.getConsoleAW().getDriver();
		networkDriver = deviceManagerAW.getNetworkAW().getDriver();
	}

}
