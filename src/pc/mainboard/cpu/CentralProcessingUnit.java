package pc.mainboard.cpu;

import global.StackOverFlowExceptionAW;
import org.jetbrains.annotations.Contract;

public class CentralProcessingUnit {
	private static final CentralProcessingUnit cpu = new CentralProcessingUnit();
	private static final ControlUnit cu = new ControlUnit();
	static final ArithmeticLogicUnit alu = new ArithmeticLogicUnit();

	@Contract(pure = true)
	private CentralProcessingUnit() {
	}

	@Contract(pure = true)
	public static CentralProcessingUnit getInstance() {
		return cpu;
	}

	public void clock() throws StackOverFlowExceptionAW {
		cu.fetch();
		cu.decode();
		cu.execute();
	}

	public enum Instruction {
		LDA, LDI, STA, ASN, ADD, SUB, MUL, DIV, AND, OR, NOT, XOR, JMP, JSZ, JSN, ITR, HLT, PRT
		/* load address
		 * load integer value
		 * store address
		 * assignment
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
		 * halt
		 * print
		 */
	}
}
