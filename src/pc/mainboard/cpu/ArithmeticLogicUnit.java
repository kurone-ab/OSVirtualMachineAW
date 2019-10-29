package pc.mainboard.cpu;

class ArithmeticLogicUnit {
	static void ADD() {
		Register.ac.data = Register.ac.data + Register.mbr.data;
	}

	static void SUB() {
		Register.ac.data = Register.ac.data - Register.mbr.data;
		Register.status.N = Register.ac.data < 0;
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

	static void EQL() {
		Register.status.Z = Register.ac.data == Register.mbr.data;
	}
}
