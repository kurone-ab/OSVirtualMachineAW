package os;

import java.util.Hashtable;
import java.util.Map;

import static os.OperatingSystem.processManagerAW;
import static pc.mainboard.cpu.Register.*;
import static pc.mainboard.cpu.Register.HSR;

public class InterruptVectorTable {
	static final Hashtable<Integer, InterruptServiceRoutine> ivt = new Hashtable<>();

	public InterruptVectorTable() {
		ivt.put(0, (pid, sp, address, csr, hsr) -> processManagerAW.halt());//halt
		ivt.put(1, (pid, sp, address, csr, hsr) -> processManagerAW.contextSwitch(ProcessState.READY));//time expired
		ivt.put(2, (pid, sp, address, csr, hsr) -> {
			processManagerAW.enWaitQueue();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			Thread thread = new Thread(()->{
				OperatingSystem.consoleDriver.output(sp, address, csr, hsr);
				processManagerAW.isrFinished(pid);
			});
			thread.start();
		});//print
		ivt.put(3, (pid, sp, address, csr, hsr) -> {
			processManagerAW.enWaitQueue();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			Thread thread = new Thread(()->{
				OperatingSystem.deviceManagerAW.setConsoleEditable(true);
				OperatingSystem.consoleDriver.input(sp, address, csr, hsr);
				OperatingSystem.deviceManagerAW.setConsoleEditable(false);
				processManagerAW.isrFinished(pid);
			});
			thread.start();
		});//input
		ivt.put(4, (pid, sp, address, csr, hsr) -> processManagerAW.contextSwitch(ProcessState.READY));//send
		ivt.put(5, (pid, sp, address, csr, hsr) -> processManagerAW.contextSwitch(ProcessState.READY));//receive
	}
}
