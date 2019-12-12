package os.compiler;

import global.*;
import os.FileManagerAW;
import os.InterruptVectorTable;
import os.OperatingSystem;
import pc.mainboard.cpu.CentralProcessingUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static os.InterruptVectorTable.*;

public class CompilerAW {
	public static final int instruction_bit = 24, segment_bit = 20, correction_bit = 12, parameter_bit = 16,
			heapSegment = 2, stackSegment = 1, dataSegment = 0, constant = 4, abnormal = 5;
	private static final String allocate = "allocate", staticData = "static", assignment = "assn",
	imports = "import", function = "func", use = "use", as = "as", annotation = "/--", returns = "return",
	ifs = "if", whiles = "while", exit = "exit", big = ">", small = "<", equal = "==", self = "self",
	print = "print", input = "input", send = "send", receive = "receive", connect = "connect", disconnect = "disconnect";
	private static final Pattern number_pattern = Pattern.compile("-?[0-9]+");
	private static final Pattern alpha_pattern = Pattern.compile("[a-zA-Z]+");
	private static final Pattern fnc_pattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*\\.?)+\\([a-zA-Z\\-0-9_, ]*\\)");
	private static final Pattern fn_name_pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*\\(");
	private static final Pattern var_pattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*\\.?)+");
	private static final Pattern parameter_pattern = Pattern.compile("\\([a-zA-Z\\-0-9.,_ ]+\\)");
	private static final Pattern parameter_element_pattern = Pattern.compile("-?[a-zA-Z0-9._]+");
	private static final Pattern main_pattern = Pattern.compile("main\\(\\)");
	private static int heapAddress, dataAddress;
	private static HashMap<String, Integer> class_variables;
	private static HashMap<String, CompilerAW> class_instances, importModules;
	private HashMap<String, Integer> functions;
	private HashMap<String, Integer> instance_variables;
	private Scanner scanner;
	private HashMap<String, Integer> local_size;
	private HashMap<String, CompilerAW> instance_instances;
	private ArrayList<String> wait_until_use;
	private int instanceAddress;
	private boolean isMain, previousMain, necessity;
	private FileManagerAW.FileAW<String> fileAW;
	private String default_directory;
	static final String main = "main";
	static ArrayList<Integer> code, data;
	int size;
	int new_main_line;


	public CompilerAW(FileManagerAW.FileAW<String> fileAW) throws IllegalFileFormatException {
		if (!fileAW.extension.equals(FileManagerAW.AWX)) throw new IllegalFileFormatException();
		this.fileAW  = fileAW;
		this.scanner = new Scanner(this.fileAW.content);
		this.instance_instances = new HashMap<>();
		this.instance_variables = new HashMap<>();
		this.local_size = new HashMap<>();
		this.functions = new HashMap<>();
		this.wait_until_use = new ArrayList<>();
		this.size = this.instanceAddress = 0;
		this.isMain = false;
		this.previousMain = this.necessity = true;
	}

	public void initialize() throws NotMainAWXFileException {
		class_instances = new HashMap<>();
		class_variables = new HashMap<>();
		importModules = new HashMap<>();
		String filename = this.fileAW.filename;
		StringTokenizer tokenizer = new StringTokenizer(filename, ".");
		StringBuilder builder = new StringBuilder();
		while (tokenizer.countTokens()>1){
			builder.append(tokenizer.nextToken());
		}
		importModules.put(builder.toString(), this);
		code = new ArrayList<>();
		data = new ArrayList<>();
		heapAddress = dataAddress = 0;
		this.isMain = main_pattern.matcher(this.fileAW.content).find();
		if (!this.isMain) throw new NotMainAWXFileException("You cannot run a file without the main method.");
		this.instance_variables.put(self, heapAddress++);
		this.default_directory = this.fileAW.directory;
	}

	public void parse() throws IllegalInstructionException, IllegalFormatException, DuplicateVariableException {
		while (this.scanner.hasNextLine()) {
			String line = this.scanner.nextLine();
			if (line.isEmpty()) continue;
			StringTokenizer tokenizer = new StringTokenizer(line);
			String next;
			switch (tokenizer.nextToken()) {
				case allocate:
					next = tokenizer.nextToken();
					if (number_pattern.matcher(next).matches()) this.size = Integer.parseInt(next);
					else throw new IllegalInstructionException();
					break;
				case imports:
					String filename = tokenizer.nextToken();
					if (!importModules.containsKey(filename)) {
						try {
							CompilerAW compilerAW = new CompilerAW(OperatingSystem.fileManagerAW.getFile(this.default_directory+"/"+filename+".awx"));
							compilerAW.parse();
							importModules.put(filename, compilerAW);
						} catch (IllegalFileFormatException e) {
							throw new IllegalFormatException();
						}
					}
					break;
				case staticData:
					next = tokenizer.nextToken();
					if (next.equals(use)) {// TODO: 2019-12-01 not yet
						CompilerAW compiler = importModules.get(tokenizer.nextToken());
						this.useCommand(tokenizer, compiler, class_variables, class_instances);
					} else if (var_pattern.matcher(next).matches()) {
						if (tokenizer.hasMoreTokens()) {//assignment and initialize
							String operator = tokenizer.nextToken();
							if (operator.equals("=")) {
								String value = tokenizer.nextToken();
								if (number_pattern.matcher(value).matches()) {
									class_variables.put(next, dataAddress++);
									data.add(Integer.parseInt(value));
								} else if (var_pattern.matcher(value).matches()) {
									Integer adr = class_variables.get(next);
									if (adr == null) throw new IllegalFormatException();
									class_variables.put(next, dataAddress++);
									data.add(data.get(adr));
								}
							} else throw new IllegalFormatException();
						} else {//assignment only
							class_variables.put(next, dataAddress++);
							data.add(0);
						}
					} else throw new IllegalInstructionException();
					break;
				case assignment:
					StringBuilder builder = new StringBuilder();
					String s = tokenizer.nextToken();
					builder.append(s).append(" ");
					while (tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken();
						builder.append(token).append(" ");
					}
					this.instance_variables.putIfAbsent(s, this.instanceAddress++);
					this.wait_until_use.add(builder.toString());
					break;
				case use://this is not static
					tokenizer.nextToken();
					tokenizer.nextToken();
					s = tokenizer.nextToken();
					this.instance_variables.putIfAbsent(s, this.instanceAddress++);
					this.wait_until_use.add(line);
					break;
				case function:
					if (this.isMain && this.necessity) {
						this.functions.put(main, code.size());
						this.useCommand(this.instance_variables, this.instance_instances, self, this);
						this.new_main_line = code.size();
						this.necessity = false;
					}
					String fn_name;
					int stackAddress = 0;
					int instruction;
					int position = 0;
					HashMap<String, Integer> local_variables = new HashMap<>();
					HashMap<String, CompilerAW> local_instances = new HashMap<>();
					next = tokenizer.nextToken();
					if (fnc_pattern.matcher(next).matches()) {
						Matcher matcher = fn_name_pattern.matcher(next);
						matcher.find();
						fn_name = matcher.group().replace("(", "");
						if (!fn_name.equals(main)) {
							int pos = code.size();
							if (this.previousMain && this.isMain) pos += 1;
							this.functions.put(fn_name, pos);
						} else {
							this.previousMain = false;
							position = code.size() + 1;
						}
						line = this.scanner.nextLine();
						matcher = parameter_pattern.matcher(next);
						if (matcher.find()) {
							String st = matcher.group();
							Matcher m = alpha_pattern.matcher(st);
							while (m.find()) {
								local_variables.put(m.group(), stackAddress++);
							}
						}
						String statement = null;
						int startLine = 0;
						ArrayList<Integer> condition = new ArrayList<>();
						while (!line.contains(returns) && !line.contains(exit)) {
							if (line.isEmpty()) {
								line = this.scanner.nextLine();
								break;
							}
							tokenizer = new StringTokenizer(line);
							String store_target = tokenizer.nextToken();// TODO: 2019-12-01 first check if or while
							if (store_target.equals("}")) {
								if (statement == null) throw new IllegalFormatException();
								if (statement.equals(whiles)) code.addAll(condition);
								instruction = CentralProcessingUnit.Instruction.JMP.ordinal() << instruction_bit;
								int pos = code.size();
								if (this.previousMain && this.isMain) pos++;
								instruction += pos;
								code.add(startLine, instruction);
							} else if (store_target.equals(use)) {
								String module = tokenizer.nextToken();
								CompilerAW compiler = importModules.get(module);
								this.useCommand(tokenizer, compiler, local_variables, local_instances);
							} else if (store_target.equals(print)) {//print
								this.compileInterruptCase(tokenizer, local_variables, local_instances, printID);
							}else if (store_target.equals(input)) {//input
								this.compileInterruptCase(tokenizer, local_variables, local_instances, inputID);
							}else if (store_target.equals(send)) {//send
								this.compileInterruptCase(tokenizer, local_variables, local_instances, sendID);
							}else if (store_target.equals(receive)) {//receive
								this.compileInterruptCase(tokenizer, local_variables, local_instances, receiveID);
							}else if (store_target.equals(connect)) {//connect
								instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << instruction_bit;
								instruction += connectID;
								code.add(instruction);
							}else if (store_target.equals(disconnect)) {//disconnect
								instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << instruction_bit;
								instruction += disconnectID;
								code.add(instruction);
							} else if (store_target.equals(ifs)) {
								statement = ifs;
								this.parseStatement(tokenizer, local_variables, local_instances);
								startLine = code.size();
							} else if (store_target.equals(whiles)) {
								statement = whiles;
								startLine = code.size();
								this.parseStatement(tokenizer, local_variables, local_instances);
								for (int i = startLine; i < code.size(); i++) {
									condition.add(code.get(i));
								}
								startLine = code.size();
							} else if (var_pattern.matcher(store_target).matches()) {
								if (!class_variables.containsKey(store_target) &&
										!this.instance_variables.containsKey(store_target))
									local_variables.putIfAbsent(store_target, stackAddress++);
								if (tokenizer.hasMoreTokens()) {//assignment and initialize
									String operator = tokenizer.nextToken();
									if (operator.equals("=")) {
										String value = tokenizer.nextToken();
										this.loadClassify(value, local_variables, local_instances);
										this.arithmeticOperations(tokenizer, local_variables, local_instances);
										this.commandWithVariable(CentralProcessingUnit.Instruction.STA.ordinal() << instruction_bit, store_target, local_variables, local_instances);
									} else throw new IllegalFormatException();
								}
							} else if (fnc_pattern.matcher(store_target).matches()) {
								this.functionCall(store_target, local_variables, local_instances);
							} else if (!store_target.equals(annotation)) throw new IllegalFormatException();
							line = this.scanner.nextLine();
						}
						tokenizer = new StringTokenizer(line);
						String command = tokenizer.nextToken();
						switch (command) {
							case returns:
								if (tokenizer.hasMoreTokens()) {
									String load_target = tokenizer.nextToken();
									this.loadClassify(load_target, local_variables, local_instances);
								}
								instruction = CentralProcessingUnit.Instruction.RTN.ordinal() << instruction_bit;
								code.add(instruction);
								scanner.nextLine();
								break;
							case exit:
								code.add(CentralProcessingUnit.Instruction.HLT.ordinal() << instruction_bit);
								scanner.nextLine();
								break;
							default:
								throw new IllegalFormatException();
						}
					} else throw new IllegalFormatException();
					this.local_size.put(fn_name, local_variables.size());
					if (fn_name.equals(main)) {
						instruction = CentralProcessingUnit.Instruction.JMP.ordinal() << instruction_bit;
						instruction += position;
						code.add(this.new_main_line, instruction);
						instruction = CentralProcessingUnit.Instruction.FNC.ordinal() << instruction_bit;
						instruction += this.local_size.get(fn_name);
						code.add(position, instruction);
					}
					break;
				case annotation:
					break;
				default:
					throw new IllegalInstructionException();
			}
		}
	}

	public int stack() {
		return size;
	}

	public int[] convertCode() {
		int i = 0;
		int[] array = new int[code.size()];
		for (int a : code) array[i++] = a;
		return array;
	}

	public int[] convertData() {
		int i = 0;
		int[] array = new int[data.size()];
		for (int a : data) array[i++] = a;
		return array;
	}

	private void computeOperand(int operation, int alternative, String operand, HashMap<String, Integer> variables,
								HashMap<String, CompilerAW> instances) throws IllegalFormatException {
		int instruction = operation << instruction_bit;
		if (number_pattern.matcher(operand).matches()) {
			int x = Integer.parseInt(operand);
			if (x >= 0) {
				instruction += constant << segment_bit;
				instruction += x;
			} else {
				instruction = alternative << instruction_bit;
				instruction += constant << segment_bit;
				instruction += -x;
			}
			code.add(instruction);
		} else if (var_pattern.matcher(operand).matches())
			this.commandWithVariable(instruction, operand, variables, instances);
	}

	private void computeOperand(int operation, String operand, HashMap<String, Integer> variables,
								HashMap<String, CompilerAW> instances) throws IllegalFormatException {
		int instruction = operation << instruction_bit;
		if (number_pattern.matcher(operand).matches()) {
			int x = Integer.parseInt(operand);
			if (x >= 0) {
				instruction += constant << segment_bit;
				instruction += x;
				code.add(instruction);
			} else throw new IllegalFormatException();
		} else if (var_pattern.matcher(operand).matches())
			this.commandWithVariable(instruction, operand, variables, instances);
	}

	private void commandWithVariable(int instruction, String variable, HashMap<String, Integer> variables,
	                                 HashMap<String, CompilerAW> instances) throws IllegalFormatException {
		StringTokenizer tokenizer = new StringTokenizer(variable, ".");
		int heap = -1;
		CompilerAW compilerAW = this;
		while (tokenizer.countTokens() > 1) {
			String instance = tokenizer.nextToken();
			if (instances.containsKey(instance) && variables != this.instance_variables && variables != class_variables) {
				compilerAW = instances.get(instance);
				heap = variables.get(instance);
			} else if (compilerAW.instance_instances.containsKey(instance)) {
				heap = compilerAW.instance_variables.get(instance);
				compilerAW = compilerAW.instance_instances.get(instance);
			} else if (class_instances.containsKey(instance)) {
				compilerAW = class_instances.get(instance);
				heap = class_variables.get(instance);
			} else {
				throw new IllegalFormatException();
			}
		}
		String real_variable = tokenizer.nextToken();
		if (heap == -1) {
			int address;
			if (variables.containsKey(real_variable) && variables != this.instance_variables && variables != class_variables) {
				instruction += (stackSegment << segment_bit);
				address = variables.get(real_variable);
			} else if (this.instance_variables.containsKey(real_variable)) {
				address = this.instance_variables.get(real_variable);
				instruction += (heapSegment << segment_bit);
			} else if (class_variables.containsKey(real_variable)) {
				address = class_variables.get(real_variable);
			} else {
				throw new IllegalFormatException();
			}
			instruction += address;
		} else {
			if (compilerAW.instance_variables.containsKey(real_variable)) {
				instruction += abnormal << segment_bit;
				instruction += heap << correction_bit;
				instruction += compilerAW.instance_variables.get(real_variable);
			} else if (class_variables.containsKey(real_variable)) instruction += class_variables.get(real_variable);
			else throw new IllegalFormatException();
		}

		code.add(instruction);
	}

	private void loadInteger(String value) {
		int x = Integer.parseInt(value);
		if (x < 0) {
			int instruction = CentralProcessingUnit.Instruction.LDNI.ordinal() << instruction_bit;
			instruction += -x;
			code.add(instruction);
		} else {
			int instruction = CentralProcessingUnit.Instruction.LDPI.ordinal() << instruction_bit;
			instruction += x;
			code.add(instruction);
		}
	}

	private void functionCall(String function, HashMap<String, Integer> variables,
	                          HashMap<String, CompilerAW> instances) throws IllegalFormatException {
		Matcher matcher = fn_name_pattern.matcher(function);
		matcher.find();
		String fn_name = matcher.group().replace("(", "");
		int position = code.size();
		matcher = parameter_pattern.matcher(function);
		int i = 0;
		if (matcher.find()) {//function has parameter
			String parameters = matcher.group();
			matcher = parameter_element_pattern.matcher(parameters);
			while (matcher.find()) {
				String parameter = matcher.group();
				this.loadParameter(parameter, variables, instances);
				int instruction = CentralProcessingUnit.Instruction.STA.ordinal() << instruction_bit;
				instruction += (stackSegment << segment_bit) + i++;
				code.add(instruction);
			}
		}
		int instruction = CentralProcessingUnit.Instruction.JMP.ordinal() << instruction_bit;
		StringTokenizer tokenizer = new StringTokenizer(function, ".");
		CompilerAW compilerAW = this;
		int heap = -1;
		while (tokenizer.countTokens() > 1) {
			String instance = tokenizer.nextToken();
			if (instances.containsKey(instance)) {
				heap = variables.get(instance);
				compilerAW = instances.get(instance);
			} else if (compilerAW.instance_instances.containsKey(instance)) {
				heap = compilerAW.instance_variables.get(instance);
				compilerAW = compilerAW.instance_instances.get(instance);
			} else if (class_instances.containsKey(instance)) {
				heap = class_variables.get(instance);
				compilerAW = class_instances.get(instance);
			} else {
				throw new IllegalFormatException();
			}
		}
		instruction += compilerAW.functions.get(fn_name);
		code.add(instruction);
		instruction = CentralProcessingUnit.Instruction.FNC.ordinal() << instruction_bit;
		instruction += i << parameter_bit;
		if (heap != -1) instruction += 1 << correction_bit;
		instruction += compilerAW.local_size.get(fn_name);
		code.add(position, instruction);
		if (heap != -1) {
			instruction = CentralProcessingUnit.Instruction.SHR.ordinal() << instruction_bit;
			instruction += heap;
			code.add(position + 1, instruction);
		}
	}

	private void useCommand(StringTokenizer tokenizer, CompilerAW compiler, HashMap<String, Integer> variables,
							HashMap<String, CompilerAW> instances) throws DuplicateVariableException, IllegalFormatException {
		if (compiler != null)
			if (tokenizer.nextToken().equals(as)) {
				String instance = tokenizer.nextToken();
				if (instances.containsKey(instance)) throw new DuplicateVariableException();
				this.useCommand(variables, instances, instance, compiler);
			} else throw new IllegalFormatException();
		else throw new IllegalFormatException();
	}

	private void useCommand(HashMap<String, Integer> variables, HashMap<String, CompilerAW> instances, String instance,
							CompilerAW compiler) throws IllegalFormatException, DuplicateVariableException {
		instances.put(instance, compiler);
		if (var_pattern.matcher(instance).matches()) {// TODO: 2019-12-04
			if (!instance.equals(self)) variables.put(instance, heapAddress++);
			int instruction = CentralProcessingUnit.Instruction.NEW.ordinal() << instruction_bit;
			instruction += compiler.instance_variables.size();
			code.add(instruction);
			for (String line : compiler.wait_until_use) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				StringBuilder store_target = new StringBuilder(tokenizer.nextToken());
				if (store_target.toString().equals(use)) {
					if (store_target.toString().equals(use)) {
						CompilerAW tCompiler = importModules.get(tokenizer.nextToken());
						this.useCommand(tokenizer, tCompiler, this.instance_variables, this.instance_instances);
					}
				} else if (var_pattern.matcher(store_target.toString()).matches()) {
					if (class_variables.containsKey(store_target.toString())) throw new DuplicateVariableException();
					if (tokenizer.hasMoreTokens()) {//assignment and initialize
						String operator = tokenizer.nextToken();
						if (operator.equals("=")) {
							StringBuilder value = new StringBuilder(tokenizer.nextToken());
							if (!instance.equals(self)) {
								if (!number_pattern.matcher(store_target).matches()) store_target.insert(0,
										instance + ".");
								if (!number_pattern.matcher(value).matches()) value.insert(0, instance + ".");
							}
							this.loadClassify(value.toString(), this.instance_variables, this.instance_instances);
							while (tokenizer.hasMoreTokens()) {
								operator = tokenizer.nextToken();
								StringBuilder operand = new StringBuilder(tokenizer.nextToken());
								if (!instance.equals(self) && !number_pattern.matcher(store_target).matches())
									operand.insert(0, instance + ".");
								switch (operator) {
									case "+":
										this.computeOperand(CentralProcessingUnit.Instruction.ADD.ordinal(),
												CentralProcessingUnit.Instruction.SUB.ordinal(), operand.toString(),
												variables, instances);
										break;
									case "-":
										this.computeOperand(CentralProcessingUnit.Instruction.SUB.ordinal(),
												CentralProcessingUnit.Instruction.ADD.ordinal(), operand.toString(),
												variables, instances);
										break;
									case "*":
										this.computeOperand(CentralProcessingUnit.Instruction.MUL.ordinal(),
												operand.toString(), variables, instances);
										break;
									case "/":
										this.computeOperand(CentralProcessingUnit.Instruction.DIV.ordinal(),
												operand.toString(), variables, instances);
										break;
									case "&":
										this.computeOperand(CentralProcessingUnit.Instruction.AND.ordinal(),
												operand.toString(), variables, instances);
										break;
									case "|":
										this.computeOperand(CentralProcessingUnit.Instruction.OR.ordinal(),
												operand.toString(), variables, instances);
										break;
									case "~":
										this.computeOperand(CentralProcessingUnit.Instruction.NOT.ordinal(),
												operand.toString(), variables, instances);
										break;
									case "^":
										this.computeOperand(CentralProcessingUnit.Instruction.XOR.ordinal(),
												operand.toString(), variables, instances);
										break;
									default:
										throw new IllegalFormatException();
								}
							}
							this.commandWithVariable
									(CentralProcessingUnit.Instruction.STA.ordinal() << instruction_bit,
											store_target.toString(), variables, instances);
						} else {
							throw new IllegalFormatException();
						}
					}
				}
			}
		} else throw new IllegalFormatException();
	}

	private void loadClassify(String value, HashMap<String, Integer> variables, HashMap<String, CompilerAW> instances)
			throws IllegalFormatException {
		if (number_pattern.matcher(value).matches()) {
			this.loadInteger(value);
		} else if (var_pattern.matcher(value).matches()) {//this is variable
			this.commandWithVariable(CentralProcessingUnit.Instruction.LDA.ordinal() << instruction_bit, value, variables, instances);
		} else if (fnc_pattern.matcher(value).matches()) {//this is function call
			this.functionCall(value, variables, instances);
		} else {
			throw new IllegalFormatException();
		}
	}

	private void loadParameter(String value, HashMap<String, Integer> variables, HashMap<String, CompilerAW> instances)
			throws IllegalFormatException {
		if (number_pattern.matcher(value).matches()) {
			this.loadInteger(value);
		} else if (var_pattern.matcher(value).matches()) {//this is variable
			this.commandWithVariable(CentralProcessingUnit.Instruction.LDP.ordinal() << instruction_bit, value,
					variables, instances);
		} else throw new IllegalFormatException();
	}

	private void parseStatement(StringTokenizer tokenizer, HashMap<String, Integer> local_variables, HashMap<String
			, CompilerAW> local_instances) throws IllegalFormatException {
		String operand1 = tokenizer.nextToken();
		String operator = tokenizer.nextToken();
		String operand2 = tokenizer.nextToken();
		int pos = this.previousMain ? 3 : 4;
		switch (operator) {
			case equal:
				this.loadClassify(operand1, local_variables, local_instances);
				this.computeOperand(CentralProcessingUnit.Instruction.SUB.ordinal(),
						CentralProcessingUnit.Instruction.ADD.ordinal(), operand2, local_variables, local_instances);
				int instruction = CentralProcessingUnit.Instruction.JSZ.ordinal() << instruction_bit;
				instruction += code.size() + pos;
				code.add(instruction);
				break;
			case big:
				this.loadClassify(operand2, local_variables, local_instances);
				this.computeOperand(CentralProcessingUnit.Instruction.SUB.ordinal(),
						CentralProcessingUnit.Instruction.ADD.ordinal(), operand1, local_variables, local_instances);
				instruction = CentralProcessingUnit.Instruction.JSN.ordinal() << instruction_bit;
				instruction += code.size() + pos;
				code.add(instruction);
				break;
			case small:
				this.loadClassify(operand1, local_variables, local_instances);
				this.computeOperand(CentralProcessingUnit.Instruction.SUB.ordinal(),
						CentralProcessingUnit.Instruction.ADD.ordinal(), operand2, local_variables, local_instances);
				instruction = CentralProcessingUnit.Instruction.JSN.ordinal() << instruction_bit;
				instruction += code.size() + pos;
				code.add(instruction);
				break;
		}
	}

	public int getStartLine() {
		return this.functions.get(main);
	}

	private void arithmeticOperations(StringTokenizer tokenizer, HashMap<String, Integer> local_variables, HashMap<String
			, CompilerAW> local_instances) throws IllegalFormatException {
		while (tokenizer.hasMoreTokens()) {
			String operator = tokenizer.nextToken();
			String operand = tokenizer.nextToken();
			switch (operator) {
				case "+":
					this.computeOperand(CentralProcessingUnit.Instruction.ADD.ordinal(),
							CentralProcessingUnit.Instruction.SUB.ordinal(), operand,
							local_variables, local_instances);
					break;
				case "-":
					this.computeOperand(CentralProcessingUnit.Instruction.SUB.ordinal(),
							CentralProcessingUnit.Instruction.ADD.ordinal(), operand,
							local_variables, local_instances);
					break;
				case "*":
					this.computeOperand(CentralProcessingUnit.Instruction.MUL.ordinal(),
							operand, local_variables, local_instances);
					break;
				case "/":
					this.computeOperand(CentralProcessingUnit.Instruction.DIV.ordinal(),
							operand, local_variables, local_instances);
					break;
				case "&":
					this.computeOperand(CentralProcessingUnit.Instruction.AND.ordinal(),
							operand, local_variables, local_instances);
					break;
				case "|":
					this.computeOperand(CentralProcessingUnit.Instruction.OR.ordinal(),
							operand, local_variables, local_instances);
					break;
				case "~":
					this.computeOperand(CentralProcessingUnit.Instruction.NOT.ordinal(),
							operand, local_variables, local_instances);
					break;
				case "^":
					this.computeOperand(CentralProcessingUnit.Instruction.XOR.ordinal(),
							operand, local_variables, local_instances);
					break;
				default:
					throw new IllegalFormatException();
			}
		}
	}

	private void compileInterruptCase(StringTokenizer tokenizer, HashMap<String, Integer> local_variables, HashMap<String
			, CompilerAW> local_instances, int id) throws IllegalFormatException {
		int instruction = CentralProcessingUnit.Instruction.SITR.ordinal();
		this.computeOperand(instruction, tokenizer.nextToken(), local_variables, local_instances);
		instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << instruction_bit;
		instruction += id;
		code.add(instruction);
	}
}
