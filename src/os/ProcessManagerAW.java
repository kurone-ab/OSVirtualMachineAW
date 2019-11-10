package os;

import global.DoubleCircularLinkedList;
import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.security.SecureRandom;
import java.util.Hashtable;

public class ProcessManagerAW {
    private Hashtable<Integer, ProcessControlBlock> pcbs;
    private SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();
    private SecureRandom random;

    {
        pcbs = new Hashtable<>();
        random = new SecureRandom();
        random.setSeed(System.currentTimeMillis());
    }

    void newProcess(int index, ProcessAW processAW) {
        ready.current = processAW;
        processAW.pid = random.nextInt();
        ProcessControlBlock pcb = new ProcessControlBlock();
        pcb.pid = processAW.pid;
        pcb.ps = Status.neww;
        pcb.registers = new int[Register.values().length];
        pcb.registers[Register.pc.ordinal()] = 0;
        pcb.registers[Register.sp.ordinal()] = index;
        readyProcess(pcb, index);
        if (ready.size() == 1) run(pcb);
    }

    private void readyProcess(ProcessControlBlock pcb, int index) {
        ready.add(index);
        pcb.ps = Status.ready;
    }

    private void run(ProcessControlBlock pcb) {
        pcb.ps = Status.run;
        long start = System.nanoTime();
        Thread isr;
        while (!ready.isEmpty()) {
            try {
                MainBoard.cpu.clock();
            } catch (StackOverFlowExceptionAW stackOverFlowExceptionAW) {
                Register.status.data |= 0x00001000;//halt
            }
            if (interrupted()) {
                int index = OperatingSystem.memoryManagerAW.processAddress(ready.current);
                this.contextSwitch(Status.wait);
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

    public synchronized void isrFinished(ProcessAW processAW) {
        wait.current = processAW;
        int index = OperatingSystem.memoryManagerAW.processAddress(wait.current);
        wait.remove(index);
        ready.add(index);
    }

    private void contextSwitch(Status status) {
        //context save
        ProcessControlBlock pcb = pcbs.get(ready.current.pid);
        pcb.ps = status;
        Register[] registers = Register.values();
        for (int i = 0; i < registers.length; i++)
            pcb.registers[i] = registers[i].data;

        ready.current = OperatingSystem.memoryManagerAW.getProcess(ready.next());

        //context load
        pcb = pcbs.get(ready.current.pid);
        pcb.ps = Status.run;
		for (int i = 0; i < registers.length; i++)
			 registers[i].data = pcb.registers[i];
    }

    private boolean interrupted() {
        return (Register.status.data & 0x00000001) != 0;
    }

    private boolean halt() {
        return (Register.status.data & 0x00001000) != 0;
    }

    private static class SchedulingQueue extends DoubleCircularLinkedList<Integer> {
        ProcessAW current;
    }
}
