package pc.mainboard.cpu;

class ArithmeticLogicUnit {
	static void ADD() {
		Register.ac.data = Register.ac.data + Register.mbr.data;
	}

	static void SUB() {
		Register.ac.data = Register.ac.data - Register.mbr.data;
		if (Register.ac.data == 0) Register.status.Z = true;
		else if (Register.ac.data < 0) Register.status.N = true;
		else Register.status.N = Register.status.Z = false;
	}

	static void MUL() {
		Register.ac.data = Register.ac.data * Register.mbr.data;
	}

	static void DIV() {
		Register.ac.data = Register.ac.data / Register.mbr.data;
	}

	static void AND() {
		Register.ac.data = Register.ac.data & Register.mbr.data;
	}

	static void OR() {
		Register.ac.data = Register.ac.data | Register.mbr.data;
	}

	static void NOT() {
		Register.ac.data = ~Register.ac.data;
	}

	static void XOR() {
		Register.ac.data = Register.ac.data ^ Register.mbr.data;
	}
}
