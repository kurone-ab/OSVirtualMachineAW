package os;

import java.util.Hashtable;

import static os.OperatingSystem.processManagerAW;

public class InterruptVectorTable {
	private static final Hashtable<Integer, InterruptServiceRoutine> ivt = new Hashtable<>();
	public static final int haltID = 0, timeExpiredID = 1, printID = 2, inputID = 3, sendID = 4, receiveID = 5,
	finishID = 10, connectID = 6, disconnectID = 7;

	public InterruptVectorTable() {
		ivt.put(haltID, new HaltInterruptServiceRoutine());//halt
		ivt.put(timeExpiredID, new TimeExpiredInterruptServiceRoutine());//time expired
		ivt.put(printID, new PrintInterruptServiceRoutine());//print
		ivt.put(inputID, new InputInterruptServiceRoutine());//input
		ivt.put(sendID, new SendInterruptServiceRoutine());//send
		ivt.put(receiveID, new ReceiveInterruptServiceRoutine());//receive
		ivt.put(connectID, new ConnectInterruptServiceRoutine());//connect
		ivt.put(disconnectID, new DisconnectInterruptServiceRoutine());//disconnect
		ivt.put(finishID, new InterruptFinished());//finish
	}

	public InterruptServiceRoutine getISR(int id){
		return ivt.get(id);
	}

	private static class HaltInterruptServiceRoutine extends InterruptServiceRoutine{
		public HaltInterruptServiceRoutine() {
			this.priority = 10;
		}

		@Override
		public void handle(Interrupt interrupt) {
			processManagerAW.halt();
		}
	}

	private static class TimeExpiredInterruptServiceRoutine extends InterruptServiceRoutine{

		public TimeExpiredInterruptServiceRoutine() {
			this.priority = 20;
		}

		@Override
		public void handle(Interrupt interrupt) {
			processManagerAW.contextSwitch(ProcessState.READY);
		}
	}

	private static class PrintInterruptServiceRoutine extends InterruptServiceRoutine{
		public PrintInterruptServiceRoutine() {
			this.priority = 5;
		}

		@Override
		public void handle(Interrupt interrupt) {
			try {
				lock.lock();
				processManagerAW.contextSwitch(ProcessState.WAIT);
				OperatingSystem.consoleDriver.output(interrupt);
			} finally {
				lock.unlock();
			}
		}

	}

	private static class InputInterruptServiceRoutine extends InterruptServiceRoutine{
		public InputInterruptServiceRoutine() {
			this.priority = 5;
		}

		@Override
		public void handle(Interrupt interrupt) {
			try {
				lock.lock();
				processManagerAW.contextSwitch(ProcessState.WAIT);
				OperatingSystem.deviceManagerAW.setConsoleEditable(true);
				OperatingSystem.consoleDriver.input(interrupt);
			} finally {
				lock.unlock();
			}
		}

	}

	private static class SendInterruptServiceRoutine extends InterruptServiceRoutine{
		public SendInterruptServiceRoutine() {
			this.priority = 5;
		}

		@Override
		public void handle(Interrupt interrupt) {
			try {
				lock.lock();
				processManagerAW.contextSwitch(ProcessState.WAIT);
				OperatingSystem.networkDriver.output(interrupt);
			} finally {
				lock.unlock();
			}
		}

	}

	private static class ReceiveInterruptServiceRoutine extends InterruptServiceRoutine{
		public ReceiveInterruptServiceRoutine() {
			this.priority = 5;
		}

		@Override
		public void handle(Interrupt interrupt) {
			try {
				lock.lock();
				processManagerAW.contextSwitch(ProcessState.WAIT);
				OperatingSystem.networkDriver.input(interrupt);
			} finally {
				lock.unlock();
			}
		}

	}

	private static class InterruptFinished extends InterruptServiceRoutine{

		@Override
		public void handle(Interrupt interrupt) {
			try {
				lock.lock();
				processManagerAW.isrFinished(interrupt.pid);
			} finally {
				lock.unlock();
			}
		}
	}

	private static class ConnectInterruptServiceRoutine extends InterruptServiceRoutine{
		public ConnectInterruptServiceRoutine() {
			this.priority = 5;
		}

		@Override
		public void handle(Interrupt interrupt) {
			processManagerAW.contextSwitch(ProcessState.WAIT);
			OperatingSystem.networkDriver.connect();
			processManagerAW.isrFinished(interrupt.pid);
		}
	}

	private static class DisconnectInterruptServiceRoutine extends InterruptServiceRoutine{
		public DisconnectInterruptServiceRoutine() {
			this.priority = 5;
		}

		@Override
		public void handle(Interrupt interrupt) {
			processManagerAW.contextSwitch(ProcessState.WAIT);
			OperatingSystem.networkDriver.disconnect();
			processManagerAW.isrFinished(interrupt.pid);
		}
	}
}
