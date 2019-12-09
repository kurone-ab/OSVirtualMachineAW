package os;

import org.jetbrains.annotations.NotNull;

public abstract class InterruptServiceRoutine implements Comparable<InterruptServiceRoutine>{
	int pid, sp, address, csr, hsr, priority;
	void set(int pid, int sp, int address, int csr, int hsr){
		this.pid = pid;
		this.sp = sp;
		this.address = address;
		this.csr = csr;
		this.hsr = hsr;
	}

	@Override
	public int compareTo(@NotNull InterruptServiceRoutine o) {
		return this.priority >= o.priority ? 1 : -1;
	}

	abstract void handle();
}
