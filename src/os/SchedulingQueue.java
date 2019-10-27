package os;

import java.util.Vector;

public class SchedulingQueue extends Vector<Integer> {
    private static final SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();
    private static int readyIndex;

	private SchedulingQueue() {}

	public static void readyEnqueue(int address) {
    	ready.add(address);
    }

    public static void readyDequeue() {
    }

    public static int next(){
		if (readyIndex==ready.elementCount) readyIndex = 0;
		int val = ready.get(readyIndex);
		readyIndex++;
		return val;
	}

    public static void waitEnqueue(int address) {
    	wait.add(address);
    }

    public static void waitDequeue() {
    }
}
