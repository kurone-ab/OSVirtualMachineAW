package pc;

import de.javasoft.synthetica.plain.SyntheticaPlainLookAndFeel;
import pc.mainboard.MainBoard;

import javax.swing.*;
import java.text.ParseException;

public class PowerAW {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new SyntheticaPlainLookAndFeel());
        } catch (UnsupportedLookAndFeelException | ParseException e) {
            e.printStackTrace();
        }
		MainBoard mainBoard = new MainBoard();
		mainBoard.on();
//        int i = 0;
//        int[] array = new int[5];
//        Vector<Integer> vector = new Vector<>();
//        vector.add(i++);
//        vector.add(i++);
//        vector.add(i++);
//        vector.add(i++);
//        vector.add(i++);
//        i = 0;
//        for (int a : vector)
//            array[i++] = a;
    }
}
