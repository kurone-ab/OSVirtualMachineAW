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
		ivt.put(connectID, new ConnectInterruptServiceRoutine());//connect
		ivt.put(disconnectID, new DisconnectInterruptServiceRoutine());//disconnect
		ivt.put(finishID, new InterruptFinished());//finish
	}

	public InterruptServiceRoutine getInterrupt(int id){
		try {
			return ivt.get(id).clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	private static class HaltInterruptServiceRoutine extends InterruptServiceRoutine{
		public HaltInterruptServiceRoutine() {
			this.isrID = InterruptVectorTable.haltID;
		}

		@Override
		public void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 10;
		}

		@Override
		public void handle() {
			processManagerAW.halt();
		}
	}

	private static class TimeExpiredInterruptServiceRoutine extends InterruptServiceRoutine{

		public TimeExpiredInterruptServiceRoutine() {
			this.isrID = InterruptVectorTable.timeExpiredID;
		}

		@Override
		public void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 11;

		}

		@Override
		public void handle() {
			processManagerAW.contextSwitch(ProcessState.READY);
		}
	}

	private static class PrintInterruptServiceRoutine extends InterruptServiceRoutine{
		public PrintInterruptServiceRoutine() {
			this.isrID = InterruptVectorTable.printID;
		}

		@Override
		public void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 2;
		}

		@Override
		public void handle() {
			processManagerAW.waitOffer();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			Thread thread = new Thread(()->{
				OperatingSystem.consoleDriver.output(pid, sp, address, csr, hsr);
				processManagerAW.isrFinished(pid);
			});
			thread.start();
		}

	}

	private static class InputInterruptServiceRoutine extends InterruptServiceRoutine{
		public InputInterruptServiceRoutine() {
			this.isrID = InterruptVectorTable.inputID;
		}

		@Override
		public void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 2;
		}

		@Override
		public void handle() {
			processManagerAW.waitOffer();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			OperatingSystem.deviceManagerAW.setConsoleEditable(true);
			Thread thread = new Thread(()->{
				OperatingSystem.consoleDriver.input(pid, sp, address, csr, hsr);
			});
			thread.start();
		}

	}

	private static class SendInterruptServiceRoutine extends InterruptServiceRoutine{
		public SendInterruptServiceRoutine() {
			this.isrID = InterruptVectorTable.inputID;
		}

		@Override
		public void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 2;
		}

		@Override
		public void handle() {
			processManagerAW.waitOffer();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			OperatingSystem.deviceManagerAW.setConsoleEditable(true);
			Thread thread = new Thread(()->{
				OperatingSystem.consoleDriver.input(pid, sp, address, csr, hsr);
			});
			thread.start();
		}

	}

	private static class InterruptFinished extends InterruptServiceRoutine{

		@Override
		public void handle() {
			processManagerAW.isrFinished(pid);
		}
	}

	private static class ConnectInterruptServiceRoutine extends InterruptServiceRoutine{
		public ConnectInterruptServiceRoutine() {
			this.isrID = connectID;
		}

		@Override
		public void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 2;
		}

		@Override
		public void handle() {
			processManagerAW.waitOffer();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			Thread thread = new Thread(()->{
				OperatingSystem.networkDriver.connect();
				processManagerAW.isrFinished(pid);
			});
			thread.start();
		}
	}

	private static class DisconnectInterruptServiceRoutine extends InterruptServiceRoutine{
		public DisconnectInterruptServiceRoutine() {
			this.isrID = disconnectID;
		}

		@Override
		public void set(int pid, int sp, int address, int csr, int hsr) {
			super.set(pid, sp, address, csr, hsr);
			this.priority = 2;
		}

		@Override
		public void handle() {
			processManagerAW.waitOffer();
			processManagerAW.contextSwitch(ProcessState.WAIT);
			Thread thread = new Thread(()->{
				OperatingSystem.networkDriver.disconnect();
				processManagerAW.isrFinished(pid);
			});
			thread.start();
		}
	}
}
