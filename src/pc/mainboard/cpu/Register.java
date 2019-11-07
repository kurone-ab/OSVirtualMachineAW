package pc.mainboard.cpu;

public enum Register {
	pc, sp, mar, mbr, ir, ac, status, itr;
	public int data;
	/*itr is store interrupt id*/
	/*status register data
	* 1. interrupt
	* 2. zero flag
	* 3. negative flag
	* */
}