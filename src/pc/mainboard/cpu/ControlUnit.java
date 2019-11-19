package pc.mainboard.cpu;


import global.StackOverFlowExceptionAW;
import pc.mainboard.MainBoard;

class ControlUnit {
	private static CentralProcessingUnit.Instruction instruction;

	ControlUnit() {
	}

	void fetch() {
		Register.MAR.data = Register.PC.data;
		MainBoard.ram.fetchInstruction();
		Register.IR.data = Register.MBR.data;
		Register.PC.data++;
	}

	void decode() {
		instruction = CentralProcessingUnit.Instruction.values()[Register.IR.data >>> 16];
	}

	void execute() throws StackOverFlowExceptionAW {
		switch (instruction){
			case LDA: LDA(); break;
			case LDI: LDI(); break;
			case STA: STA(); break;
			case ASN: ASN(); break;
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
			case PRT: PRT(); break;
		}
	}

	private void LDA() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		Register.AC.data = Register.MBR.data;
	}

	private void LDI() {
		Register.AC.data = Register.IR.data & 0x0000ffff;
	}

	private void STA() {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		Register.MBR.data = Register.AC.data;
		MainBoard.ram.storeData();
	}

	private void ASN() {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		Register.MBR.data = 0;
		MainBoard.ram.storeData();
	}

	private void ADD() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.ADD();
	}

	private void SUB() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.SUB();
	}

	private void MUL() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.MUL();
	}

	private void DIV() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.DIV();
	}

	private void AND() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.AND();
	}

	private void OR() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.OR();
	}

	private void NOT() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.NOT();
	}

	private void XOR() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.XOR();
	}

	private void JMP() {
		Register.PC.data = Register.IR.data & 0x0000ffff;
	}

	private void JSZ() {
		if ((Register.STATUS.data&0x00000010)!=0) Register.PC.data = Register.IR.data & 0x0000ffff;
	}

	private void JSN() {
		if ((Register.STATUS.data&0x00000100)!=0) Register.PC.data = Register.IR.data & 0x0000ffff;
	}

	private void ITR(){
		Register.STATUS.data |= 0x00000001;
		Register.ITR.data = Register.IR.data & 0x0000ffff;
	}

	private void HLT(){
		Register.STATUS.data |= 0x00001000;
	}

	private void PRT() throws StackOverFlowExceptionAW {
		Register.MAR.data = Register.IR.data & 0x0000ffff;
		MainBoard.ram.fetchData();
		Register.AC.data = Register.MBR.data;
		System.out.println("ac data: "+Register.AC.data);
	}

}
