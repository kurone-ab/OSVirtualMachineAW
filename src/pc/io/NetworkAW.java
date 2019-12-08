package pc.io;

import os.DeviceType;
import os.Driver;
import os.IODevice;

public class NetworkAW implements IODevice {
	private static final NetworkDriver networkDriver = new NetworkDriver();

	@Override
	public Driver getDriver() {
		return networkDriver;
	}

	@Override
	public DeviceType getType() {
		return DeviceType.network;
	}

	private static class NetworkDriver implements Driver{
		@Override
		public void input(int sp, int address, int csr, int hsr, int data) {

		}

		@Override
		public void output(int sp, int address, int csr, int hsr) {

		}
	}
}
