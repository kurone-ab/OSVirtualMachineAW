package os;

import global.DoubleCircularLinkedList;

import java.security.SecureRandom;

public class ProcessManagerAW {
	private SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();
	private SecureRandom random;

	{
		random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
	}

	public void newProcess(int index, ProcessAW processAW) {
		ready.current = processAW;
		processAW.pcb.pid = random.nextInt();
		processAW.pcb.ps = Status.neww;
		readyProcess(index);
	}

	private void readyProcess(int index){
		ready.add(index);
		ready.current.pcb.ps = Status.ready;
	}

	public void run(){
		ready.current.pcb.ps = Status.run;

	}

	public void processSwitch(){

	}

	private class SchedulingQueue extends DoubleCircularLinkedList<Integer> {
		ProcessAW current;
	}
}
