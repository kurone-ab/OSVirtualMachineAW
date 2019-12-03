package pc.mainboard.cpu;


import os.ActivationRecord;
import os.CompilerAW;
import os.Instance;
import os.ProcessAW;
import pc.mainboard.MainBoard;
import pc.mainboard.MemoryManagementUnit;

import java.util.Arrays;

import static pc.mainboard.MainBoard.mmu;
import static pc.mainboard.cpu.Register.*;

class ControlUnit {
	private static CentralProcessingUnit.Instruction instruction;

	ControlUnit() {
	}

	void fetch() {
		MAR.data = Register.PC.data;
		mmu.instructionFetch();
		IR.data = MBR.data;
		Register.PC.data++;
	}

	void decode() {
		instruction = CentralProcessingUnit.Instruction.values()[IR.data >>> CompilerAW.instruction_bit];
	}

	void execute() {
		System.out.println("current instruction: " + instruction);
		switch (instruction) {
			case LDA:
				LDA();
				break;
			case LDP:
				LDP();
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

	private void LDP() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch(MemoryManagementUnit.ABNORMAL);
		AC.data = MBR.data;
		System.out.println("accumulator value: "+ AC.data);
	}

	private void NEW() {
		int size = IR.data & 0x00ffffff;
		Instance instance = new Instance(size);
		mmu.create_instance(instance);
	}

	private void LDNI() {
		AC.data = -(IR.data & 0x00ffffff);
	}

	private void LDPI() {
		AC.data = IR.data & 0x00ffffff;
		System.out.println("accumulator value: "+ AC.data);
	}

	private void LDA() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		AC.data = MBR.data;
		System.out.println("accumulator value: "+ AC.data);
	}


	private void STA() {
		MAR.data = IR.data & 0x00ffffff;
		MBR.data = AC.data;
		mmu.dataStore();
		System.out.println("accumulator value: "+ AC.data);
	}

	private void ADD() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.ADD();
		System.out.println("accumulator value: "+ AC.data);
	}

	private void SUB() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.SUB();
		System.out.println("accumulator value: "+ AC.data);
	}

	private void MUL() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.MUL();
	}

	private void DIV() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.DIV();
	}

	private void AND() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.AND();
	}

	private void OR() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.OR();
	}

	private void NOT() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.NOT();
	}

	private void XOR() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		CentralProcessingUnit.alu.XOR();
	}

	private void JMP() {
		Register.PC.data = IR.data & 0x00ffffff;
	}

	private void JSZ() {
		if ((Register.STATUS.data & 0x00000010) != 0) Register.PC.data = IR.data & 0x00ffffff;
	}

	private void JSN() {
		if ((Register.STATUS.data & 0x00000100) != 0) Register.PC.data = IR.data & 0x00ffffff;
	}

	private void ITR() {
		Register.STATUS.data |= 0x00000001;
		Register.ITR.data = IR.data & 0x00ffffff;
	}

	private void FNC() {
		int size = IR.data & 0x0000ffff;
		int parameter_count = (IR.data >>> CompilerAW.parameter_bit) & 0x000000ff;
		parameter_count *= 2;
		ActivationRecord activationRecord = new ActivationRecord(Register.PC.data + parameter_count + 1, size);
		mmu.create_activation_record(activationRecord);
	}

	private void RTN() {
		ActivationRecord activationRecord = mmu.current_activation_record();
		Register.PC.data = activationRecord.returnPC;
		mmu.eliminate_activation_record();
		ARC.data--;
	}

	private void RTNV() {
		MAR.data = IR.data & 0x00ffffff;
		mmu.dataFetch();
		AC.data = MBR.data;
		this.RTN();
		System.out.println("accumulator value: "+ AC.data);
	}

	private void HLT() {
		Register.STATUS.data |= 0x00001000;
	}

}
