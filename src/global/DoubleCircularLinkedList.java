package global;

import java.io.Serializable;

public class DoubleCircularLinkedList<T> implements Serializable {
	private NodeAW head, current;
	private int size;

	public DoubleCircularLinkedList() {
	}

	public void add(T data) {
		if (this.head == null) {
			this.current = this.head = new NodeAW(data);
		} else if (this.head.pre == null) {
			NodeAW newData = new NodeAW(data);
			this.head.next = newData;
			newData.next = this.head;
			this.head.pre = newData;
			newData.pre = this.head;
		} else {
			NodeAW temp = this.head.pre;
			NodeAW newData = new NodeAW(data);
			temp.next = newData;
			newData.next = this.head;
			newData.pre = temp;
			this.head.pre = newData;
		}
		this.size++;
	}

	public T remove() {
		T temp = this.head.pre.data;
		this.head.pre = this.head.pre.pre;
		this.head.pre.next = this.head;
		System.gc();
		this.size--;
		return temp;
	}

	public T get(int i) {
		if (i >= this.size)
			throw new ArrayIndexOutOfBoundsException();
		int cen = this.size / 2;
		NodeAW temp = this.head;
		if (cen < i) for (int a = this.size; a > i; a--) temp = temp.pre;
		else for (int a = 0; a < i; a++) temp = temp.next;
		return temp.data;
	}

	public T next() {
		T data = this.current.data;
		this.current = this.current.next;
		return data;
	}

	public int size() {
		return this.size;
	}

	private class NodeAW {
		T data;
		NodeAW pre, next;

		NodeAW(T data) {
			this.data = data;
		}
	}
}
