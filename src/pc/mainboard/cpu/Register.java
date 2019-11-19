package pc.mainboard.cpu;

public enum Register {
	PC, SP, MAR, MBR, IR, AC, STATUS, ITR;
	public int data;
	/*itr is store interrupt id*/
	/*status register data
	* 1. interrupt
	* 2. zero flag
	* 3. negative flag
	* 4. halt
	* */
}