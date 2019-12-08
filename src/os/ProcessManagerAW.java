package os;

import os.compiler.CompilerAW;
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
        this.clockState = ClockState.RUN;
        while (true) {
            System.out.println("notified");
            ready.nextProcess();
            this.currentProcess.ps = ProcessState.RUN;
            Register[] registers = Register.values();//Initialize the register to the value of pcb before run.
            for (int i = 0; i < registers.length; i++)
                registers[i].data = this.currentProcess.context[i];
            OperatingSystem.uxManagerAW.updateRegisters();
            OperatingSystem.uxManagerAW.updateMemory();
            OperatingSystem.uxManagerAW.updateReadyQueue(this.ready);
            long start = System.nanoTime();
            while (this.currentProcess.ps != ProcessState.TERMINATE) {
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

                if (interrupted() || OperatingSystem.deviceManagerAW.hasMoreInterrupt()) {// TODO: 2019-11-12 make interrupt
                    int interruptID = Register.ITR.data >>> CompilerAW.instruction_bit;
                    if (OperatingSystem.deviceManagerAW.hasMoreInterrupt()) {
                        OperatingSystem.deviceManagerAW.putInterrupt();
                        interruptID = OperatingSystem.deviceManagerAW.getInterrupt();
                    }
                    InterruptServiceRoutine isr = InterruptVectorTable.ivt.get(interruptID);
                    isr.handle(this.currentProcess.pid, Register.SP.data, Register.ITR.data & 0x00ffffff, Register.CSR.data, Register.HSR.data);
                    Register.STATUS.data &= 0x11111110;
                    Register.ITR.data = 0;
                    start = System.nanoTime();
                }
            }
            if ((System.nanoTime() - start) > OperatingSystem.TIME_SLICE) {
                Register.STATUS.data |= 0x00000001;
                Register.ITR.data = 1 << CompilerAW.instruction_bit;
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
        if (this.clockThread.getState() == Thread.State.WAITING && this.clockState == ClockState.RUN) {
            this.ready.nextProcess();
            this.clockThread.notify();
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public ProcessAW getCurrentProcess() {
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
        ready.increasePriority();
        if (ready.isEmpty() && processState == ProcessState.TERMINATE) {
            this.currentProcess.ps = ProcessState.TERMINATE;
            return;
        }
        Register[] registers = Register.values();
        this.currentProcess.ps = processState;
        if (processState != ProcessState.TERMINATE) {
            //context save
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

    void enWaitQueue() {
        wait.offer(this.currentProcess);
    }

    private boolean interrupted() {
        return (Register.STATUS.data & 0x00000001) != 0;
    }

    void halt() {// TODO: 2019-12-08 please change
        OperatingSystem.memoryManagerAW.unload(this.currentProcess.pid);
        ready.remove(this.currentProcess);
        this.contextSwitch(ProcessState.TERMINATE);
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
