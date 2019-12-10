package os;

import os.compiler.CompilerAW;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

import static os.OperatingSystem.*;

public class ProcessManagerAW {
    private SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();
    private ProcessControlBlock currentProcess;
    private int delay;
    private ClockState clockState;
    private final Thread clockThread = new Thread(this::run);
    private final ReentrantLock lock = new ReentrantLock(true);

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
            synchronized (this.clockThread) {
                this.clockThread.notify();
            }
    }

    private void readyProcess(ProcessControlBlock pcb) {
        ready.offer(pcb);
        pcb.ps = ProcessState.READY;
        OperatingSystem.uxManagerAW.updateReadyQueue(this.ready);
    }

    private void run() {
        while (true) {
            this.clockState = ClockState.RUN;
            ready.nextProcess();
            this.currentProcess.ps = ProcessState.RUN;
            Register[] registers = Register.values();//Initialize the register to the value of pcb before run.
            for (int i = 0; i < registers.length; i++)
                registers[i].data = this.currentProcess.context[i];
            uxManagerAW.updateRegisters();
            uxManagerAW.updateMemory();
            uxManagerAW.updateReadyQueue(this.ready);
            long start = System.nanoTime();
            try {
                lock.lock();
                while (this.currentProcess != null) {
                    if (this.currentProcess.ps == ProcessState.TERMINATE) break;
                    if (this.clockState == ClockState.WAIT) {
                        synchronized (this.clockThread) {
                            try {
                                this.clockThread.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (this.currentProcess.ps == ProcessState.RUN) MainBoard.cpu.clock();
                    uxManagerAW.updateRegisters();
                    if (this.delay != 0) {
                        uxManagerAW.updateProcess(this.getCurrentProcess());
                        try {
                            Thread.sleep(this.delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    boolean moreInterrupt = deviceManagerAW.hasMoreInterrupt(),
                            interrupted = interrupted();
                    if (interrupted || moreInterrupt) {
                        int interruptID = Register.ITR.data >>> CompilerAW.instruction_bit;
                        int interruptData = Register.ITR.data & 0x00ffffff;
                        if (interruptID==2){
                            System.out.println(this.currentProcess.pid);
                            System.out.println(interruptData);
                        }
                        InterruptServiceRoutine isr = interruptVectorTable.getISR(interruptID);
                        Interrupt interrupt = new Interrupt(this.currentProcess.pid, interruptID, Register.SP.data,
                                interruptData, Register.CSR.data, Register.HSR.data, isr.priority);
                        if (interrupted && moreInterrupt) deviceManagerAW.putInterrupt(interrupt);
                        if (moreInterrupt) {
                            interrupt = deviceManagerAW.getInterrupt();
                            isr = interruptVectorTable.getISR(interrupt.iid);
                        }
                        isr.handle(interrupt);
                        Register.STATUS.data &= 0x11111110;
                        Register.ITR.data &= 0x00ffffff;
                        start = System.nanoTime();
                    }
                    if ((System.nanoTime() - start) > TIME_SLICE) {
                        deviceManagerAW.putInterrupt(new Interrupt(InterruptVectorTable.timeExpiredID, interruptVectorTable.getISR(InterruptVectorTable.timeExpiredID).priority));
                    }
                }
            } finally {
                lock.unlock();
            }
            uxManagerAW.updateProcess(null);
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
        ProcessControlBlock pcb;
        try {
            pcb = this.wait.pull(pid);
            pcb.ps = ProcessState.READY;
            this.ready.offer(pcb);
        } catch (NoSuchElementException e) {
            System.out.println(pid);
            e.printStackTrace();
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public ProcessAW getCurrentProcess() {
        if (this.currentProcess == null) return null;
        return OperatingSystem.memoryManagerAW.getProcess(this.currentProcess.pid);
    }

    ClockState changeClockState() {
        if (this.clockState == ClockState.WAIT) synchronized (this.clockThread) {
            this.clockThread.notify();
            this.clockState = ClockState.RUN;
        }
        else this.clockState = ClockState.WAIT;
        return this.clockState;
    }

    void contextSwitch(ProcessState processState) {
        try {
            lock.lock();
            ready.increasePriority();
            if (ready.isEmpty() && processState == ProcessState.TERMINATE) {
                this.currentProcess.ps = processState;
                return;
            }
            this.currentProcess.ps = processState;
            if (processState != ProcessState.TERMINATE) {
                //context save
                for (Register register:Register.values()){
                    this.currentProcess.context[register.ordinal()] = register.data;
                }
                this.currentProcess.priority--;
                if (processState == ProcessState.READY) this.ready.offer(this.currentProcess);
                else if (processState==ProcessState.WAIT) this.wait.offer(this.currentProcess);
            }
            this.ready.nextProcess();
            //context load
            this.currentProcess.ps = ProcessState.RUN;
            if (this.currentProcess.context[Register.ITR.ordinal()]!=0)
                System.out.println("---");
            for (Register register:Register.values()){
                register.data = this.currentProcess.context[register.ordinal()];
            }
            OperatingSystem.uxManagerAW.updateReadyQueue(this.ready);
            OperatingSystem.uxManagerAW.updateWaitQueue(this.wait);
        } finally {
            lock.unlock();
        }
    }

    private boolean interrupted() {
        return (Register.STATUS.data & 0x00000001) != 0;
    }

    void halt() {
        OperatingSystem.memoryManagerAW.unload(this.currentProcess.pid);
        this.contextSwitch(ProcessState.TERMINATE);
    }

    private class SchedulingQueue extends PriorityQueue<ProcessControlBlock> {
        SchedulingQueue() {
            super();
        }

        void nextProcess() {
            if (!this.isEmpty()) {
                currentProcess = this.poll();
            } else currentProcess = null;
        }

        synchronized ProcessControlBlock pull(int pid) {
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
