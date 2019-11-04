package os;

import global.DoubleCircularLinkedList;

class SchedulingQueue extends DoubleCircularLinkedList<Integer> {
    static final SchedulingQueue ready = new SchedulingQueue(), wait = new SchedulingQueue();

	private SchedulingQueue() {
	}
}
