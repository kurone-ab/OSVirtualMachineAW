package os;

import global.DoubleCircularLinkedList;
import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.security.SecureRandom;
import java.util.Enumeration;
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

    void setProcessID(ProcessAW processAW) {
        processAW.pid = random.nextInt();
    }

    synchronized void newProcess(int index, ProcessAW processAW) {
        ProcessControlBlock pcb = new ProcessControlBlock();
        pcb.pid = processAW.pid;
        pcb.ps = Status.neww;
        pcb.registers = new int[Register.values().length];
        pcb.registers[Register.pc.ordinal()] = 0;
        pcb.registers[Register.sp.ordinal()] = index;
        pcbs.put(processAW.pid, pcb);
        readyProcess(pcb, index);
        if (ready.size() == 1) {
            Thread run = new Thread(() -> this.run(pcb));
            run.start();
        }
    }

    private void readyProcess(ProcessControlBlock pcb, int index) {
        ready.add(index);
        pcb.ps = Status.ready;
    }

    private void run(ProcessControlBlock pcb) {
        pcb.ps = Status.run;
        Thread isr;
        Register[] registers = Register.values();//Initialize the register to the value of pcb before run.
        for (int i = 0; i < registers.length; i++)
            registers[i].data = pcb.registers[i];
        ready.current = pcb.pid;
        long start = System.nanoTime();
        while (!ready.isEmpty()) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("current process id: " + ready.current);
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
                start = System.nanoTime();
                continue;
            }
            if (halt()) {
                System.out.println("halt");
                int index = OperatingSystem.memoryManagerAW.unload(ready.current);
                int pid = ready.current;
                ready.remove(index);
                this.pcbs.remove(pid);
                this.stackPointerReset();
                this.contextSwitch(Status.terminate);
                start = System.nanoTime();
                continue;
            }
            if ((System.nanoTime() - start) > OperatingSystem.TIME_SLICE) {
                System.out.println("time expired");
                this.contextSwitch(Status.ready);
                start = System.nanoTime();
            }
            System.out.println("time left: "+(System.nanoTime() - start));
        }
    }

    public synchronized void isrFinished(int pid) {
        wait.current = pid;
        int index = OperatingSystem.memoryManagerAW.processAddress(wait.current);
        wait.remove(index);
        ready.add(index);
    }

    private void contextSwitch(Status status) {
        if (ready.isEmpty()) return;
        ProcessControlBlock pcb;
        Register[] registers = Register.values();
        if (status != Status.terminate) {
            //context save
            pcb = pcbs.get(ready.current);
            pcb.ps = status;
            for (int i = 0; i < registers.length; i++)
                pcb.registers[i] = registers[i].data;
        }

        ready.current = OperatingSystem.memoryManagerAW.getProcess(ready.next());

        //context load
        pcb = pcbs.get(ready.current);
        pcb.ps = Status.run;
        for (int i = 0; i < registers.length; i++)
            registers[i].data = pcb.registers[i];
    }

    private void stackPointerReset() {
        Enumeration<Integer> keys = this.pcbs.keys();
        this.ready.reset();
        while (keys.hasMoreElements()) {
            Integer key = keys.nextElement();
            ProcessControlBlock pcb = this.pcbs.get(key);
            int sp = OperatingSystem.memoryManagerAW.processAddress(pcb.pid);
            pcb.registers[Register.sp.ordinal()] = sp;
            this.ready.add(sp);
            this.pcbs.replace(key, pcb);
        }
    }

    private boolean interrupted() {
        return (Register.status.data & 0x00000001) != 0;
    }

    private boolean halt() {
        return (Register.status.data & 0x00001000) != 0;
    }

    private static class SchedulingQueue extends DoubleCircularLinkedList<Integer> {
        int current;
    }
}
