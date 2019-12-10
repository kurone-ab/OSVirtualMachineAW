package os;

import java.util.concurrent.locks.ReentrantLock;

public abstract class InterruptServiceRoutine{
	final ReentrantLock lock = new ReentrantLock(true);
	int priority;

	public abstract void handle(Interrupt interrupt);
}
