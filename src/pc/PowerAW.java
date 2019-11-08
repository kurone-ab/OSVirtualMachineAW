package pc;

import global.DoubleCircularLinkedList;
import pc.mainboard.MainBoard;

public class PowerAW {
	public static void main(String[] args) {
//		MainBoard mainBoard = new MainBoard();
//		mainBoard.on();
		DoubleCircularLinkedList<String> linkedList = new DoubleCircularLinkedList<>();
		linkedList.add("aaa");
		for (int i = 0; i < 5; i++)
			System.out.println(linkedList.next());
	}
}
