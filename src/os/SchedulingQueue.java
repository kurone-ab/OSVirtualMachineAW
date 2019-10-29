package os;

import global.DoubleLinkedList;

class SchedulingQueue extends DoubleLinkedList<Integer> {
    static final SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();

	private SchedulingQueue() {
	}
}
