package pc.io;

import os.Driver;

public class ConsoleAW implements IODevice {
	@Override
	public Driver getDriver() {
		return null;
	}

	private static class ConsoleDriver implements Driver{

		@Override
		public void input(int sp, int address, int data) {

		}

		@Override
		public void output(int sp, int address) {

		}
	}
}
