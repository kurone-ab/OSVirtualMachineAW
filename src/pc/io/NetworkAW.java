package pc.io;

import os.Driver;

public class NetworkAW implements IODevice {
	@Override
	public Driver getDriver() {
		return null;
	}

	private static class NetworkDriver implements Driver{
		@Override
		public void input(int sp, int address, int data) {

		}

		@Override
		public void output(int sp, int address) {

		}
	}
}
