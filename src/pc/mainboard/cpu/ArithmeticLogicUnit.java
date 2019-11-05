package pc.mainboard.cpu;

class ArithmeticLogicUnit {

	ArithmeticLogicUnit() {
	}

	void ADD() {
		Register.ac.data = Register.ac.data + Register.mbr.data;
		setFlag();
	}

	void SUB() {
		Register.ac.data = Register.ac.data - Register.mbr.data;
		setFlag();
	}

	void MUL() {
		Register.ac.data = Register.ac.data * Register.mbr.data;
		setFlag();
	}

	void DIV() {
		Register.ac.data = Register.ac.data / Register.mbr.data;
		setFlag();
	}

	void AND() {
		Register.ac.data = Register.ac.data & Register.mbr.data;
		setFlag();
	}

	void OR() {
		Register.ac.data = Register.ac.data | Register.mbr.data;
		setFlag();
	}

	void NOT() {
		Register.ac.data = ~Register.ac.data;
		setFlag();
	}

	void XOR() {
		Register.ac.data = Register.ac.data ^ Register.mbr.data;
		setFlag();
	}

	private void setFlag(){
		if (Register.ac.data == 0) Register.status.Z = true;
		else if (Register.ac.data < 0) Register.status.N = true;
		else Register.status.N = Register.status.Z = false;
	}
}
