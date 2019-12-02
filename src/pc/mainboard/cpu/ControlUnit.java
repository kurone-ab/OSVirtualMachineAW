package pc.mainboard.cpu;


import os.ActivationRecord;
import os.CompilerAW;
import os.Instance;
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
		instruction = CentralProcessingUnit.Instruction.values()[Register.IR.data >>> CompilerAW.instruction_bit];
	}

	void execute() {
		System.out.println("current instruction: " + instruction);
		switch (instruction) {
			case LDA:
				LDA();
				break;
			case LDPI:
				LDPI();
				break;
			case LDNI:
				LDNI();
				break;
			case STA:
				STA();
				break;
			case ADD:
				ADD();
				break;
			case SUB:
				SUB();
				break;
			case MUL:
				MUL();
				break;
			case DIV:
				DIV();
				break;
			case AND:
				AND();
				break;
			case OR:
				OR();
				break;
			case NOT:
				NOT();
				break;
			case XOR:
				XOR();
				break;
			case JMP:
				JMP();
				break;
			case JSZ:
				JSZ();
				break;
			case JSN:
				JSN();
				break;
			case ITR:
				ITR();
				break;
			case FNC:
				FNC();
				break;
			case RTN:
				RTN();
				break;
			case RTNV:
				RTNV();
				break;
			case NEW:
				NEW();
				break;
			case HLT:
				HLT();
				break;
		}
	}

	private void NEW() {
		int size = Register.IR.data & 0x00ffffff;
		Instance instance = new Instance(size);
		MainBoard.ram.memory[Register.SP.data].heap.add(instance);
	}

	private void LDNI() {
		Register.AC.data = -(Register.IR.data & 0x00ffffff);
	}

	private void LDPI() {
		Register.AC.data = Register.IR.data & 0x00ffffff;
	}

	private void LDA() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		Register.AC.data = Register.MBR.data;
	}


	private void STA() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		Register.MBR.data = Register.AC.data;
		MainBoard.ram.storeData();
	}

	private void ADD() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.ADD();
	}

	private void SUB() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.SUB();
	}

	private void MUL() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.MUL();
	}

	private void DIV() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.DIV();
	}

	private void AND() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.AND();
	}

	private void OR() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.OR();
	}

	private void NOT() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.NOT();
	}

	private void XOR() {
		Register.MAR.data = Register.IR.data & 0x00ffffff;
		MainBoard.ram.fetchData();
		CentralProcessingUnit.alu.XOR();
	}

	private void JMP() {
		Register.PC.data = Register.IR.data & 0x00ffffff;
	}

	private void JSZ() {
		if ((Register.STATUS.data & 0x00000010) != 0) Register.PC.data = Register.IR.data & 0x00ffffff;
	}

	private void JSN() {
		if ((Register.STATUS.data & 0x00000100) != 0) Register.PC.data = Register.IR.data & 0x00ffffff;
	}

	private void ITR() {
		Register.STATUS.data |= 0x00000001;
		Register.ITR.data = Register.IR.data & 0x00ffffff;
	}

	private void FNC() {
		int size = Register.IR.data & 0x0000ffff;
		int parameter_count = (Register.IR.data >>> CompilerAW.parameter_bit) & 0x000000ff;
		parameter_count *= 2;
		ActivationRecord activationRecord = new ActivationRecord(Register.PC.data + parameter_count + 1, size);
		ActivationRecord[] stack = MainBoard.ram.memory[Register.SP.data].stack;
		for (int i = 0; i < MainBoard.ram.memory.length; i++) {
			if (stack[i] == null) {
				stack[i] = activationRecord;
				Register.ARC.data = i;
				break;
			}
		}
	}

	private void RTN() {
		ActivationRecord activationRecord = MainBoard.ram.memory[Register.SP.data].stack[Register.ARC.data];
		Register.PC.data = activationRecord.returnPC;
		MainBoard.ram.memory[Register.SP.data].stack[Register.ARC.data] = null;
		Register.ARC.data--;
	}

	private void RTNV() {
		Register.AC.data = Register.IR.data & 0x00ffffff;
		this.RTN();
	}

	private void HLT() {
		Register.STATUS.data |= 0x00001000;
	}

}
