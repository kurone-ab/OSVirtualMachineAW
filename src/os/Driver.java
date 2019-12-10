package os;

public interface Driver {
	void input(Interrupt interrupt);
	void output(Interrupt interrupt);
	void connect();
	void disconnect();
}
