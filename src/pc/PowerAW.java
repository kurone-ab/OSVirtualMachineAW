package pc;

import global.DoubleCircularLinkedList;
import pc.mainboard.MainBoard;

public class PowerAW {
	public static void main(String[] args) {
//		MainBoard mainBoard = new MainBoard();
//		mainBoard.on();
		DoubleCircularLinkedList<String> linkedList = new DoubleCircularLinkedList<>();
		linkedList.add("aaa");
		linkedList.add("bbb");
		linkedList.add("ccc");
		linkedList.add("ddd");
		linkedList.add("eee");
		linkedList.add("fff");
		System.out.println(linkedList.get(5));
	}
}
