package os;

public interface Driver {
	void input(int pid, int sp, int address, int csr, int hsr);
	void output(int pid, int sp, int address, int csr, int hsr);
	void connect();
	void disconnect();
}
