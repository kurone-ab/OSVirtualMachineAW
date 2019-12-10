package pc.io;

import os.*;
import pc.mainboard.MainBoard;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkAW implements IODevice {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final NetworkDriver networkDriver = new NetworkDriver();
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

    private class NetworkDriver implements Driver {
        @Override
        public void input(Interrupt interrupt) {
            Thread thread = new Thread(() -> {
                try {
                    lock.lock();
                    boolean repeat = true;
                    while (repeat) {
                        try {
                            Scanner scanner = new Scanner(inputStream);
                            if (scanner.hasNextLine()) {
                                String tx = scanner.nextLine();
                                if (tx.matches("-?[0-9]+")) {
                                    MainBoard.mmu.dataStore(Integer.parseInt(tx), interrupt.address, interrupt.sp, interrupt.csr, interrupt.hsr);
                                    repeat = false;
                                }
                            }
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    InterruptServiceRoutine isr = OperatingSystem.interruptVectorTable.getISR(InterruptVectorTable.finishID);
                    isr.handle(interrupt);
                    lock.unlock();
                }
            });
            thread.start();
        }

        @Override
        public void output(Interrupt interrupt) {
            Thread thread = new Thread(() -> {
                try {
                    lock.lock();
                    PrintWriter writer = new PrintWriter(outputStream);
                    writer.println(MainBoard.mmu.dataFetch(interrupt.address, interrupt.sp, interrupt.csr, interrupt.hsr));
                    writer.flush();
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
