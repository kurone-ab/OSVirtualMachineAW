package os;

import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.function.Consumer;

public class ProcessManagerAW {
	private SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();
	private ProcessControlBlock currentProcess;
	private int delay;

	synchronized void newProcess(int index, ProcessAW processAW) {
		ProcessControlBlock pcb = new ProcessControlBlock();
		pcb.pid = processAW.pid;
		pcb.ps = State.NEW;
		pcb.context = new int[Register.values().length];
		pcb.context[Register.PC.ordinal()] = processAW.main;
		pcb.context[Register.SP.ordinal()] = index;
		readyProcess(pcb);
		if (ready.size() == 1) {
			Thread run = new Thread(() -> this.run(pcb));
			run.start();
		}
	}

	private void readyProcess(ProcessControlBlock pcb) {
		ready.offer(pcb);
		pcb.ps = State.READY;
	}

	private void run(ProcessControlBlock pcb) {
		pcb.ps = State.RUN;
		Thread isr;
		Register[] registers = Register.values();//Initialize the register to the value of pcb before run.
		for (int i = 0; i < registers.length; i++)
			registers[i].data = pcb.context[i];
		this.currentProcess = pcb;
		OperatingSystem.uxManagerAW.updateRegisters();
		long start = System.nanoTime();
		while (this.currentProcess != null) {
			if (this.delay!=0) {
				try {
					Thread.sleep(this.delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("current process id: " + this.currentProcess.pid);
			MainBoard.cpu.clock();
			OperatingSystem.uxManagerAW.updateRegisters();
			if (interrupted()) {// TODO: 2019-11-12 make interrupt
				this.contextSwitch(State.WAIT);
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
				this.contextSwitch(State.TERMINATE);
				start = System.nanoTime();
				continue;
			}
			if ((System.nanoTime() - start) > OperatingSystem.TIME_SLICE) {
				System.out.println("time expired");
				this.contextSwitch(State.READY);
				start = System.nanoTime();
			}
			System.out.println("time left: " + (OperatingSystem.TIME_SLICE - (System.nanoTime() - start)));
		}
	}

	public synchronized void isrFinished(int pid) {
		ready.offer(wait.pull(pid));
	}

	public void setDelay(int delay){
		this.delay = delay;
	}

	private void contextSwitch(State state) {
		if (ready.isEmpty() && state == State.TERMINATE) {
			this.currentProcess = null;
			return;
		}
		Register[] registers = Register.values();
		if (state != State.TERMINATE) {
			//context save
			this.currentProcess.ps = state;
			for (int i = 0; i < registers.length; i++)
				this.currentProcess.context[i] = registers[i].data;
			this.currentProcess.priority--;
			ready.offer(this.currentProcess);
		}
		ready.nextProcess();
		ready.increasePriority();
		//context load
		this.currentProcess.ps = State.RUN;
		for (int i = 0; i < registers.length; i++)
			registers[i].data = this.currentProcess.context[i];
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
