package pc.mainboard.cpu;


import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;
import pc.mainboard.RandomAccessMemory;

import java.lang.reflect.InvocationTargetException;

public class ControlUnit {
	private static Instruction instruction;

	ControlUnit() {
	}

	void fetch() {
		Register.mar.data = Register.pc.data;
		MainBoard.ram.fetchInstruction();
		Register.ir.data = Register.mbr.data;
	}

	void decode() {
		instruction = Instruction.values()[Register.ir.data >>> 16];
	}

	void execute() throws StackOverFlowExceptionAW {
		switch (instruction){
			case LDA: LDA(); break;
			case LDI: LDI(); break;
			case STA: STA(); break;
			case ADD: ADD(); break;
			case SUB: SUB(); break;
			case MUL: MUL(); break;
			case DIV: DIV(); break;
			case AND: AND(); break;
			case OR: OR(); break;
			case NOT: NOT(); break;
			case XOR: XOR(); break;
			case JMP: JMP(); break;
			case JSZ: JSZ(); break;
			case JSN: JSN(); break;
			case ITR: ITR(); break;
			case HLT: HLT(); break;
		}
	}

	private void LDA() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		Register.ac.data = Register.mbr.data;
	}

	private void LDI() {
		Register.ac.data = Register.ir.data & 0x0000ffff;
	}

	private void STA() {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		Register.mbr.data = Register.ac.data;
		MainBoard.ram.storeData();
	}

	private void ADD() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.ADD();
	}

	private void SUB() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.SUB();
	}

	private void MUL() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.MUL();
	}

	private void DIV() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.DIV();
	}

	private void AND() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.AND();
	}

	private void OR() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.OR();
	}

	private void NOT() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.NOT();
	}

	private void XOR() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.XOR();
	}

	private void JMP() {
		Register.pc.data = Register.ir.data & 0x0000ffff;
	}

	private void JSZ() {
		if ((Register.status.data&0x00000010)!=0) Register.pc.data = Register.ir.data & 0x0000ffff;
	}

	private void JSN() {
		if ((Register.status.data&0x00000100)!=0) Register.pc.data = Register.ir.data & 0x0000ffff;
	}

	private void ITR(){
		Register.status.data |= 0x00000001;
		Register.itr.data = Register.ir.data & 0x0000ffff;
	}

	private void HLT(){

	}

	public enum Instruction {
		LDA, LDI, STA, ASN, ADD, SUB, MUL, DIV, AND, OR, NOT, XOR, JMP, JSZ, JSN, ITR, HLT
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
		 */
	}

}
