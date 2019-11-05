package pc.mainboard.cpu;

public enum Register {
	pc, sp, mar, mbr, ir, ac, status, itr;
	public int data;
	public boolean Z, N, I;
	/*itr is store interrupt id*/
}