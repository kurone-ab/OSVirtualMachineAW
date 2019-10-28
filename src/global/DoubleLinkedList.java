package global;

import java.io.Serializable;

public class DoubleLinkedList<T> implements Serializable {
    private NodeAW head, current;
    private int size;

    public DoubleLinkedList() {
    }

    public void add(T data) {
        if (this.head == null) {
            this.current = this.head = new NodeAW(data);
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
