package pc.io;

import os.DeviceType;
import os.Driver;
import os.IODevice;

import java.io.*;
import java.net.Socket;

public class NetworkAW implements IODevice {
	private static final NetworkDriver networkDriver = new NetworkDriver();
	private static Socket socket;
	private static InputStream inputStream;
	private static OutputStream outputStream;

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
		public void input(int pid, int sp, int address, int csr, int hsr) {

		}

		@Override
		public void output(int pid, int sp, int address, int csr, int hsr) {

		}

		@Override
		public void connect() {
			try {
				socket = new Socket("localhost", 16748);
				outputStream = socket.getOutputStream();
				inputStream = socket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void disconnect() {
			PrintWriter writer = new PrintWriter(outputStream);
			writer.println("-1");
			writer.flush();
		}
	}
}
