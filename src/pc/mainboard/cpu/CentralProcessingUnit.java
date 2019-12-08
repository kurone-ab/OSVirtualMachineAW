package pc.mainboard.cpu;

public class CentralProcessingUnit {
	static final ArithmeticLogicUnit alu = new ArithmeticLogicUnit();
	private static final CentralProcessingUnit cpu = new CentralProcessingUnit();
	private static final ControlUnit cu = new ControlUnit();

	private CentralProcessingUnit() {
	}

	public static CentralProcessingUnit getInstance() {
		return cpu;
	}

	public void clock() {
		cu.fetch();
		cu.decode();
		cu.execute();
	}

	public enum Instruction {
		 LDA, LDP, LDPI, LDNI, STA, ADD, SUB, MUL, DIV, AND, OR, NOT, XOR, JMP, JSZ, JSN, ITR, SITR, FNC, RTN, SHR, NEW, HLT
		/* load address
		 * load parameter
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
		 * set heap register
		 * new
		 * halt
		 */
	}
}
