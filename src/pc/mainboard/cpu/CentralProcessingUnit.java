package pc.mainboard.cpu;

import global.StackOverFlowExceptionAW;
import org.jetbrains.annotations.Contract;

public class CentralProcessingUnit {
	static final ArithmeticLogicUnit alu = new ArithmeticLogicUnit();
	private static final CentralProcessingUnit cpu = new CentralProcessingUnit();
	private static final ControlUnit cu = new ControlUnit();

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
		LDA, LDPI, LDNI,
		ASN, HDS, STP, STA, ADD, SUB, MUL, DIV, AND, OR, NOT, XOR, JMP, JSZ, JSN, ITR, FNC, RTN, RTNV, NEW, HLT
		/* load address
		 * load positive integer value
		 * load negative integer value
		 * heap data store
		 * set parameter
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
		 * return value
		 * new
		 * halt
		 */
	}
}
