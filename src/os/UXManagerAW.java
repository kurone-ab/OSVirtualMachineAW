package os;

import global.IllegalFileFormatException;
import os.compiler.CompilerAW;
import pc.io.ConsoleAW;
import pc.mainboard.MainBoard;
import pc.mainboard.cpu.CentralProcessingUnit;
import pc.mainboard.cpu.Register;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class UXManagerAW extends JFrame {
    private static final String pathString = "PATH: ", pauseString = "||", continueString = ">",
            currentString = "CURRENT PROCESS ID: ", instructionString = "CURRENT INSTRUCTION: ";
    private JPanel main;
    private JTree fileTree;
    private JLabel marValue;
    private JLabel csrValue;
    private JLabel hsrValue;
    private JLabel mbrValue;
    private JLabel spValue;
    private JLabel iroValue;
    private JLabel iraValue;
    private JLabel itrValue;
    private JLabel pcValue;
    private JLabel acValue;
    private JLabel statusValue;
    private JSpinner delay;
    private JList<ProcessAW> memory;
    private ConsoleAW console;
    private JButton execute;
    private JButton pause;
    private JLabel path;
    private JList<ProcessControlBlock> ready;
    private JList<ProcessControlBlock> wait;
    private JList<Integer> codeLine;
    private JList<Integer> dataLine;
    private JList<ActivationRecord> locals;
    private JList<Instance> instances;
    private JLabel current;
    private JLabel currentInstruction;

    private DefaultListModel<ProcessAW> processAWListModel;
    private DefaultListModel<ProcessControlBlock> readyListModel;
    private DefaultListModel<ProcessControlBlock> waitListModel;
    private DefaultListModel<Integer> codeListModel;
    private DefaultListModel<Integer> dataListModel;
    private DefaultListModel<ActivationRecord> arListModel;
    private DefaultListModel<Instance> instanceListModel;

    private FileManagerAW.FileAW<String> executableFile;

    private void createUIComponents() {
        this.execute = new JButton();
        this.execute.setEnabled(false);
        this.execute.addActionListener((e) -> {
            String priority = JOptionPane.showInputDialog("Set Process Priority.");
            int pri = 0;
            if (priority != null) if (priority.matches("[\\-0-9]+")) pri = Integer.parseInt(priority);
            Loader.load(this.executableFile, pri);
        });

        this.pause = new JButton();
        this.pause.addActionListener((e) -> {
            ClockState state = OperatingSystem.processManagerAW.changeClockState();
            if (state == ClockState.WAIT) this.pause.setText(continueString);
            else this.pause.setText(pauseString);
        });

        this.path = new JLabel();
        this.fileTree = new JTree();
        this.fileTree.addTreeSelectionListener((event) -> {
            TreePath path = event.getPath();
            StringBuilder builder = new StringBuilder();
            Object[] elements = path.getPath();
            for (int i = 1; i < elements.length - 1; i++) {
                builder.append(elements[i].toString()).append("/");
            }
            builder.append(elements[elements.length - 1].toString());
            this.path.setText(pathString + " " + elements[0].toString() + builder.toString());
            try {
                FileManagerAW.FileAW fileAW = OperatingSystem.fileManagerAW.getFile(builder.toString());
                if (fileAW.extension.equals(FileManagerAW.AWX)) {
                    this.executableFile = fileAW;
                    this.execute.setEnabled(true);
                }
            } catch (IllegalFileFormatException ex) {
                this.execute.setEnabled(false);
            }
        });
        this.delay = new JSpinner();
        this.delay.setModel(new SpinnerNumberModel(50, 50, 5000, 100));
        this.delay.addChangeListener((e) -> OperatingSystem.processManagerAW.setDelay((Integer) this.delay.getValue()));
        this.delay.setValue(100);

        this.console = OperatingSystem.deviceManagerAW.getConsoleAW();

        this.processAWListModel = new DefaultListModel<>();
        this.readyListModel = new DefaultListModel<>();
        this.waitListModel = new DefaultListModel<>();
        this.codeListModel = new DefaultListModel<>();
        this.dataListModel = new DefaultListModel<>();
        this.arListModel = new DefaultListModel<>();
        this.instanceListModel = new DefaultListModel<>();

        this.memory = new JList<>(this.processAWListModel);
        this.ready = new JList<>(this.readyListModel);
        this.wait = new JList<>(this.waitListModel);
        this.codeLine = new JList<>(this.codeListModel);
        this.dataLine = new JList<>(this.dataListModel);
        this.locals = new JList<>(this.arListModel);
        this.instances = new JList<>(this.instanceListModel);
    }

    public void on() {
        this.setContentPane(this.main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        try {
            Loader.load(OperatingSystem.fileManagerAW.getFile("system/system.awx"), 0);
        } catch (IllegalFileFormatException e) {
            e.printStackTrace();
        }
    }

    public void updateRegisters() {
        int inst = Register.IR.data >>> CompilerAW.instruction_bit;
        this.marValue.setText(String.valueOf(Register.MAR.data));
        this.csrValue.setText(String.valueOf(Register.CSR.data));
        this.hsrValue.setText(String.valueOf(Register.HSR.data));
        this.mbrValue.setText(String.valueOf(Register.MBR.data));
        this.iroValue.setText(String.valueOf(inst));
        this.iraValue.setText(String.valueOf(Register.IR.data & 0x00ffffff));
        this.spValue.setText(String.valueOf(Register.SP.data));
        this.pcValue.setText(String.valueOf(Register.PC.data));
        this.statusValue.setText(String.valueOf(Register.STATUS.data));
        this.acValue.setText(String.valueOf(Register.AC.data));
        this.itrValue.setText(String.valueOf(Register.ITR.data));
        this.currentInstruction.setText(instructionString+CentralProcessingUnit.Instruction.values()[inst].name());
    }

    public void updateFile() {
        FileManagerAW.DirectoryAW rootName = OperatingSystem.fileManagerAW.getRootDirectory();
        DefaultMutableTreeNode fileRoot = new DefaultMutableTreeNode(rootName.getName());
        this.createNode(fileRoot, rootName);
        DefaultTreeModel fileModel = new DefaultTreeModel(fileRoot);
        this.fileTree.setModel(fileModel);
    }

    public void updateMemory() {
        this.processAWListModel.clear();
        this.processAWListModel.addAll(OperatingSystem.memoryManagerAW.getLoadedProcess());
    }

    public void updateProcess(ProcessAW processAW) {
        this.codeListModel.removeAllElements();
        this.dataListModel.removeAllElements();
        this.arListModel.removeAllElements();
        this.instanceListModel.removeAllElements();
        if (processAW != null) {
            this.current.setText(currentString + processAW.pid);
            for (int i : processAW.code) this.codeListModel.addElement(i);
            for (int i : processAW.data) this.dataListModel.addElement(i);
            this.arListModel.addAll(Arrays.asList(processAW.stack));
            this.instanceListModel.addAll(processAW.heap);
        } else {
            this.currentInstruction.setText(instructionString);
            this.current.setText(currentString);
        }
    }

    public void updateReadyQueue(Collection<ProcessControlBlock> processControlBlocks) {
        this.readyListModel.clear();
        this.readyListModel.addAll(processControlBlocks);
    }

    public void updateWaitQueue(Collection<ProcessControlBlock> processControlBlocks) {
        this.waitListModel.clear();
        this.waitListModel.addAll(processControlBlocks);
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

    public void errorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "error", JOptionPane.WARNING_MESSAGE);
    }

    {
        $$$setupUI$$$();
    }
    private void $$$setupUI$$$() {
        this.createUIComponents();
        main = new JPanel();
        main.setLayout(new GridBagLayout());
        main.setBackground(new Color(-1));
        JPanel cpuPanel = new JPanel();
        cpuPanel.setLayout(new GridBagLayout());
        cpuPanel.setBackground(new Color(-1));
        cpuPanel.setForeground(new Color(-16777216));
        cpuPanel.setMaximumSize(new Dimension(450, 400));
        cpuPanel.setMinimumSize(new Dimension(450, 400));
        cpuPanel.setPreferredSize(new Dimension(450, 400));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(cpuPanel, gbc);
        cpuPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), "<CPU>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, cpuPanel.getFont())));
        JLabel mar = new JLabel();
        Font marFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, mar.getFont());
        if (marFont != null) mar.setFont(marFont);
        mar.setText("MAR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(mar, gbc);
        marValue = new JLabel();
        Font marValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, marValue.getFont());
        if (marValueFont != null) marValue.setFont(marValueFont);
        marValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(marValue, gbc);
        JLabel csr = new JLabel();
        Font csrFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, csr.getFont());
        if (csrFont != null) csr.setFont(csrFont);
        csr.setText("CSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(csr, gbc);
        csrValue = new JLabel();
        Font csrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, csrValue.getFont());
        if (csrValueFont != null) csrValue.setFont(csrValueFont);
        csrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        cpuPanel.add(csrValue, gbc);
        JLabel hsr = new JLabel();
        Font hsrFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, hsr.getFont());
        if (hsrFont != null) hsr.setFont(hsrFont);
        hsr.setText("HSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        cpuPanel.add(hsr, gbc);
        hsrValue = new JLabel();
        Font hsrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, hsrValue.getFont());
        if (hsrValueFont != null) hsrValue.setFont(hsrValueFont);
        hsrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        cpuPanel.add(hsrValue, gbc);
        JLabel mbr = new JLabel();
        Font mbrFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, mbr.getFont());
        if (mbrFont != null) mbr.setFont(mbrFont);
        mbr.setText("     MBR     ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(mbr, gbc);
        mbrValue = new JLabel();
        Font mbrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, mbrValue.getFont());
        if (mbrValueFont != null) mbrValue.setFont(mbrValueFont);
        mbrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(mbrValue, gbc);
        JLabel sp = new JLabel();
        Font spFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, sp.getFont());
        if (spFont != null) sp.setFont(spFont);
        sp.setText("SP");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(sp, gbc);
        spValue = new JLabel();
        Font spValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, spValue.getFont());
        if (spValueFont != null) spValue.setFont(spValueFont);
        spValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(spValue, gbc);
        JLabel iro = new JLabel();
        Font iroFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, iro.getFont());
        if (iroFont != null) iro.setFont(iroFont);
        iro.setText("IR ODCODE");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(iro, gbc);
        iroValue = new JLabel();
        Font iroValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, iroValue.getFont());
        if (iroValueFont != null) iroValue.setFont(iroValueFont);
        iroValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        cpuPanel.add(iroValue, gbc);
        JLabel ira = new JLabel();
        Font iraFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, ira.getFont());
        if (iraFont != null) ira.setFont(iraFont);
        ira.setText("IR ADDRESS");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        cpuPanel.add(ira, gbc);
        iraValue = new JLabel();
        Font iraValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, iraValue.getFont());
        if (iraValueFont != null) iraValue.setFont(iraValueFont);
        iraValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        cpuPanel.add(iraValue, gbc);
        JLabel itr = new JLabel();
        Font itrFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, itr.getFont());
        if (itrFont != null) itr.setFont(itrFont);
        itr.setText("ITR");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(itr, gbc);
        itrValue = new JLabel();
        Font itrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, itrValue.getFont());
        if (itrValueFont != null) itrValue.setFont(itrValueFont);
        itrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(itrValue, gbc);
        JLabel pc = new JLabel();
        Font pcFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, pc.getFont());
        if (pcFont != null) pc.setFont(pcFont);
        pc.setText("PC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(pc, gbc);
        acValue = new JLabel();
        Font acValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, acValue.getFont());
        if (acValueFont != null) acValue.setFont(acValueFont);
        acValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(acValue, gbc);
        JLabel cu = new JLabel();
        Font cuFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, cu.getFont());
        if (cuFont != null) cu.setFont(cuFont);
        cu.setText("<html><center>CONTROL</center><center>UNIT</center></html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridheight = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(cu, gbc);
        JLabel alu = new JLabel();
        Font aluFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, alu.getFont());
        if (aluFont != null) alu.setFont(aluFont);
        alu.setText("<html><center>ARITHMATIC</center><center>LOGIC</center><center>UNIT</center></html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 6;
        gbc.gridheight = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(alu, gbc);
        JPanel delayPanel = new JPanel();
        delayPanel.setLayout(new GridBagLayout());
        delayPanel.setBackground(new Color(-1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        cpuPanel.add(delayPanel, gbc);
        JLabel cdelay = new JLabel();
        Font cdelayFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, cdelay.getFont());
        if (cdelayFont != null) cdelay.setFont(cdelayFont);
        cdelay.setText("CPU delay ms");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 20);
        delayPanel.add(cdelay, gbc);
        gbc = new GridBagConstraints();
        Font delayFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, delay.getFont());
        if (delayFont != null) delay.setFont(delayFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        delayPanel.add(delay, gbc);
        JSeparator sperator = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        cpuPanel.add(sperator, gbc);
        pcValue = new JLabel();
        Font pcValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, pcValue.getFont());
        if (pcValueFont != null) pcValue.setFont(pcValueFont);
        pcValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(pcValue, gbc);
        JLabel status = new JLabel();
        Font statusFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, status.getFont());
        if (statusFont != null) status.setFont(statusFont);
        status.setText("STATUS");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridheight = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(status, gbc);
        JLabel ac = new JLabel();
        Font acFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, ac.getFont());
        if (acFont != null) ac.setFont(acFont);
        ac.setText("AC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(ac, gbc);
        statusValue = new JLabel();
        Font statusValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, statusValue.getFont());
        if (statusValueFont != null) statusValue.setFont(statusValueFont);
        statusValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridheight = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(statusValue, gbc);
        JScrollPane memoryPane = new JScrollPane();
        memoryPane.setBackground(new Color(-1));
        memoryPane.setMinimumSize(new Dimension(200, 350));
        memoryPane.setPreferredSize(new Dimension(200, 350));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        main.add(memoryPane, gbc);
        memoryPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<MEMORY>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, memoryPane.getFont())));
        memory.setBackground(new Color(-1));
        Font memoryFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, memory.getFont());
        if (memoryFont != null) memory.setFont(memoryFont);
        memory.setPreferredSize(new Dimension(170, 300));
        memoryPane.setViewportView(memory);
        JScrollPane consolePane = new JScrollPane();
        consolePane.setAutoscrolls(true);
        consolePane.setBackground(new Color(-1));
        Font consolePaneFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, consolePane.getFont());
        if (consolePaneFont != null) consolePane.setFont(consolePaneFont);
        consolePane.setMinimumSize(new Dimension(600, 310));
        consolePane.setPreferredSize(new Dimension(600, 310));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(consolePane, gbc);
        consolePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<CONSOLE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, consolePane.getFont())));
        console.setBackground(new Color(-1));
        Font consoleFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, console.getFont());
        if (consoleFont != null) console.setFont(consoleFont);
        this.console.setLineWrap(true);
        this.console.setMinimumSize(new Dimension(550, 270));
        this.console.setPreferredSize(new Dimension(550, 270));
        this.console.setVerifyInputWhenFocusTarget(true);
