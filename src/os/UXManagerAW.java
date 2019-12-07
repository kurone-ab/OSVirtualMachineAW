package os;

import global.IllegalFileFormatException;
import os.compiler.CompilerAW;
import pc.mainboard.cpu.Register;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Arrays;

public class UXManagerAW extends JFrame {
    private JTextArea console;
    private JTree fileList;
    private JLabel mar;
    private JLabel csr;
    private JLabel hsr;
    private JLabel mbr;
    private JLabel sp;
    private JLabel iro;
    private JLabel status;
    private JLabel ira;
    private JLabel pc;
    private JLabel ac;
    private JLabel itr;
    private JLabel csrValue;
    private JLabel marValue;
    private JLabel hsrValue;
    private JLabel mbrValue;
    private JLabel spValue;
    private JLabel iroValue;
    private JLabel iraValue;
    private JLabel statusValue;
    private JLabel pcValue;
    private JLabel acValue;
    private JLabel itrValue;
    private JLabel cu;
    private JLabel alu;
    private JTree memoryList;
    private JList ioList;
    private JLabel con;
    private JLabel fi;
    private JLabel memo;
    private JLabel cpu;
    private JList readyQueue;
    private JLabel ready;
    private JLabel wait;
    private JLabel io;
    private JList waitQueue;
    private JPanel main;
    private JSpinner delay;
    private JLabel cdelay;
    private JLabel path;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        this.fileList = new JTree();
        this.fileList.addTreeSelectionListener((event) -> {
            TreePath path = event.getPath();
            StringBuilder builder = new StringBuilder();
            Object[] elements = path.getPath();
            for (int i = 1; i < elements.length - 1; i++) {
                builder.append(elements[i].toString()).append("/");
            }
            builder.append(elements[elements.length - 1].toString());
            System.out.println(builder.toString());
            try {
                FileManagerAW.FileAW fileAW = OperatingSystem.fileManagerAW.getFile(builder.toString());
                if (fileAW.extension.equals(FileManagerAW.AWX)) {
                    Loader.load(fileAW);
                }
            } catch (IllegalFileFormatException ignored) {
            }
        });
        this.memoryList = new JTree();
        this.delay = new JSpinner();
        this.delay.setModel(new SpinnerNumberModel(0, 0, 1000, 100));
        this.delay.addChangeListener((e) -> OperatingSystem.processManagerAW.setDelay((Integer) this.delay.getValue()));
    }

    public void on() {
        this.setContentPane(this.main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public void updateRegisters() {
        this.marValue.setText(String.valueOf(Register.MAR.data));
        this.csrValue.setText(String.valueOf(Register.CSR.data));
        this.hsrValue.setText(String.valueOf(Register.HSR.data));
        this.mbrValue.setText(String.valueOf(Register.MBR.data));
        this.iroValue.setText(String.valueOf(Register.IR.data >>> CompilerAW.instruction_bit));
        this.iraValue.setText(String.valueOf(Register.IR.data & 0x00ffffff));
        this.spValue.setText(String.valueOf(Register.SP.data));
        this.pcValue.setText(String.valueOf(Register.PC.data));
        this.statusValue.setText(String.valueOf(Register.STATUS.data));
        this.acValue.setText(String.valueOf(Register.AC.data));
        this.itrValue.setText(String.valueOf(Register.ITR.data));
    }

    public void updateFile() {
        FileManagerAW.DirectoryAW rootName = OperatingSystem.fileManagerAW.getRootDirectory();
        DefaultMutableTreeNode fileRoot = new DefaultMutableTreeNode(rootName.getName());
        this.createNode(fileRoot, rootName);
        DefaultTreeModel fileModel = new DefaultTreeModel(fileRoot);
        this.fileList.setModel(fileModel);
    }

    public void updateMemory() {
        DefaultMutableTreeNode memoryRoot = new DefaultMutableTreeNode("memoryList");
        for (ProcessAW processAW : OperatingSystem.memoryManagerAW.getLoadedProcess())
            this.createNode(memoryRoot, processAW);
        DefaultTreeModel memoryModel = new DefaultTreeModel(memoryRoot);
        this.memoryList.setModel(memoryModel);
    }

    private void createNode(DefaultMutableTreeNode node, ProcessAW processAW) {
        DefaultMutableTreeNode codeNode = new DefaultMutableTreeNode("code");
        codeNode.add(new DefaultMutableTreeNode(Arrays.toString(processAW.code)));
        DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode("data");
        dataNode.add(new DefaultMutableTreeNode(Arrays.toString(processAW.data)));
        DefaultMutableTreeNode stackNode = new DefaultMutableTreeNode("stack");
        for (ActivationRecord ar : processAW.stack) {
            if (ar == null) break;
            DefaultMutableTreeNode arNode = new DefaultMutableTreeNode(ar);
            stackNode.add(arNode);
        }
        DefaultMutableTreeNode heapNode = new DefaultMutableTreeNode("heap");
        for (Instance instance : processAW.heap) {
            DefaultMutableTreeNode instanceNode = new DefaultMutableTreeNode(instance);
            stackNode.add(instanceNode);
        }
        node.add(codeNode);
        node.add(dataNode);
        node.add(stackNode);
        node.add(heapNode);
    }

    private void createNode(DefaultMutableTreeNode node, FileManagerAW.DirectoryAW nodeName) {
        for (FileManagerAW.DirectoryAW dir : nodeName.directoryAWS) {
            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(dir);
            this.createNode(temp, dir);
            node.add(temp);
        }
        for (int i : nodeName.fileAWS) {
            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(OperatingSystem.fileManagerAW.getFile(i));
            node.add(temp);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridBagLayout());
        main.setBackground(new Color(-1));
        console = new JTextArea();
        console.setEnabled(true);
        console.setLineWrap(true);
        console.setToolTipText("this is console");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(console, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setBackground(new Color(-1));
        Font panel1Font = this.$$$getFont$$$(null, -1, 30, panel1.getFont());
        if (panel1Font != null) panel1.setFont(panel1Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 200;
        main.add(panel1, gbc);
        mar = new JLabel();
        Font marFont = this.$$$getFont$$$(null, -1, 25, mar.getFont());
        if (marFont != null) mar.setFont(marFont);
        mar.setText("MAR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(mar, gbc);
        csr = new JLabel();
        Font csrFont = this.$$$getFont$$$(null, -1, 25, csr.getFont());
        if (csrFont != null) csr.setFont(csrFont);
        csr.setText("CSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(csr, gbc);
        hsr = new JLabel();
        Font hsrFont = this.$$$getFont$$$(null, -1, 25, hsr.getFont());
        if (hsrFont != null) hsr.setFont(hsrFont);
        hsr.setText("HSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(hsr, gbc);
        mbr = new JLabel();
        Font mbrFont = this.$$$getFont$$$(null, -1, 25, mbr.getFont());
        if (mbrFont != null) mbr.setFont(mbrFont);
        mbr.setHorizontalAlignment(0);
        mbr.setHorizontalTextPosition(0);
        mbr.setText("MBR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(mbr, gbc);
        csrValue = new JLabel();
        Font csrValueFont = this.$$$getFont$$$(null, -1, 40, csrValue.getFont());
        if (csrValueFont != null) csrValue.setFont(csrValueFont);
        csrValue.setHorizontalAlignment(0);
        csrValue.setHorizontalTextPosition(0);
        csrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(csrValue, gbc);
        hsrValue = new JLabel();
        Font hsrValueFont = this.$$$getFont$$$(null, -1, 40, hsrValue.getFont());
        if (hsrValueFont != null) hsrValue.setFont(hsrValueFont);
        hsrValue.setHorizontalAlignment(0);
        hsrValue.setHorizontalTextPosition(0);
        hsrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(hsrValue, gbc);
        mbrValue = new JLabel();
        Font mbrValueFont = this.$$$getFont$$$(null, -1, 40, mbrValue.getFont());
        if (mbrValueFont != null) mbrValue.setFont(mbrValueFont);
        mbrValue.setHorizontalAlignment(0);
        mbrValue.setHorizontalTextPosition(0);
        mbrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(mbrValue, gbc);
        marValue = new JLabel();
        Font marValueFont = this.$$$getFont$$$(null, -1, 40, marValue.getFont());
        if (marValueFont != null) marValue.setFont(marValueFont);
        marValue.setHorizontalAlignment(0);
        marValue.setHorizontalTextPosition(0);
        marValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(marValue, gbc);
        sp = new JLabel();
        Font spFont = this.$$$getFont$$$(null, -1, 25, sp.getFont());
        if (spFont != null) sp.setFont(spFont);
        sp.setText("SP");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(sp, gbc);
        spValue = new JLabel();
        Font spValueFont = this.$$$getFont$$$(null, -1, 40, spValue.getFont());
        if (spValueFont != null) spValue.setFont(spValueFont);
        spValue.setHorizontalAlignment(0);
        spValue.setHorizontalTextPosition(0);
        spValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(spValue, gbc);
        iro = new JLabel();
        Font iroFont = this.$$$getFont$$$(null, -1, 25, iro.getFont());
        if (iroFont != null) iro.setFont(iroFont);
        iro.setText("IR OPCODE");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(iro, gbc);
        ira = new JLabel();
        Font iraFont = this.$$$getFont$$$(null, -1, 25, ira.getFont());
        if (iraFont != null) ira.setFont(iraFont);
        ira.setText("IR ADDRESS");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(ira, gbc);
        iroValue = new JLabel();
        Font iroValueFont = this.$$$getFont$$$(null, -1, 40, iroValue.getFont());
        if (iroValueFont != null) iroValue.setFont(iroValueFont);
        iroValue.setHorizontalAlignment(0);
        iroValue.setHorizontalTextPosition(0);
        iroValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(iroValue, gbc);
        iraValue = new JLabel();
        Font iraValueFont = this.$$$getFont$$$(null, -1, 40, iraValue.getFont());
        if (iraValueFont != null) iraValue.setFont(iraValueFont);
        iraValue.setHorizontalAlignment(0);
        iraValue.setHorizontalTextPosition(0);
        iraValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(iraValue, gbc);
        pc = new JLabel();
        Font pcFont = this.$$$getFont$$$(null, -1, 25, pc.getFont());
        if (pcFont != null) pc.setFont(pcFont);
        pc.setText("PC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(pc, gbc);
        ac = new JLabel();
        Font acFont = this.$$$getFont$$$(null, -1, 25, ac.getFont());
        if (acFont != null) ac.setFont(acFont);
        ac.setText("AC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(ac, gbc);
        pcValue = new JLabel();
        Font pcValueFont = this.$$$getFont$$$(null, -1, 40, pcValue.getFont());
        if (pcValueFont != null) pcValue.setFont(pcValueFont);
        pcValue.setHorizontalAlignment(0);
        pcValue.setHorizontalTextPosition(0);
        pcValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 4;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(pcValue, gbc);
        acValue = new JLabel();
        Font acValueFont = this.$$$getFont$$$(null, -1, 40, acValue.getFont());
        if (acValueFont != null) acValue.setFont(acValueFont);
        acValue.setHorizontalAlignment(0);
        acValue.setHorizontalTextPosition(0);
        acValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.gridheight = 4;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(acValue, gbc);
        itr = new JLabel();
        Font itrFont = this.$$$getFont$$$(null, -1, 25, itr.getFont());
        if (itrFont != null) itr.setFont(itrFont);
        itr.setText("ITR");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(itr, gbc);
        itrValue = new JLabel();
        Font itrValueFont = this.$$$getFont$$$(null, -1, 40, itrValue.getFont());
        if (itrValueFont != null) itrValue.setFont(itrValueFont);
        itrValue.setHorizontalAlignment(0);
        itrValue.setHorizontalTextPosition(0);
        itrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(itrValue, gbc);
        cu = new JLabel();
        Font cuFont = this.$$$getFont$$$(null, -1, 50, cu.getFont());
        if (cuFont != null) cu.setFont(cuFont);
        cu.setText("CU");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(cu, gbc);
        alu = new JLabel();
        Font aluFont = this.$$$getFont$$$(null, -1, 50, alu.getFont());
        if (aluFont != null) alu.setFont(aluFont);
        alu.setText("ALU");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.gridheight = 4;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(alu, gbc);
        statusValue = new JLabel();
        Font statusValueFont = this.$$$getFont$$$(null, -1, 40, statusValue.getFont());
        if (statusValueFont != null) statusValue.setFont(statusValueFont);
        statusValue.setHorizontalAlignment(0);
        statusValue.setHorizontalTextPosition(0);
        statusValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(statusValue, gbc);
        status = new JLabel();
        Font statusFont = this.$$$getFont$$$(null, -1, 25, status.getFont());
        if (statusFont != null) status.setFont(statusFont);
        status.setText("STATUS");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.weightx = 0.2;
        gbc.weighty = 0.2;
        panel1.add(status, gbc);
        con = new JLabel();
        Font conFont = this.$$$getFont$$$(null, -1, 15, con.getFont());
        if (conFont != null) con.setFont(conFont);
        con.setText("<console>");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        main.add(con, gbc);
        cpu = new JLabel();
        Font cpuFont = this.$$$getFont$$$(null, -1, 15, cpu.getFont());
        if (cpuFont != null) cpu.setFont(cpuFont);
        cpu.setText("<cpu>");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        main.add(cpu, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 100;
        gbc.insets = new Insets(0, 15, 0, 15);
        main.add(scrollPane1, gbc);
        memoryList.setToolTipText("memory");
        scrollPane1.setViewportView(memoryList);
        memo = new JLabel();
        Font memoFont = this.$$$getFont$$$(null, -1, 15, memo.getFont());
        if (memoFont != null) memo.setFont(memoFont);
        memo.setText("<memory>");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        main.add(memo, gbc);
        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 100;
        gbc.insets = new Insets(0, 15, 0, 15);
        main.add(scrollPane2, gbc);
        fileList.setToolTipText("file list");
        scrollPane2.setViewportView(fileList);
        fi = new JLabel();
        Font fiFont = this.$$$getFont$$$(null, -1, 15, fi.getFont());
        if (fiFont != null) fi.setFont(fiFont);
        fi.setText("<file>");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        main.add(fi, gbc);
        wait = new JLabel();
        Font waitFont = this.$$$getFont$$$(null, -1, 15, wait.getFont());
        if (waitFont != null) wait.setFont(waitFont);
        wait.setText("<wait queue>");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        main.add(wait, gbc);
        final JScrollPane scrollPane3 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(scrollPane3, gbc);
        readyQueue = new JList();
        readyQueue.setBackground(new Color(-1));
        scrollPane3.setViewportView(readyQueue);
        ready = new JLabel();
        Font readyFont = this.$$$getFont$$$(null, -1, 15, ready.getFont());
        if (readyFont != null) ready.setFont(readyFont);
        ready.setText("<ready queue>");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        main.add(ready, gbc);
        io = new JLabel();
        Font ioFont = this.$$$getFont$$$(null, -1, 15, io.getFont());
        if (ioFont != null) io.setFont(ioFont);
        io.setText("<IO device>");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        main.add(io, gbc);
        final JScrollPane scrollPane4 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(scrollPane4, gbc);
        ioList = new JList();
        ioList.setBackground(new Color(-1));
        scrollPane4.setViewportView(ioList);
        final JScrollPane scrollPane5 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(scrollPane5, gbc);
        waitQueue = new JList();
        waitQueue.setBackground(new Color(-1));
        scrollPane5.setViewportView(waitQueue);
        Font delayFont = this.$$$getFont$$$(null, -1, 12, delay.getFont());
        if (delayFont != null) delay.setFont(delayFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 5;
        main.add(delay, gbc);
        cdelay = new JLabel();
        Font cdelayFont = this.$$$getFont$$$(null, -1, 15, cdelay.getFont());
        if (cdelayFont != null) cdelay.setFont(cdelayFont);
        cdelay.setText("clock delay");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        main.add(cdelay, gbc);
        path = new JLabel();
        Font pathFont = this.$$$getFont$$$(null, -1, 15, path.getFont());
        if (pathFont != null) path.setFont(pathFont);
        path.setText("PATH: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 15, 0, 0);
        main.add(path, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

}
