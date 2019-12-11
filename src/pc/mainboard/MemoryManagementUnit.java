package pc.mainboard;

import os.ActivationRecord;
import os.compiler.CompilerAW;
import os.Instance;
import pc.mainboard.cpu.Register;

import static pc.mainboard.cpu.Register.CSR;
import static pc.mainboard.cpu.Register.SP;

public class MemoryManagementUnit {
    public static final int NORMAL = 0, ABNORMAL = 1;
    private static RandomAccessMemory ram;

    public void dataFetch() {
        int segment = (Register.MAR.data >>> CompilerAW.segment_bit) & 0x0000000f;
        int correction = (Register.MAR.data >>> CompilerAW.correction_bit) & 0x000000ff;
        int mar = Register.MAR.data;
        Register.MAR.data = Register.MAR.data & 0x00000fff;
        switch (segment) {
            case CompilerAW.dataSegment:
                ram.data_segment_fetch();
                break;
            case CompilerAW.stackSegment:
                ram.stack_segment_fetch();
                break;
            case CompilerAW.heapSegment:
                ram.heap_segment_fetch();
                break;
            case CompilerAW.constant:
                Register.MBR.data = mar & 0x000fffff;
                break;
            case CompilerAW.abnormal:
                int recover = Register.HSR.data;
                Register.HSR.data = correction;
                ram.heap_segment_fetch();
                Register.HSR.data = recover;
                break;
        }
    }

    public int dataFetch(int address, int sp, int csr, int hsr) throws IllegalAccessException {
        int segment = (address >>> CompilerAW.segment_bit) & 0x0000000f;
        int correction = (address >>> CompilerAW.correction_bit) & 0x000000ff;
        int adr = address & 0x00000fff;
        switch (segment) {
            case CompilerAW.dataSegment:
                return ram.data_segment_fetch(sp, adr);
            case CompilerAW.stackSegment:
                return ram.stack_segment_fetch(sp, csr, adr);
            case CompilerAW.heapSegment:
                return ram.heap_segment_fetch(sp, hsr, adr);
            case CompilerAW.constant:
                return address & 0x000fffff;
            case CompilerAW.abnormal:
                return ram.heap_segment_fetch(sp, correction, adr);
            default: throw new IllegalAccessException();
        }
    }

    public void dataFetch(int mode) {
        if (mode == ABNORMAL) {
            CSR.data--;
            this.dataFetch();
            CSR.data++;
        } else this.dataFetch();
    }

    public void dataStore() {
        int segment = (Register.MAR.data >>> CompilerAW.segment_bit) & 0x0000000f;
        int correction = (Register.MAR.data >>> CompilerAW.correction_bit) & 0x000000ff;
        Register.MAR.data = Register.MAR.data & 0x00000fff;
        switch (segment) {
            case CompilerAW.dataSegment:
                ram.data_segment_store();
                break;
            case CompilerAW.stackSegment:
                ram.stack_segment_store();
                break;
            case CompilerAW.heapSegment:
                ram.heap_segment_store();
                break;
            case CompilerAW.abnormal:
                int recover = Register.HSR.data;
                Register.HSR.data = correction;
                ram.heap_segment_store();
                Register.HSR.data = recover;
                break;
        }
    }

    public void dataStore(int data, int address, int sp, int csr, int hsr) {
        int segment = (address >>> CompilerAW.segment_bit) & 0x0000000f;
        int correction = (address >>> CompilerAW.correction_bit) & 0x000000ff;
        address = address & 0x00000fff;
        switch (segment) {
            case CompilerAW.dataSegment:
                ram.data_segment_store(sp, data, address);
                break;
            case CompilerAW.stackSegment:
                ram.stack_segment_store(sp, data, address, csr);
                break;
            case CompilerAW.heapSegment:
                ram.heap_segment_store(sp, data, address, hsr);
                break;
            case CompilerAW.abnormal:
                ram.heap_segment_store(sp, data, address, correction);
                break;
        }
    }


    public void instructionFetch() {
        ram.instructionFetch();
    }

    void connect(RandomAccessMemory ram) {
        MemoryManagementUnit.ram = ram;
    }

    public void create_activation_record(ActivationRecord activationRecord) {
        ActivationRecord[] stack = ram.memory[SP.data].stack;
        for (int i = 0; i < ram.memory.length; i++) {
            if (stack[i] == null) {
                stack[i] = activationRecord;
                CSR.data = i;
                return;
            }
        }
    }

    public void eliminate_activation_record() {
        ram.memory[SP.data].stack[CSR.data] = null;
    }

    public ActivationRecord current_activation_record() {
        return ram.memory[SP.data].stack[CSR.data];
    }

    public void create_instance(Instance instance) {
        ram.memory[SP.data].heap.add(instance);
    }
}
