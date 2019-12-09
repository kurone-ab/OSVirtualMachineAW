package os;

import org.jetbrains.annotations.NotNull;
import os.compiler.CompilerAW;

import java.util.Hashtable;
import java.util.Map;

import static os.OperatingSystem.processManagerAW;
import static pc.mainboard.cpu.Register.*;
import static pc.mainboard.cpu.Register.HSR;

public class InterruptVectorTable {
	static final Hashtable<Integer, InterruptServiceRoutine> ivt = new Hashtable<>();
	public static final int haltID = 0, timeExpiredID = 1, printID = 2, inputID = 3, sendID = 4, receiveID = 5;

	public InterruptVectorTable() {
		ivt.put(haltID, new HaltInterruptServiceRoutine());//halt
		ivt.put(timeExpiredID, new TimeInterruptServiceRoutine());//time expired
		ivt.put(printID, new PrintInterruptServiceRoutine());//print
		ivt.put(inputID, new InputInterruptServiceRoutine());//input
	}
	private static class HaltInterruptServiceRoutine extends InterruptServiceRoutine{
		@Override
		void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 10;
		}

		@Override
		void handle() {
			processManagerAW.halt();
		}
	}

	private static class TimeInterruptServiceRoutine extends InterruptServiceRoutine{
		@Override
		void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 11;
		}

		@Override
		void handle() {
			processManagerAW.contextSwitch(ProcessState.READY);
		}
	}

	private static class PrintInterruptServiceRoutine extends InterruptServiceRoutine{

		@Override
		void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 2;
		}

		@Override
		void handle() {
			processManagerAW.enWaitQueue();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			Thread thread = new Thread(()->{
				OperatingSystem.consoleDriver.output(sp, address, csr, hsr);
				processManagerAW.isrFinished(pid);
			});
			thread.start();
		}

	}

	private static class InputInterruptServiceRoutine extends InterruptServiceRoutine{

		@Override
		void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 2;
		}

		@Override
		void handle() {
			processManagerAW.enWaitQueue();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			OperatingSystem.deviceManagerAW.setConsoleEditable(true);
			Thread thread = new Thread(()->{
				OperatingSystem.consoleDriver.input(sp, address, csr, hsr);
				OperatingSystem.deviceManagerAW.setConsoleEditable(false);
				processManagerAW.isrFinished(pid);
			});
			thread.start();
		}

	}
}
