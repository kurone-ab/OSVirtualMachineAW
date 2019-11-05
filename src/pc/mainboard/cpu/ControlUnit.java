package pc.mainboard.cpu;


import global.StackOverFlowExceptionAW;
import pc.mainboard.RandomAccessMemory;

import java.lang.reflect.InvocationTargetException;

public class ControlUnit {
	private static Instruction instruction;

	ControlUnit() {
	}

	public void fetch() {
		Register.mar.data = Register.pc.data;
		RandomAccessMemory.fetchInstruction();
		Register.ir.data = Register.mbr.data;
	}

	public void decode() {
		instruction = Instruction.values()[Register.ir.data >>> 16];
	}

	public void execute() throws StackOverFlowExceptionAW {
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
		RandomAccessMemory.fetchData();
		Register.ac.data = Register.mbr.data;
	}

	private void LDI() {
		Register.ac.data = Register.ir.data & 0x0000ffff;
	}

	private void STA() {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		Register.mbr.data = Register.ac.data;
		RandomAccessMemory.storeData();
	}

	private void ADD() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.ADD();
	}

	private void SUB() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.SUB();
	}

	private void MUL() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.MUL();
	}

	private void DIV() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.DIV();
	}

	private void AND() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.AND();
	}

	private void OR() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.OR();
	}

	private void NOT() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.NOT();
	}

	private void XOR() throws StackOverFlowExceptionAW {
		Register.mar.data = Register.ir.data & 0x0000ffff;
		RandomAccessMemory.fetchData();
		ArithmeticLogicUnit.XOR();
	}

	private void JMP() {
		Register.pc.data = Register.ir.data & 0x0000ffff;
	}

	private void JSZ() {
		if (Register.status.Z) Register.pc.data = Register.ir.data & 0x0000ffff;
	}

	private void JSN() {
		if (Register.status.N) Register.pc.data = Register.ir.data & 0x0000ffff;
	}

	private void ITR(){
		Register.status.I = true;
		Register.itr.data = Register.ir.data & 0x0000ffff;
	}

	private void HLT(){

	}

	public enum Instruction {
		LDA, LDI, STA, ADD, SUB, MUL, DIV, AND, OR, NOT, XOR, JMP, JSZ, JSN, ITR, HLT
		/* load address
		 * load integer value
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
		 * halt
		 */
	}

}
