package os;

import org.jetbrains.annotations.NotNull;

public class Interrupt implements Comparable<Interrupt> {
    public int pid, iid, sp, address, csr, hsr, priority;

    public Interrupt(int pid, int iid, int sp, int address, int csr, int hsr, int priority) {
        this.pid = pid;
        this.iid = iid;
        this.sp = sp;
        this.address = address;
        this.csr = csr;
        this.hsr = hsr;
        this.priority = priority;
    }

    public Interrupt(int iid, int priority) {
        this.iid = iid;
        this.priority = priority;
    }

    @Override
    public int compareTo(@NotNull Interrupt o) {
        return this.priority <= o.priority ? -1 : 1;
    }
}
