package pc;

import global.DoubleLinkedList;

public class PowerAW {
	public static void main(String[] args) {
		DoubleLinkedList<String> linkedList = new DoubleLinkedList<>();

		linkedList.add("a");
		linkedList.add("b");
		linkedList.add("c");
		for (int i = 0;i<linkedList.size();i++){
			System.out.println(linkedList.next());
		}
		linkedList.remove();
		for (int i = 0;i<linkedList.size();i++){
			System.out.println(linkedList.next());
		}
	}
}
