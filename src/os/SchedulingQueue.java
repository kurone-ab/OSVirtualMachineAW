package os;

public class SchedulingQueue {
	private static final SchedulingQueue queue = new SchedulingQueue();

	public static SchedulingQueue getInstance(){
		return queue;
	}
}
