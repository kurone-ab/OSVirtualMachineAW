package pc.mainboard.cpu;

class ArithmeticLogicUnit {

	ArithmeticLogicUnit() {
	}

	void ADD() {
		Register.AC.data = Register.AC.data + Register.MBR.data;
		setFlag();
	}

	void SUB() {
		Register.AC.data = Register.AC.data - Register.MBR.data;
		setFlag();
	}

	void MUL() {
		Register.AC.data = Register.AC.data * Register.MBR.data;
		setFlag();
	}

	void DIV() {
		Register.AC.data = Register.AC.data / Register.MBR.data;
		setFlag();
	}

	void AND() {
		Register.AC.data = Register.AC.data & Register.MBR.data;
		setFlag();
	}

	void OR() {
		Register.AC.data = Register.AC.data | Register.MBR.data;
		setFlag();
	}

	void NOT() {
		Register.AC.data = ~Register.AC.data;
		setFlag();
	}

	void XOR() {
		Register.AC.data = Register.AC.data ^ Register.MBR.data;
		setFlag();
	}

	private void setFlag(){
		if (Register.AC.data == 0) Register.STATUS.data |= 0x00000010;
		else if (Register.AC.data < 0) Register.STATUS.data |= 0x00000100;
		else Register.STATUS.data &= 0x11111001;
	}
}
