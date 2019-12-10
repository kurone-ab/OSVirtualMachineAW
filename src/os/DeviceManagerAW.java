package os;

import pc.io.ConsoleAW;
import pc.io.NetworkAW;
import pc.mainboard.MainBoard;

import java.util.PriorityQueue;

public class DeviceManagerAW {
    private PriorityQueue<Interrupt> interruptQueue = new PriorityQueue<>();// TODO: 2019-12-09 not interrupt id change to isr

    boolean hasMoreInterrupt() {
        return !this.interruptQueue.isEmpty();
    }

    void putInterrupt(Interrupt interrupt) {
        interruptQueue.add(interrupt);
    }

    Interrupt getInterrupt() {
        return interruptQueue.poll();
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