//        this.console.setEditable(false);
        consolePane.setViewportView(this.console);
        Font executeFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 15, execute.getFont());
        if (executeFont != null) execute.setFont(executeFont);
        execute.setText("EXECUTE");
        Font pauseFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 15, pause.getFont());
        if (pauseFont != null) pause.setFont(pauseFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.ipadx = 100;
        main.add(execute, gbc);
        pause.setText(pauseString);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.ipadx = 30;
        main.add(pause, gbc);
        current = new JLabel();
        current.setText(currentString);
        Font currentFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 15, current.getFont());
        if (currentFont != null) current.setFont(currentFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(current, gbc);
        currentInstruction = new JLabel();
        currentInstruction.setText(instructionString);
        Font currentInstructionFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 15, currentInstruction.getFont());
        if (currentInstructionFont != null) currentInstruction.setFont(currentInstructionFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(currentInstruction, gbc);
        JScrollPane filePane = new JScrollPane();
        filePane.setAutoscrolls(true);
        filePane.setBackground(new Color(-1));
        filePane.setMinimumSize(new Dimension(200, 400));
        filePane.setPreferredSize(new Dimension(200, 400));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(filePane, gbc);
        filePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<FILE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, filePane.getFont())));
        fileTree.setBackground(new Color(-1));
        Font fileTreeFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, fileTree.getFont());
        if (fileTreeFont != null) fileTree.setFont(fileTreeFont);
        fileTree.setMinimumSize(new Dimension(170, 350));
        fileTree.setPreferredSize(new Dimension(170, 350));
        fileTree.setToggleClickCount(2);
        filePane.setViewportView(fileTree);
        Font pathFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 15, path.getFont());
        if (pathFont != null) path.setFont(pathFont);
        path.setText(pathString);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(path, gbc);
        JScrollPane readyPane = new JScrollPane();
        readyPane.setBackground(new Color(-1));
        readyPane.setMinimumSize(new Dimension(400, 400));
        readyPane.setPreferredSize(new Dimension(400, 400));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(readyPane, gbc);
        readyPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<READY QUEUE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, readyPane.getFont())));
        Font readyFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, ready.getFont());
        if (readyFont != null) ready.setFont(readyFont);
        ready.setBackground(new Color(-1));
        ready.setMinimumSize(new Dimension(370, 350));
        ready.setPreferredSize(new Dimension(370, 350));
        readyPane.setViewportView(ready);
        JScrollPane waitPane = new JScrollPane();
        waitPane.setBackground(new Color(-1));
        waitPane.setMinimumSize(new Dimension(300, 400));
        waitPane.setPreferredSize(new Dimension(300, 400));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(waitPane, gbc);
        waitPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<WAIT QUEUE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, waitPane.getFont())));
        Font waitFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, wait.getFont());
        if (waitFont != null) wait.setFont(waitFont);
        wait.setBackground(new Color(-1));
        wait.setMinimumSize(new Dimension(270, 350));
        wait.setPreferredSize(new Dimension(280, 350));
        waitPane.setViewportView(wait);
        JTabbedPane currentProcess = new JTabbedPane();
        Font currentProcessFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, currentProcess.getFont());
        if (currentProcessFont != null) currentProcess.setFont(currentProcessFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(currentProcess, gbc);
        currentProcess.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<CURRENT PROCESS>", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, currentProcess.getFont())));
        currentProcess.setMaximumSize(new Dimension(400, 300));
        currentProcess.setPreferredSize(new Dimension(400, 300));
        JScrollPane codePane = new JScrollPane();
        currentProcess.addTab("CODE", codePane);
        Font codeLineFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, codeLine.getFont());
        if (codeLineFont != null) codeLine.setFont(codeLineFont);
        codePane.setViewportView(codeLine);
        JScrollPane dataPane = new JScrollPane();
        currentProcess.addTab("DATA", dataPane);
        Font dataLineFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, codeLine.getFont());
        if (dataLineFont != null) dataLine.setFont(dataLineFont);
        dataLine.setBackground(new Color(-1));
        dataPane.setViewportView(dataLine);
        JScrollPane stackPane = new JScrollPane();
        currentProcess.addTab("STACK", stackPane);
        Font localsFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, codeLine.getFont());
        if (localsFont != null) locals.setFont(localsFont);
        locals.setBackground(new Color(-1));
        stackPane.setViewportView(locals);
        JScrollPane heapPane = new JScrollPane();
        currentProcess.addTab("HEAP", heapPane);
        Font instancesFont = this.$$$getFont$$$("JetBrains Mono", -1, 13, codeLine.getFont());
        if (instancesFont != null) instances.setFont(instancesFont);
        instances.setBackground(new Color(-1));
        heapPane.setViewportView(instances);
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

}
