package os;

import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.util.NoSuchElementException;
import java.util.PriorityQueue;

public class ProcessManagerAW {
	private SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();
	private ProcessControlBlock currentProcess;
	private int delay;
	private ClockState clockState;
	private final Thread clockThread = new Thread(this::run);

	synchronized void newProcess(int index, int priority, ProcessAW processAW) {
		ProcessControlBlock pcb = new ProcessControlBlock();
		pcb.pid = processAW.pid;
		pcb.priority = priority;
		pcb.ps = ProcessState.NEW;
		pcb.context = new int[Register.values().length];
		pcb.context[Register.PC.ordinal()] = processAW.main;
		pcb.context[Register.SP.ordinal()] = index;
		readyProcess(pcb);
		if (this.clockThread.getState() == Thread.State.NEW) this.clockThread.start();
		if (this.clockThread.getState() == Thread.State.WAITING && this.clockState == ClockState.RUN)
			this.clockThread.notify();
	}

	private void readyProcess(ProcessControlBlock pcb) {
		ready.offer(pcb);
		pcb.ps = ProcessState.READY;
		OperatingSystem.uxManagerAW.updateReadyQueue(this.ready);
	}

	private void run() {
		while (true) {
			ready.nextProcess();
			this.currentProcess.ps = ProcessState.RUN;
			Thread isr;
			Register[] registers = Register.values();//Initialize the register to the value of pcb before run.
			for (int i = 0; i < registers.length; i++)
				registers[i].data = this.currentProcess.context[i];
			OperatingSystem.uxManagerAW.updateRegisters();
			OperatingSystem.uxManagerAW.updateMemory();
			OperatingSystem.uxManagerAW.updateReadyQueue(this.ready);
			long start = System.nanoTime();
			while (this.currentProcess != null) {
				if (this.clockState == ClockState.WAIT) {
					synchronized (this.clockThread) {
						try {
							this.clockThread.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				MainBoard.cpu.clock();
				OperatingSystem.uxManagerAW.updateRegisters();
				if (this.delay != 0) {
					OperatingSystem.uxManagerAW.updateProcess(this.getCurrentProcess());
					try {
						Thread.sleep(this.delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (interrupted()) {// TODO: 2019-11-12 make interrupt
					this.contextSwitch(ProcessState.WAIT);
					wait.add(this.currentProcess);
					isr = new Thread(() -> {
						OperatingSystem.isr = InterruptVectorTable.ivt.get(Register.ITR.data);
						OperatingSystem.isr.handle(this.currentProcess.pid);
					});
					isr.start();
					start = System.nanoTime();
					continue;
				}
				if (halt()) {
					OperatingSystem.memoryManagerAW.unload(this.currentProcess.pid);
					ready.remove(this.currentProcess);
					this.contextSwitch(ProcessState.TERMINATE);
					start = System.nanoTime();
					continue;
				}
				if ((System.nanoTime() - start) > OperatingSystem.TIME_SLICE) {
					System.out.println("time expired");
					this.contextSwitch(ProcessState.READY);
					start = System.nanoTime();
				}
				System.out.println("time left: " + (OperatingSystem.TIME_SLICE - (System.nanoTime() - start)));
			}
			OperatingSystem.uxManagerAW.updateProcess(null);
			synchronized (this.clockThread) {
				try {
					this.clockThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void isrFinished(int pid) {
		ready.offer(wait.pull(pid));
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public ProcessAW getCurrentProcess() {
		return OperatingSystem.memoryManagerAW.getProcess(this.currentProcess.pid);
	}

	public ClockState changeClockState() {
		if (this.clockState == ClockState.WAIT) synchronized (this.clockThread) {
			this.clockThread.notify();
			this.clockState = ClockState.RUN;
		}
		else this.clockState = ClockState.WAIT;
		return this.clockState;
	}

	private void contextSwitch(ProcessState processState) {
		ready.increasePriority();
		if (ready.isEmpty() && processState == ProcessState.TERMINATE) {
			this.currentProcess = null;
			return;
		}
		Register[] registers = Register.values();
		if (processState != ProcessState.TERMINATE) {
			//context save
			this.currentProcess.ps = processState;
			for (int i = 0; i < registers.length; i++)
				this.currentProcess.context[i] = registers[i].data;
			this.currentProcess.priority--;
			ready.offer(this.currentProcess);
		}
		ready.nextProcess();
		//context load
		this.currentProcess.ps = ProcessState.RUN;
		for (int i = 0; i < registers.length; i++)
			registers[i].data = this.currentProcess.context[i];
		OperatingSystem.uxManagerAW.updateReadyQueue(this.ready);
	}

	private boolean interrupted() {
		return (Register.STATUS.data & 0x00000001) != 0;
	}

	private boolean halt() {
		return (Register.STATUS.data & 0x00001000) != 0;
	}

	private class SchedulingQueue extends PriorityQueue<ProcessControlBlock> {
		SchedulingQueue() {
			super();
		}

		void nextProcess() {
			if (!this.isEmpty()) {
				currentProcess = this.poll();
			}
		}

		ProcessControlBlock pull(int pid) {
			for (ProcessControlBlock processControlBlock : this) {
				if (processControlBlock.pid == pid) {
					this.remove(processControlBlock);
					return processControlBlock;
				}
			}
			throw new NoSuchElementException();
		}

		void increasePriority() {
			this.forEach((i) -> i.priority++);
		}
	}
}
