package pc.mainboard.cpu;

import pc.mainboard.cpu.bus.AddressBus;
import pc.mainboard.cpu.bus.ControlBus;
import pc.mainboard.cpu.bus.DataBus;

class ControlUnit {
	private static ControlBus controlBus;

	ControlUnit() {
		controlBus = new ControlBus();
	}

	enum Instruction {
		LDA, STA, ADD, SUB, MUL, DIV, AND, OR, NOT, XOR, EQL, JMP, JSZ, JSN
		/* load address
		 * store address
		 * add
		 * subtract
		 * multiple
		 * division
		 * and
		 * or
		 * not
		 * exclusive or
		 * equal
		 * jump
		 * jump if zero is true
		 * jump if negative is true
		 */
	}

	void fetch(){

	}
	void decode(){

	}
	void execute(){

	}

	void connect(AddressBus addressBus, DataBus dataBus){

	}

}
