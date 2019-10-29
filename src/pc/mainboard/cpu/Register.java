package pc.mainboard.cpu;

public enum Register {
	pc, sp, mar, mbr, ir, ac, status;
	public int data;
	public boolean Z, N, I;
}