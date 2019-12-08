package os;

public interface Driver {
	void input(int sp, int address, int csr, int hsr, int data);
	void output(int sp, int address, int csr, int hsr);
}
