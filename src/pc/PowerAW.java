package pc;

import global.DoubleCircularLinkedList;
import global.ParserAW;
import pc.mainboard.MainBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class PowerAW {
    public static void main(String[] args) {
//		MainBoard mainBoard = new MainBoard();
//		mainBoard.on();
        int i = 0;
        int[] array = new int[5];
        Vector<Integer> vector = new Vector<>();
        vector.add(i++);
        vector.add(i++);
        vector.add(i++);
        vector.add(i++);
        vector.add(i++);
        i = 0;
        for (int a : vector)
            array[i++] = a;
        System.out.println(Arrays.toString(array));
    }
}
