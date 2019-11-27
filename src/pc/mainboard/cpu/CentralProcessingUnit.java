package pc.mainboard.cpu;

import global.StackOverFlowExceptionAW;
import org.jetbrains.annotations.Contract;

public class CentralProcessingUnit {
	private static final CentralProcessingUnit cpu = new CentralProcessingUnit();
	private static final ControlUnit cu = new ControlUnit();
	static final ArithmeticLogicUnit alu = new ArithmeticLogicUnit();

	private CentralProcessingUnit() {
	}

	public static CentralProcessingUnit getInstance() {
		return cpu;
	}

	public void clock() throws StackOverFlowExceptionAW {
		cu.fetch();
		cu.decode();
		cu.execute();
	}

	public enum Instruction {
		LDA, LDPI, LDNI, HDS, STA, ADD, SUB, MUL, DIV, AND, OR, NOT, XOR, JMP, JSZ, JSN, ITR, FNC, RTN, NEW, HLT
		/* load address
		 * load positive integer value
		 * load negative integer value
		 * heap data store
		 * store address
		 * add
		 * subtract
		 * multiple
		 * division
		 * and
		 * or
		 * not
		 * exclusive or
		 * jump
		 * jump if zero is true
		 * jump if negative is true
		 * interrupt
		 * function call
		 * return
		 * new
		 * halt
		 */
	}
}
