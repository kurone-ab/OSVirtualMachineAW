package os;

import os.compiler.CompilerAW;
import pc.io.ConsoleAW;
import pc.io.NetworkAW;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeviceManagerAW {
    private ConcurrentLinkedQueue<Integer> ioQueue = new ConcurrentLinkedQueue<>();

    boolean hasMoreInterrupt() {
        return !this.ioQueue.isEmpty();
    }

    void putInterrupt() {
        ioQueue.add(Register.ITR.data >>> CompilerAW.instruction_bit);
    }

    void putInterrupt(int id) {
        ioQueue.add(id);
    }

    int getInterrupt() {
        return ioQueue.poll();
    }

    ConsoleAW getConsoleAW() {
        return MainBoard.consoleAW;
    }

    NetworkAW getNetworkAW() {
        return MainBoard.networkAW;
    }
}
