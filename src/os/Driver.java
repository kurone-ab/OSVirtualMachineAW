package os;

public interface Driver {
	void input(int sp, int address, int data);
	void output(int sp, int address);
}
