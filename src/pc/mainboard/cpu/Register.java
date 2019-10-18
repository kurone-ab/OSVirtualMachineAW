package pc.mainboard.cpu;

enum Register {
	pc, sp, mar, mbr, ir, ac, status;
	int data;
	byte Z, I;
}