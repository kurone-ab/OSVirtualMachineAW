package os;

import global.IllegalFileFormatException;
import os.compiler.CompilerAW;
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
    private static final String pathString = "PATH: ", pauseString = "||", continueString = ">", currentString = "CURRENT PROCESS ID: ";
    private JPanel main;
    private JScrollPane filePane;
    private JTree fileTree;
    private JPanel cpuPanel;
    private JLabel mar;
    private JLabel marValue;
    private JLabel csr;
    private JLabel csrValue;
    private JLabel hsr;
    private JLabel hsrValue;
    private JLabel mbr;
    private JLabel mbrValue;
    private JLabel sp;
    private JLabel spValue;
    private JLabel iro;
    private JLabel iroValue;
    private JLabel ira;
    private JLabel iraValue;
    private JLabel itr;
    private JLabel itrValue;
    private JLabel pc;
    private JLabel pcValue;
    private JLabel ac;
    private JLabel acValue;
    private JLabel status;
    private JLabel statusValue;
    private JLabel cu;
    private JLabel alu;
    private JPanel delayPanel;
    private JLabel cdelay;
    private JSpinner delay;
    private JSeparator sperator;
    private JList<ProcessAW> memory;
    private JScrollPane memoryPane;
    private JScrollPane consolePane;
    private JTextArea console;
    private JButton execute;
    private JButton pause;
    private JLabel path;
    private JList<ProcessControlBlock> ready;
    private JScrollPane readyPane;
    private JScrollPane waitPane;
    private JList<ProcessControlBlock> wait;
    private JTabbedPane currentProcess;
    private JList<Integer> codeLine;
    private JList<Integer> dataLine;
    private JList<ActivationRecord> locals;
    private JList<Instance> instances;
    private JScrollPane codePane;
    private JScrollPane dataPane;
    private JScrollPane stackPane;
    private JScrollPane heapPane;
    private JLabel current;

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
            if (priority != null) if (priority.matches("[0-9]+")) pri = Integer.parseInt(priority);
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
        this.delay.setModel(new SpinnerNumberModel(0, 0, 1000, 100));
        this.delay.addChangeListener((e) -> OperatingSystem.processManagerAW.setDelay((Integer) this.delay.getValue()));
        this.delay.setValue(500);

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
        this.fileTree.setModel(fileModel);
    }

    public void updateMemory() {
        this.processAWListModel.clear();
        this.processAWListModel.addAll(OperatingSystem.memoryManagerAW.getLoadedProcess());
    }

    public void updateProcess(ProcessAW processAW) {
        this.codeListModel.clear();
        this.dataListModel.clear();
        this.arListModel.clear();
        this.instanceListModel.clear();
        if (processAW != null) {
            this.current.setText(currentString + processAW.pid);
            for (int i : processAW.code) this.codeListModel.addElement(i);
            for (int i : processAW.data) this.dataListModel.addElement(i);
            this.arListModel.addAll(Arrays.asList(processAW.stack));
            this.instanceListModel.addAll(processAW.heap);
        }
    }

    public void updateReadyQueue(Collection<ProcessControlBlock> processControlBlocks) {
        this.readyListModel.clear();
        this.readyListModel.addAll(processControlBlocks);
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
        this.createUIComponents();
        main = new JPanel();
        main.setLayout(new GridBagLayout());
        main.setBackground(new Color(-1));
        cpuPanel = new JPanel();
        cpuPanel.setLayout(new GridBagLayout());
        cpuPanel.setBackground(new Color(-1));
        cpuPanel.setForeground(new Color(-16777216));
        cpuPanel.setMaximumSize(new Dimension(550, 400));
        cpuPanel.setMinimumSize(new Dimension(550, 400));
        cpuPanel.setPreferredSize(new Dimension(550, 400));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(cpuPanel, gbc);
        cpuPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), "<CPU>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, cpuPanel.getFont())));
        mar = new JLabel();
        Font marFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, mar.getFont());
        if (marFont != null) mar.setFont(marFont);
        mar.setText("MAR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(mar, gbc);
        marValue = new JLabel();
        Font marValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, marValue.getFont());
        if (marValueFont != null) marValue.setFont(marValueFont);
        marValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(marValue, gbc);
        csr = new JLabel();
        Font csrFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, csr.getFont());
        if (csrFont != null) csr.setFont(csrFont);
        csr.setText("CSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(csr, gbc);
        csrValue = new JLabel();
        Font csrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, csrValue.getFont());
        if (csrValueFont != null) csrValue.setFont(csrValueFont);
        csrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        cpuPanel.add(csrValue, gbc);
        hsr = new JLabel();
        Font hsrFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, hsr.getFont());
        if (hsrFont != null) hsr.setFont(hsrFont);
        hsr.setText("HSR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        cpuPanel.add(hsr, gbc);
        hsrValue = new JLabel();
        Font hsrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, hsrValue.getFont());
        if (hsrValueFont != null) hsrValue.setFont(hsrValueFont);
        hsrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        cpuPanel.add(hsrValue, gbc);
        mbr = new JLabel();
        Font mbrFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, mbr.getFont());
        if (mbrFont != null) mbr.setFont(mbrFont);
        mbr.setText("MBR");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(mbr, gbc);
        mbrValue = new JLabel();
        Font mbrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, mbrValue.getFont());
        if (mbrValueFont != null) mbrValue.setFont(mbrValueFont);
        mbrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(mbrValue, gbc);
        sp = new JLabel();
        Font spFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, sp.getFont());
        if (spFont != null) sp.setFont(spFont);
        sp.setText("SP");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(sp, gbc);
        spValue = new JLabel();
        Font spValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, spValue.getFont());
        if (spValueFont != null) spValue.setFont(spValueFont);
        spValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(spValue, gbc);
        iro = new JLabel();
        Font iroFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, iro.getFont());
        if (iroFont != null) iro.setFont(iroFont);
        iro.setText("IR ODCODE");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(iro, gbc);
        iroValue = new JLabel();
        Font iroValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, iroValue.getFont());
        if (iroValueFont != null) iroValue.setFont(iroValueFont);
        iroValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        cpuPanel.add(iroValue, gbc);
        ira = new JLabel();
        Font iraFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, ira.getFont());
        if (iraFont != null) ira.setFont(iraFont);
        ira.setText("IR ADDRESS");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        cpuPanel.add(ira, gbc);
        iraValue = new JLabel();
        Font iraValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, iraValue.getFont());
        if (iraValueFont != null) iraValue.setFont(iraValueFont);
        iraValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        cpuPanel.add(iraValue, gbc);
        itr = new JLabel();
        Font itrFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, itr.getFont());
        if (itrFont != null) itr.setFont(itrFont);
        itr.setText("ITR");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(itr, gbc);
        itrValue = new JLabel();
        Font itrValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, itrValue.getFont());
        if (itrValueFont != null) itrValue.setFont(itrValueFont);
        itrValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(itrValue, gbc);
        pc = new JLabel();
        Font pcFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, pc.getFont());
        if (pcFont != null) pc.setFont(pcFont);
        pc.setText("PC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(pc, gbc);
        acValue = new JLabel();
        Font acValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, acValue.getFont());
        if (acValueFont != null) acValue.setFont(acValueFont);
        acValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.weightx = 0.1;
        cpuPanel.add(acValue, gbc);
        cu = new JLabel();
        Font cuFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, cu.getFont());
        if (cuFont != null) cu.setFont(cuFont);
        cu.setText("<html><center>CONTROL</center><center>UNIT</center></html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.gridheight = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(cu, gbc);
        alu = new JLabel();
        Font aluFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, alu.getFont());
        if (aluFont != null) alu.setFont(aluFont);
        alu.setText("<html><center>ARITHMATIC</center><center>LOGIC</center><center>UNIT</center></html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 6;
        gbc.gridheight = 4;
        gbc.weightx = 0.1;
        cpuPanel.add(alu, gbc);
        delayPanel = new JPanel();
        delayPanel.setLayout(new GridBagLayout());
        delayPanel.setBackground(new Color(-1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        cpuPanel.add(delayPanel, gbc);
        cdelay = new JLabel();
        Font cdelayFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, cdelay.getFont());
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
        Font delayFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, delay.getFont());
        if (delayFont != null) delay.setFont(delayFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 0.7;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        delayPanel.add(delay, gbc);
        sperator = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        cpuPanel.add(sperator, gbc);
        pcValue = new JLabel();
        Font pcValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, pcValue.getFont());
        if (pcValueFont != null) pcValue.setFont(pcValueFont);
        pcValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        cpuPanel.add(pcValue, gbc);
        status = new JLabel();
        Font statusFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, status.getFont());
        if (statusFont != null) status.setFont(statusFont);
        status.setText("STATUS");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridheight = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(status, gbc);
        ac = new JLabel();
        Font acFont = this.$$$getFont$$$("JetBrains Mono", -1, 20, ac.getFont());
        if (acFont != null) ac.setFont(acFont);
        ac.setText("AC");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        cpuPanel.add(ac, gbc);
        statusValue = new JLabel();
        Font statusValueFont = this.$$$getFont$$$("JetBrains Mono", -1, 25, statusValue.getFont());
        if (statusValueFont != null) statusValue.setFont(statusValueFont);
        statusValue.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridheight = 2;
        gbc.weightx = 0.1;
        cpuPanel.add(statusValue, gbc);
        memoryPane = new JScrollPane();
        memoryPane.setBackground(new Color(-1));
        memoryPane.setMinimumSize(new Dimension(250, 450));
        memoryPane.setPreferredSize(new Dimension(250, 450));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        main.add(memoryPane, gbc);
        memoryPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<MEMORY>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, memoryPane.getFont())));
        memory.setBackground(new Color(-1));
        Font memoryFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, memory.getFont());
        if (memoryFont != null) memory.setFont(memoryFont);
        memory.setPreferredSize(new Dimension(225, 400));
        memoryPane.setViewportView(memory);
        consolePane = new JScrollPane();
        consolePane.setAutoscrolls(true);
        consolePane.setBackground(new Color(-1));
        Font consolePaneFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, consolePane.getFont());
        if (consolePaneFont != null) consolePane.setFont(consolePaneFont);
        consolePane.setMinimumSize(new Dimension(830, 400));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(consolePane, gbc);
        consolePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<CONSOLE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, consolePane.getFont())));
        console = new JTextArea();
        console.setBackground(new Color(-1));
        Font consoleFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, console.getFont());
        if (consoleFont != null) console.setFont(consoleFont);
        console.setLineWrap(false);
        console.setMinimumSize(new Dimension(800, 350));
        console.setPreferredSize(new Dimension(10, 350));
        console.setVerifyInputWhenFocusTarget(true);
        consolePane.setViewportView(console);
        Font executeFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, execute.getFont());
        if (executeFont != null) execute.setFont(executeFont);
        execute.setText("EXECUTE");
        Font pauseFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, execute.getFont());
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
        Font currentFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, execute.getFont());
        if (currentFont != null) current.setFont(currentFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        main.add(current, gbc);
        filePane = new JScrollPane();
        filePane.setAutoscrolls(true);
        filePane.setBackground(new Color(-1));
        filePane.setMinimumSize(new Dimension(250, 450));
        filePane.setPreferredSize(new Dimension(250, 450));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(filePane, gbc);
        filePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<FILE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 18, filePane.getFont())));
        fileTree.setBackground(new Color(-1));
        Font fileTreeFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, fileTree.getFont());
        if (fileTreeFont != null) fileTree.setFont(fileTreeFont);
        fileTree.setMinimumSize(new Dimension(225, 400));
        fileTree.setPreferredSize(new Dimension(225, 400));
        fileTree.setToggleClickCount(2);
        filePane.setViewportView(fileTree);
        Font pathFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, path.getFont());
        if (pathFont != null) path.setFont(pathFont);
        path.setText(pathString);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        main.add(path, gbc);
        readyPane = new JScrollPane();
        readyPane.setBackground(new Color(-1));
        readyPane.setMinimumSize(new Dimension(530, 430));
        readyPane.setPreferredSize(new Dimension(530, 430));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(readyPane, gbc);
        readyPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<READY QUEUE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, readyPane.getFont())));
        Font readyFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, fileTree.getFont());
        if (readyFont != null) ready.setFont(readyFont);
        ready.setBackground(new Color(-1));
        ready.setMinimumSize(new Dimension(500, 400));
        ready.setPreferredSize(new Dimension(500, 400));
        readyPane.setViewportView(ready);
        waitPane = new JScrollPane();
        waitPane.setBackground(new Color(-1));
        waitPane.setMinimumSize(new Dimension(330, 430));
        waitPane.setPreferredSize(new Dimension(330, 430));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(waitPane, gbc);
        waitPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<WAIT QUEUE>", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, waitPane.getFont())));
        Font waitFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, fileTree.getFont());
        if (waitFont != null) wait.setFont(waitFont);
        wait.setBackground(new Color(-1));
        wait.setMinimumSize(new Dimension(300, 400));
        wait.setPreferredSize(new Dimension(300, 400));
        waitPane.setViewportView(wait);
        currentProcess = new JTabbedPane();
        Font currentProcessFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, currentProcess.getFont());
        if (currentProcessFont != null) currentProcess.setFont(currentProcessFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(currentProcess, gbc);
        currentProcess.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "<CURRENT PROCESS>", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 20, currentProcess.getFont())));
        currentProcess.setMaximumSize(new Dimension(500, 400));
        currentProcess.setPreferredSize(new Dimension(500, 400));
        codePane = new JScrollPane();
        currentProcess.addTab("CODE", codePane);
        Font codeLineFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, codeLine.getFont());
        if (codeLineFont != null) codeLine.setFont(codeLineFont);
        codePane.setViewportView(codeLine);
        dataPane = new JScrollPane();
        currentProcess.addTab("DATA", dataPane);
        Font dataLineFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, codeLine.getFont());
        if (dataLineFont != null) dataLine.setFont(dataLineFont);
        dataLine.setBackground(new Color(-1));
        dataPane.setViewportView(dataLine);
        stackPane = new JScrollPane();
        currentProcess.addTab("STACK", stackPane);
        Font localsFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, codeLine.getFont());
        if (localsFont != null) locals.setFont(localsFont);
        locals.setBackground(new Color(-1));
        stackPane.setViewportView(locals);
        heapPane = new JScrollPane();
        currentProcess.addTab("HEAP", heapPane);
        Font instancesFont = this.$$$getFont$$$("JetBrains Mono", -1, 15, codeLine.getFont());
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

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

}
