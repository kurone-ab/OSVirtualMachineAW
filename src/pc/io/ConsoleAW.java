package pc.io;

import os.DeviceType;
import os.Driver;
import os.IODevice;
import pc.mainboard.MainBoard;

import javax.swing.*;

public class ConsoleAW extends JTextArea implements IODevice {
	private final ConsoleDriver consoleDriver = new ConsoleDriver();
	@Override
	public Driver getDriver() {
		return consoleDriver;
	}

	@Override
	public DeviceType getType() {
		return DeviceType.console;
	}

	private class ConsoleDriver implements Driver{

		@Override
		public void input(int sp, int address, int csr, int hsr, int data) {
			MainBoard.mmu.dataStore(data, address, sp, hsr, csr);
		}

		@Override
		public void output(int sp, int address, int csr, int hsr) {
			try {
				setText(String.valueOf(MainBoard.mmu.dataFetch(address, sp, hsr, csr)));
			} catch (IllegalAccessException ignored) {
			}
		}
	}
}
