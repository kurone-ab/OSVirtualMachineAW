package pc.io;

import os.*;
import pc.mainboard.MainBoard;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConsoleAW extends JTextArea implements IODevice {
	private final ReentrantLock lock = new ReentrantLock(true);
	private final ConsoleDriver consoleDriver = new ConsoleDriver();
	private final LinkedList<int[]> inputQueue = new LinkedList<>();
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
		public void input(int pid, int sp, int address, int csr, int hsr) {
			try{
				lock.lock();
				inputQueue.add(new int[]{pid, sp, address, csr, hsr});
			}finally {
				lock.unlock();
			}
		}

		@Override
		public void output(int pid, int sp, int address, int csr, int hsr) {
			try {
				lock.lock();
				setText(getText() + MainBoard.mmu.dataFetch(address, sp, csr, hsr) + "\n");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void connect() {

		}

		@Override
		public void disconnect() {

		}
	}

	private class InputListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String text = getText();
				StringTokenizer tokenizer = new StringTokenizer(text, "\n");
				while (tokenizer.countTokens() > 1) tokenizer.nextToken();
				String value = tokenizer.nextToken();
				if (value.matches("-?[0-9]+"))
					buffer = Integer.parseInt(value);
				if (!inputQueue.isEmpty()) {
					int[] parameters = inputQueue.pop();
					MainBoard.mmu.dataStore(buffer, parameters[2], parameters[1], parameters[3], parameters[4]);
					InterruptServiceRoutine isr = OperatingSystem.interruptVectorTable.getInterrupt(InterruptVectorTable.finishID);
					isr.set(parameters[0], 0, 0, 0, 0);
					isr.handle();
					if (inputQueue.isEmpty()) setEditable(false);
				}
			}
		}
	}
}
