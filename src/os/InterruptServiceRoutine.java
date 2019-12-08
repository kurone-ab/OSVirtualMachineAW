package os;

public interface InterruptServiceRoutine {
	void handle(int pid, int sp, int address, int csr, int hsr);
}
