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
    private final LinkedList<Interrupt> inputQueue = new LinkedList<>();
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

    private class ConsoleDriver implements Driver {

        @Override
        public void input(Interrupt interrupt) {
            synchronized (this) {
                inputQueue.add(interrupt);
            }
        }

        @Override
        public void output(Interrupt interrupt) {
            Thread thread = new Thread(() -> {
                lock.lock();
                try {
                    setText(getText() + MainBoard.mmu.dataFetch(interrupt.address, interrupt.sp, interrupt.csr, interrupt.hsr) + "\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } finally {
                    InterruptServiceRoutine isr = OperatingSystem.interruptVectorTable.getISR(InterruptVectorTable.finishID);
                    isr.handle(interrupt);
                    lock.unlock();
                }
            });
            thread.start();
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
            try {
                lock.lock();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = getText();
                    StringTokenizer tokenizer = new StringTokenizer(text, "\n");
                    while (tokenizer.countTokens() > 1) tokenizer.nextToken();
                    String value = tokenizer.nextToken();
                    if (value.matches("-?[0-9]+"))
                        buffer = Integer.parseInt(value);
                    if (!inputQueue.isEmpty()) {
                        Interrupt interrupt = inputQueue.pop();
                        MainBoard.mmu.dataStore(buffer, interrupt.address, interrupt.sp, interrupt.csr, interrupt.hsr);
                        InterruptServiceRoutine isr = OperatingSystem.interruptVectorTable.getISR(InterruptVectorTable.finishID);
                        isr.handle(interrupt);
                        if (inputQueue.isEmpty()) setEditable(false);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
