package pc.io;

import os.DeviceType;
import os.Driver;
import os.IODevice;
import pc.mainboard.MainBoard;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.StringTokenizer;

public class ConsoleAW extends JTextArea implements IODevice {
	private final Object lock = new Object();
	private final ConsoleDriver consoleDriver = new ConsoleDriver();
	private int buffer;

	public ConsoleAW() {
		this.addKeyListener(new InputListener());
	}

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
		public void input(int sp, int address, int csr, int hsr) {
			synchronized (lock){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				MainBoard.mmu.dataStore(buffer, address, sp, hsr, csr);
			}
		}

		@Override
		public void output(int sp, int address, int csr, int hsr) {
			try {
				setText(getText()+MainBoard.mmu.dataFetch(address, sp, hsr, csr) +"\n");
			} catch (IllegalAccessException ignored) {
			}
		}
	}

	private class InputListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_ENTER){
				synchronized (lock){
					String text = getText();
					StringTokenizer tokenizer = new StringTokenizer(text, "\n");
					while (tokenizer.countTokens()>1) tokenizer.nextToken();
					String value = tokenizer.nextToken();
					if (value.matches("[\\-0-9]+"))
						buffer = Integer.parseInt(value);
					lock.notify();
				}
			}
		}
	}
}
