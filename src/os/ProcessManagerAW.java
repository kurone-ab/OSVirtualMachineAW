package os;

import global.DoubleCircularLinkedList;
import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.security.SecureRandom;

public class ProcessManagerAW {
	private SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();
	private SecureRandom random;

	{
		random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
	}

	public void newProcess(int index, ProcessAW processAW) {
		ready.current = processAW;
		processAW.pcb.pid = random.nextInt();
		processAW.pcb.ps = Status.neww;
		readyProcess(index);
	}

	private void readyProcess(int index) {
		ready.add(index);
		ready.current.pcb.ps = Status.ready;
		ready.current.pcb.pc = 0;
		ready.current.pcb.sp = index;
	}

	public void run() {
		ready.current.pcb.ps = Status.run;
		long start = System.nanoTime();
		Thread isr = null;
		while (!ready.isEmpty()) {
			if (isr != null)
				if (isr.getState() == Thread.State.TERMINATED) {

				}
			try {
				MainBoard.cpu.cycle();
			} catch (StackOverFlowExceptionAW stackOverFlowExceptionAW) {
				Register.status.data |= 0x00001000;//halt
			}
			if (interrupted()) {
				this.contextSwitch(Status.wait);
				int index = OperatingSystem.memoryManagerAW.processAddress(ready.current);
				ready.remove(index);
				wait.add(index);
				isr = new Thread(() -> {
					OperatingSystem.isr = InterruptVectorTable.ivt.get(Register.itr.data);
					OperatingSystem.isr.handle(ready.current);
				});
				isr.start();
				continue;
			}
			if (halt()) {
				this.contextSwitch(Status.terminate);
				int index = OperatingSystem.memoryManagerAW.unload(ready.current);
				ready.remove(index);
				continue;
			}
			if ((System.nanoTime() - start) > OperatingSystem.TIME_SLICE) {
				this.contextSwitch(Status.ready);
				start = System.nanoTime();
			}
		}
	}

	public synchronized void isrFinished(ProcessAW processAW){
		wait.current = processAW;
		int index = OperatingSystem.memoryManagerAW.processAddress(wait.current);
		wait.remove(index);
		ready.add(index);
	}

	private void contextSwitch(Status status) {
		//context save
		ready.current.pcb.ps = status;
		ready.current.pcb.pc = Register.pc.data;
		ready.current.pcb.sp = Register.sp.data;
		ready.current.pcb.mar = Register.mar.data;
		ready.current.pcb.mbr = Register.mbr.data;
		ready.current.pcb.ac = Register.ac.data;
		ready.current.pcb.status = Register.status.data;
		ready.current.pcb.ir = Register.ir.data;
		ready.current.pcb.itr = Register.itr.data;

		int next = ready.next();
		ready.current = OperatingSystem.memoryManagerAW.getProcess(next);

		//context load
		ready.current.pcb.ps = Status.run;
		Register.pc.data = ready.current.pcb.pc;
		Register.sp.data = ready.current.pcb.sp;
		Register.mar.data = ready.current.pcb.mar;
		Register.mbr.data = ready.current.pcb.mbr;
		Register.ac.data = ready.current.pcb.ac;
		Register.status.data = ready.current.pcb.status;
		Register.ir.data = ready.current.pcb.ir;
		Register.itr.data = ready.current.pcb.itr;
	}

	private boolean interrupted() {
		return (Register.status.data & 0x00000001) != 0;
	}

	private boolean halt() {
		return (Register.status.data & 0x00001000) != 0;
	}

	private class SchedulingQueue extends DoubleCircularLinkedList<Integer> {
		ProcessAW current;
	}
}
