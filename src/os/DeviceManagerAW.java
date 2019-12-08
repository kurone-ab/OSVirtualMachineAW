package os;

import os.compiler.CompilerAW;
import pc.io.ConsoleAW;
import pc.io.NetworkAW;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.Register;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class DeviceManagerAW {
    private PriorityQueue<Integer> ioQueue = new PriorityQueue<>();// TODO: 2019-12-09 not interrupt id change to isr

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

    void setConsoleEditable(boolean editable){
        MainBoard.consoleAW.setEditable(editable);
    }

    ConsoleAW getConsoleAW() {
        return MainBoard.consoleAW;
    }

    NetworkAW getNetworkAW() {
        return MainBoard.networkAW;
    }
}
