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
    private static final Font baseFont = new Font("JetBrains Mono", Font.PLAIN, 15);
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
            if (priority != null) if (priority.matches("-?[0-9]+")) pri = Integer.parseInt(priority);
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
        this.currentInstruction.setText(instructionString + CentralProcessingUnit.Instruction.values()[inst].name());
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
        SwingUtilities.invokeLater(() -> {
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
        });
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
        cpuPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), "<CPU>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, baseFont.deriveFont(Font.BOLD, 18f)));
        JLabel mar = new JLabel();
        mar.setFont(baseFont);
        mar.setText("MAR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(mar, gbc);
        marValue = new JLabel();
        marValue.setFont(baseFont.deriveFont(20f));
        marValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(marValue, gbc);
        JLabel csr = new JLabel();
        csr.setFont(baseFont);
        csr.setText("CSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(csr, gbc);
        csrValue = new JLabel();
        csrValue.setFont(baseFont.deriveFont(20f));
        csrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        cpuPanel.add(csrValue, gbc);
        JLabel hsr = new JLabel();
        hsr.setFont(baseFont);
        hsr.setText("HSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        cpuPanel.add(hsr, gbc);
        hsrValue = new JLabel();
        hsrValue.setFont(baseFont.deriveFont(20f));
        hsrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        cpuPanel.add(hsrValue, gbc);
        JLabel mbr = new JLabel();
        mbr.setFont(baseFont);
        mbr.setText("     MBR     ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(mbr, gbc);
        mbrValue = new JLabel();
        mbrValue.setFont(baseFont.deriveFont(20f));
        mbrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(mbrValue, gbc);
        JLabel sp = new JLabel();
        sp.setFont(baseFont);
        sp.setText("SP");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(sp, gbc);
        spValue = new JLabel();
        spValue.setFont(baseFont.deriveFont(20f));
        spValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(spValue, gbc);
        JLabel iro = new JLabel();
        iro.setFont(baseFont);
        iro.setText("IR ODCODE");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(iro, gbc);
        iroValue = new JLabel();
        iroValue.setFont(baseFont.deriveFont(20f));
        iroValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        cpuPanel.add(iroValue, gbc);
        JLabel ira = new JLabel();
        ira.setFont(baseFont);
        ira.setText("IR ADDRESS");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        cpuPanel.add(ira, gbc);
        iraValue = new JLabel();
        iraValue.setFont(baseFont.deriveFont(20f));
        iraValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        cpuPanel.add(iraValue, gbc);
        JLabel itr = new JLabel();
        itr.setFont(baseFont);
        itr.setText("ITR");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(itr, gbc);
        itrValue = new JLabel();
        itrValue.setFont(baseFont.deriveFont(20f));
        itrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(itrValue, gbc);
        JLabel pc = new JLabel();
        pc.setFont(baseFont);
        pc.setText("PC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(pc, gbc);
        acValue = new JLabel();
        acValue.setFont(baseFont.deriveFont(20f));
        acValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(acValue, gbc);
        JLabel cu = new JLabel();
        cu.setFont(baseFont.deriveFont(20f));
        cu.setText("<html><center>CONTROL</center><center>UNIT</center></html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridheight = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(cu, gbc);
        JLabel alu = new JLabel();
        alu.setFont(baseFont.deriveFont(20f));
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
        cdelay.setFont(baseFont);
        cdelay.setText("CPU delay ms");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 20);
        delayPanel.add(cdelay, gbc);
        gbc = new GridBagConstraints();
        delay.setFont(baseFont.deriveFont(13f));
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
        pcValue.setFont(baseFont.deriveFont(20f));
        pcValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(pcValue, gbc);
        JLabel status = new JLabel();
        status.setFont(baseFont);
        status.setText("STATUS");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridheight = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(status, gbc);
        JLabel ac = new JLabel();
        ac.setFont(baseFont);
        ac.setText("AC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(ac, gbc);
        statusValue = new JLabel();
        statusValue.setFont(baseFont.deriveFont(20f));
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
        memoryPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<MEMORY>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, baseFont.deriveFont(Font.BOLD, 18f)));
        memory.setBackground(new Color(-1));
        memory.setFont(baseFont.deriveFont(13f));
        memory.setPreferredSize(new Dimension(170, 300));
        memoryPane.setViewportView(memory);
        JScrollPane consolePane = new JScrollPane();
        consolePane.setAutoscrolls(true);
        consolePane.setBackground(new Color(-1));
        consolePane.setFont(baseFont.deriveFont(13f));
        consolePane.setMinimumSize(new Dimension(600, 310));
        consolePane.setPreferredSize(new Dimension(600, 310));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(consolePane, gbc);
        consolePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<CONSOLE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, baseFont.deriveFont(Font.BOLD, 18f)));
        console.setBackground(new Color(-1));
        console.setFont(baseFont.deriveFont(13f));
        this.console.setLineWrap(true);
        this.console.setMinimumSize(new Dimension(550, 270));
        this.console.setPreferredSize(new Dimension(550, 270));
        this.console.setVerifyInputWhenFocusTarget(true);
        this.console.setEditable(false);
        consolePane.setViewportView(this.console);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        execute.setFont(baseFont.deriveFont(Font.BOLD));
        execute.setText("EXECUTE");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.ipadx = 100;
        main.add(execute, gbc);
        pause.setFont(baseFont.deriveFont(Font.BOLD));
        pause.setText(pauseString);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.ipadx = 30;
        main.add(pause, gbc);
        current = new JLabel();
        current.setText(currentString);
        current.setFont(baseFont.deriveFont(Font.BOLD));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(current, gbc);
        currentInstruction = new JLabel();
        currentInstruction.setText(instructionString);
        currentInstruction.setFont(baseFont.deriveFont(Font.BOLD));
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
        filePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<FILE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, baseFont.deriveFont(Font.BOLD, 18f)));
        fileTree.setBackground(new Color(-1));
        fileTree.setFont(baseFont);
        fileTree.setMinimumSize(new Dimension(170, 350));
        fileTree.setPreferredSize(new Dimension(170, 350));
        fileTree.setToggleClickCount(2);
        filePane.setViewportView(fileTree);
        path.setFont(baseFont.deriveFont(Font.BOLD));
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
        readyPane.setMinimumSize(new Dimension(330, 400));
        readyPane.setPreferredSize(new Dimension(330, 400));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(readyPane, gbc);
        readyPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<READY QUEUE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, baseFont.deriveFont(Font.BOLD, 18f)));
        ready.setFont(baseFont.deriveFont(13f));
        ready.setBackground(new Color(-1));
        ready.setMinimumSize(new Dimension(300, 350));
        ready.setPreferredSize(new Dimension(300, 350));
        readyPane.setViewportView(ready);
        JScrollPane waitPane = new JScrollPane();
        waitPane.setBackground(new Color(-1));
        waitPane.setMinimumSize(new Dimension(330, 400));
        waitPane.setPreferredSize(new Dimension(330, 400));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(waitPane, gbc);
        waitPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<WAIT QUEUE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, baseFont.deriveFont(Font.BOLD, 18f)));
        wait.setBackground(new Color(-1));
        wait.setMinimumSize(new Dimension(300, 350));
        wait.setPreferredSize(new Dimension(300, 350));
        waitPane.setViewportView(wait);
        JTabbedPane currentProcess = new JTabbedPane();
        currentProcess.setFont(baseFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(currentProcess, gbc);
        currentProcess.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<CURRENT PROCESS>", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, baseFont.deriveFont(Font.BOLD, 18f)));
        currentProcess.setMaximumSize(new Dimension(300, 300));
        currentProcess.setPreferredSize(new Dimension(300, 300));
        JScrollPane codePane = new JScrollPane();
        currentProcess.addTab("CODE", codePane);
        codeLine.setFont(baseFont.deriveFont(13f));
        codePane.setViewportView(codeLine);
        JScrollPane dataPane = new JScrollPane();
        currentProcess.addTab("DATA", dataPane);
        dataLine.setFont(baseFont.deriveFont(13f));
        dataLine.setBackground(new Color(-1));
        dataPane.setViewportView(dataLine);
        JScrollPane stackPane = new JScrollPane();
        currentProcess.addTab("STACK", stackPane);
        locals.setFont(baseFont.deriveFont(13f));
        locals.setBackground(new Color(-1));
        stackPane.setViewportView(locals);
        JScrollPane heapPane = new JScrollPane();
        currentProcess.addTab("HEAP", heapPane);
        instances.setFont(baseFont.deriveFont(13f));
        instances.setBackground(new Color(-1));
        heapPane.setViewportView(instances);
    }

}
