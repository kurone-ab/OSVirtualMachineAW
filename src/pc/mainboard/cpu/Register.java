package pc.mainboard.cpu;

public enum Register {
	PC, SP, MAR, MBR, IR, AC, STATUS, ITR, CSR, HSR;
	public int data;
	/*itr is store interrupt id*/
	/*status register data
	* 1. interrupt
	* 2. zero flag
	* 3. negative flag
	* 4. halt
	* */
	/*
	* program counter
	* stack pointer
	* memory address register
	* memory buffer register
	* instruction register
	* accumulator
	* status register
	* interrupt id register
	* instance register
	* current activation register
	* heap segment register
	*/
}