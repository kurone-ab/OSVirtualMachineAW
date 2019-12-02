package os;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

public class ProcessManagerAW {
	private Hashtable<Integer, ProcessControlBlock> pcbs = new Hashtable<>();
	private SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();

	synchronized void newProcess(int index, ProcessAW processAW) {
		ProcessControlBlock pcb = new ProcessControlBlock();
		pcb.pid = processAW.pid;
		pcb.ps = State.NEW;
		pcb.registers = new int[Register.values().length];
		pcb.registers[Register.PC.ordinal()] = processAW.main;
		pcb.registers[Register.SP.ordinal()] = index;
		pcbs.put(processAW.pid, pcb);
		readyProcess(pcb);
		if (ready.size() == 1) {
			Thread run = new Thread(() -> this.run(pcb));
			run.start();
		}
	}

	private void readyProcess(ProcessControlBlock pcb) {
		ready.add(new ElementAW(pcb.pid, pcb.priority));
		pcb.ps = State.READY;
	}

	private void run(ProcessControlBlock pcb) {
		pcb.ps = State.RUN;
		Thread isr;
		Register[] registers = Register.values();//Initialize the register to the value of pcb before run.
		for (int i = 0; i < registers.length; i++)
			registers[i].data = pcb.registers[i];
		ready.currentID = ready.next();
		long start = System.nanoTime();
		while (!ready.isEmpty()) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
			System.out.println("current process id: " + ready.currentID);
			MainBoard.cpu.clock();
			if (interrupted()) {// TODO: 2019-11-12 make interrupt
				int index = OperatingSystem.memoryManagerAW.processAddress(ready.currentID);
				this.contextSwitch(State.WAIT);
				ready.remove(index);
				wait.add(index);
				isr = new Thread(() -> {
					OperatingSystem.isr = InterruptVectorTable.ivt.get(Register.ITR.data);
					OperatingSystem.isr.handle(ready.currentID);
				});
				isr.start();
				start = System.nanoTime();
				continue;
			}
			if (halt()) {
				OperatingSystem.memoryManagerAW.unload(ready.currentID);
				int pid = ready.currentID;
				ready.remove(pid);
				this.pcbs.remove(pid);
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
		wait.currentID = pid;
		int index = OperatingSystem.memoryManagerAW.processAddress(wait.currentID);
		wait.remove(index);
		ready.add(index);
	}

	private void contextSwitch(State state) {
		if (ready.isEmpty()) return;
		ProcessControlBlock pcb;

		Register[] registers = Register.values();
		if (state != State.TERMINATE) {
			//context save
			pcb = pcbs.get(ready.currentID);
			pcb.ps = state;
			for (int i = 0; i < registers.length; i++)
				pcb.registers[i] = registers[i].data;
		}
		ready.next();
		//context load
		pcb = pcbs.get(ready.currentID);
		pcb.ps = State.RUN;
		for (int i = 0; i < registers.length; i++)
			registers[i].data = pcb.registers[i];
	}

	private boolean interrupted() {
		return (Register.STATUS.data & 0x00000001) != 0;
	}

	private boolean halt() {
		return (Register.STATUS.data & 0x00001000) != 0;
	}

	private static class SchedulingQueue extends PriorityQueue<ElementAW> {
		int currentID;
		private

		SchedulingQueue() {
			super();
		}

		void next() {
			if (!this.isEmpty())
				this.currentID = this.poll().id;
			else throw new NoSuchElementException();
		}
	}

	private static class ElementAW implements Comparable<ElementAW> {
		int id, priority;

		ElementAW(int id, int priority) {
			this.id = id;
			this.priority = priority;
		}

		@Override
		public int compareTo(@NotNull ProcessManagerAW.ElementAW o) {
			return this.priority > o.priority ? -1 : 1;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;

			if (!(o instanceof ElementAW)) return false;

			ElementAW elementAW = (ElementAW) o;

			return new EqualsBuilder()
					.append(id, elementAW.id)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37)
					.append(id)
					.toHashCode();
		}
	}
}
