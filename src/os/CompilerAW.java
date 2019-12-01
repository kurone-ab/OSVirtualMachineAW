package os;

import global.DuplicateVariableException;
import global.IllegalFormatException;
import global.IllegalInstructionException;
import pc.mainboard.cpu.CentralProcessingUnit;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilerAW {
	private static final String allocate = "allocate", staticData = "static", main = "start", assignment = "assn",
			imports = "import", function = "func", use = "use", as = "as", annotation = "/--";
	private static final Pattern number_pattern = Pattern.compile("[0-9]+");
	private static final Pattern alpha_pattern = Pattern.compile("[a-zA-Z]+");
	private static final Pattern fnc_pattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*\\.?)+\\([a-zA-Z0-9_, ]*\\)");
	private static final Pattern fn_name_pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*\\(");
	private static final Pattern var_pattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*\\.?)+");
	private static final Pattern parameter_pattern = Pattern.compile("\\([a-zA-Z0-9.,_ ]+\\)");
	private static final Pattern parameter_element_pattern = Pattern.compile("[a-zA-Z0-9._]+");
	private static int commandLine, heapAddress, dataAddress;
	private static HashMap<String, Integer> class_variables;
	private static HashMap<String, CompilerAW> class_instances;
	private static ArrayList<Integer> code, data;
	boolean isMain;
	private Scanner scanner;
	private HashMap<String, Integer> instance_variables, functions;
	private HashMap<String, CompilerAW> importModules, instance_instances;
	private int size;


	public CompilerAW(String sentence) {
		this.scanner = new Scanner(sentence);
		this.instance_variables = new HashMap<>();
		this.functions = new HashMap<>();
		this.importModules = new HashMap<>();
		this.size = 0;
	}

	public void initialize() {
		class_instances = new HashMap<>();
		class_variables = new HashMap<>();
		code = new ArrayList<>();
		data = new ArrayList<>();
		commandLine = heapAddress = dataAddress = 0;
	}

	public void parse() throws IllegalInstructionException, IllegalFormatException, DuplicateVariableException {// TODO: 2019-11-28 main의 주소를 pc로 세팅
		while (this.scanner.hasNextLine()) {
			String line = this.scanner.nextLine();
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
					CompilerAW compilerAW = new CompilerAW(OperatingSystem.fileManagerAW.getFile(filename));
					compilerAW.parse();
					this.importModules.put(filename, compilerAW);
					break;
				case staticData:
					next = tokenizer.nextToken();
					if (var_pattern.matcher(next).matches()) {
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
								break;
							} else throw new IllegalFormatException();
						} else {//assignment only
							class_variables.put(next, dataAddress++);
							data.add(0);
						}
					} else if (next.equals(use)) {// TODO: 2019-12-01 not yet
					} else throw new IllegalInstructionException();
				case assignment:
					next = tokenizer.nextToken();
					if (var_pattern.matcher(next).matches()) {// TODO: 2019-11-28 use sta & ldpi, ldni
						if (tokenizer.hasMoreTokens()) {//assignment and initialize
							String operator = tokenizer.nextToken();
							if (operator.equals("=")) {
								String value = tokenizer.nextToken();
								if (number_pattern.matcher(value).matches()) {
									class_variables.put(next, commandLine++);
									data.add(Integer.parseInt(value));
								} else if (var_pattern.matcher(value).matches()) {
									Integer adr = class_variables.get(next);
									if (adr == null) throw new IllegalFormatException();
									class_variables.put(next, commandLine++);
									data.add(data.get(adr));
								}
								break;
							} else throw new IllegalFormatException();
						} else {//assignment only
							class_variables.put(next, commandLine++);
							data.add(0);
						}
					} else if (next.equals(use)) {// TODO: 2019-12-01 not yet
					} else throw new IllegalInstructionException();
					break;
				case use://this is not static
					CompilerAW compiler = this.importModules.get(tokenizer.nextToken());
					if (compiler != null)
						if (tokenizer.nextToken().equals(as)) {
							String instance = tokenizer.nextToken();
							if (class_instances.containsKey(instance)) throw new DuplicateVariableException();
							class_instances.put(instance, compiler);
							if (var_pattern.matcher(instance).matches()) {
								this.instance_variables.put(instance, heapAddress++);
								int instruction = CentralProcessingUnit.Instruction.NEW.ordinal() << 24;
								code.add(instruction);
							} else throw new IllegalFormatException();
						} else throw new IllegalFormatException();
					else throw new IllegalFormatException();
					break;
				case function:
					HashMap<String, Integer> local_variables = new HashMap<>();
					HashMap<String, CompilerAW> local_instances = new HashMap<>();
					next = tokenizer.nextToken();
					if (fnc_pattern.matcher(next).matches()) {
						Matcher matcher = fn_name_pattern.matcher(next);
						matcher.find();
						String fn_name = matcher.group().replace("(", "");
						this.functions.put(fn_name, code.size());
						if (fn_name.equals(main)) {
							this.isMain = true;
							int instruction = CentralProcessingUnit.Instruction.FNC.ordinal() << 24;
							code.add(instruction);
							instruction = CentralProcessingUnit.Instruction.JMP.ordinal() << 24;
							instruction += this.functions.get(main) + 2;
							code.add(instruction);
						}
						line = this.scanner.nextLine();
						matcher = parameter_pattern.matcher(next);
						if (matcher.find()) {
							String s = matcher.group();
							Matcher m = alpha_pattern.matcher(s);
							while (m.find()) {
								local_variables.put(m.group(), commandLine++);
							}
						}
						while (!line.equals("}")) {
							tokenizer = new StringTokenizer(line);
							String store_target = tokenizer.nextToken();// TODO: 2019-12-01 first check if or while
							if (var_pattern.matcher(store_target).matches()) {
								if (tokenizer.hasMoreTokens()) {//assignment and initialize
									String operator = tokenizer.nextToken();
									if (operator.equals("=")) {
										String value = tokenizer.nextToken();
										if (number_pattern.matcher(value).matches()) {
											this.loadInteger(value);
										} else if (var_pattern.matcher(value).matches()) {//this is variable
											this.loadVariable(value, local_variables, local_instances);
										} else if (fnc_pattern.matcher(value).matches()) {//this is function call
											this.functionCall(value, local_variables, local_instances);
										}
										while (tokenizer.hasMoreTokens()) {
											operator = tokenizer.nextToken();
											switch (operator) {
												case "+":
													this.computeOperand(CentralProcessingUnit.Instruction.ADD.ordinal(), CentralProcessingUnit.Instruction.SUB.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												case "-":
													this.computeOperand(CentralProcessingUnit.Instruction.SUB.ordinal(), CentralProcessingUnit.Instruction.ADD.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												case "*":
													this.computeOperand(CentralProcessingUnit.Instruction.MUL.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												case "/":
													this.computeOperand(CentralProcessingUnit.Instruction.DIV.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												case "&":
													this.computeOperand(CentralProcessingUnit.Instruction.AND.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												case "|":
													this.computeOperand(CentralProcessingUnit.Instruction.OR.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												case "~":
													this.computeOperand(CentralProcessingUnit.Instruction.NOT.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												case "^":
													this.computeOperand(CentralProcessingUnit.Instruction.XOR.ordinal(), tokenizer.nextToken(), next, local_variables);
													break;
												default:
													throw new IllegalFormatException();
											}
										}
										int instruction = CentralProcessingUnit.Instruction.STA.ordinal() << 24;
										Integer adr = class_variables.get(store_target);
										if (adr == null) {
											Integer integer = local_variables.putIfAbsent(store_target, commandLine);
											if (integer == null) commandLine++;
											adr = local_variables.get(store_target);
											instruction += (1 << 20);
										}
										instruction += adr;
										code.add(instruction);
									} else throw new IllegalFormatException();
								} else local_variables.put(next, commandLine++);//assignment only
							} else if (store_target.equals(use)) {
								compiler = this.importModules.get(tokenizer.nextToken());
								if (compiler != null)
									if (tokenizer.nextToken().equals(as)) {
										String instance = tokenizer.nextToken();
										local_instances.put(instance, compiler);
										if (var_pattern.matcher(instance).matches()) {
											local_variables.put(instance, heapAddress++);
											int instruction = CentralProcessingUnit.Instruction.NEW.ordinal() << 24;
											code.add(instruction);
										} else throw new IllegalFormatException();
									} else throw new IllegalFormatException();
								else throw new IllegalFormatException();
							} else if (store_target.equals("irpt")) {
								int instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << 24;
								String id = tokenizer.nextToken();
								if (number_pattern.matcher(id).matches()) {
									instruction += Integer.parseInt(id);
									code.add(instruction);
								} else throw new IllegalFormatException();
							} else if (store_target.equals("if")) {// TODO: 2019-11-28 complete if statement
								int instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << 24;
								String id = tokenizer.nextToken();
								if (number_pattern.matcher(id).matches()) {
									instruction += Integer.parseInt(id);
									code.add(instruction);
								} else throw new IllegalFormatException();
							} else if (store_target.equals("while")) {// TODO: 2019-11-28 complete while statement
								int instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << 24;
								String id = tokenizer.nextToken();
								if (number_pattern.matcher(id).matches()) {
									instruction += Integer.parseInt(id);
									code.add(instruction);
								} else throw new IllegalFormatException();
							} else throw new IllegalFormatException();

						}
						// TODO: 2019-11-28 rtn사용 
					} else throw new IllegalFormatException();
				case annotation:
					break;
				default:
					throw new IllegalInstructionException();
			}
		}
	}


	private int decode(String instruction) throws IllegalInstructionException {
		for (CentralProcessingUnit.Instruction inst : CentralProcessingUnit.Instruction.values()) {
			if (inst.name().equals(instruction)) return inst.ordinal();
		}
		throw new IllegalInstructionException();
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

	private void computeOperand(int operation, int alternative, String operand, String next, HashMap<String, Integer> local_variables) throws IllegalFormatException {
		if (number_pattern.matcher(operand).matches()) {
			int x = Integer.parseInt(operand);
			if (x >= 0) {
				int instruction = operation << 24;
				instruction += x;
				code.add(instruction);
			} else {
				int instruction = alternative << 24;
				instruction += -x;
				code.add(instruction);
			}
		} else extractPattern(operation, operand, next, local_variables);
	}

	private void computeOperand(int operation, String operand, String next, HashMap<String, Integer> local_variables) throws IllegalFormatException {
		if (number_pattern.matcher(operand).matches()) {
			int x = Integer.parseInt(operand);
			if (x >= 0) {
				int instruction = operation << 24;
				instruction += x;
				code.add(instruction);
			} else throw new IllegalFormatException();
		} else extractPattern(operation, operand, next, local_variables);
	}

	private void extractPattern(int operation, String operand, String next, HashMap<String, Integer> local_variables) throws IllegalFormatException {
		if (var_pattern.matcher(operand).matches()) {
			Integer adr = local_variables.get(next);
			if (adr == null) adr = class_variables.get(next);
			if (adr == null) throw new IllegalFormatException();
			int instruction = operation << 24;
			instruction += adr;
			code.add(instruction);
		}
	}

	private void loadVariable(String variable, HashMap<String, Integer> local_variables,
	                          HashMap<String, CompilerAW> local_instances) throws IllegalFormatException {
		int instruction = CentralProcessingUnit.Instruction.LDA.ordinal() << 24;
		StringTokenizer tokenizer = new StringTokenizer(variable, ".");
		int heap = 0;
		CompilerAW compilerAW = this;
		while (tokenizer.countTokens() > 1) {
			String instance = tokenizer.nextToken();
			Integer adr = local_variables.get(instance);
			if (adr == null) {
				adr = this.instance_variables.get(instance);
				if (adr == null) {
					adr = class_variables.get(instance);
					if (adr == null) {
						throw new IllegalFormatException();
					} else {
						compilerAW = class_instances.get(instance);
						heap += adr;
					}
				} else {
					compilerAW = compilerAW.instance_instances.get(instance);
					heap += adr;
				}
			} else {
				compilerAW = local_instances.get(instance);
				heap += adr;
			}
		}
		Integer adr = class_variables.get(variable);
		if (adr == null) {
			adr = local_variables.get(variable);
			if (adr == null) throw new IllegalFormatException();
			instruction += (1 << 20);
		}
		instruction += adr;
		code.add(instruction);
	}

	private void loadInteger(String value) {
		int x = Integer.parseInt(value);
		if (x < 0) {
			int instruction = CentralProcessingUnit.Instruction.LDNI.ordinal() << 24;
			instruction += -x;
			code.add(instruction);
		} else {
			int instruction = CentralProcessingUnit.Instruction.LDPI.ordinal() << 24;
			instruction += x;
			code.add(instruction);
		}
	}

	private void functionCall(String value, HashMap<String, Integer> local_variables,
	                          HashMap<String, CompilerAW> local_instances) throws IllegalFormatException {
		int instruction = CentralProcessingUnit.Instruction.FNC.ordinal() << 24;
		this.code.add(instruction);
		Matcher matcher = parameter_pattern.matcher(value);
		if (matcher.find()) {//function has parameter
			String parameters = matcher.group();
			matcher = parameter_element_pattern.matcher(parameters);
			int i = 0;
			while (matcher.find()) {
				String parameter = matcher.group();
				if (var_pattern.matcher(parameter).matches())
					this.loadVariable(parameter, local_variables, local_instances);//parameter is variable
				else if (number_pattern.matcher(parameter).matches())
					this.loadInteger(value);//parameter is integer
				else if (fnc_pattern.matcher(parameter).matches())
					this.functionCall(value, local_variables, local_instances);
				this.code.add(instruction);
				instruction = CentralProcessingUnit.Instruction.STP.ordinal() << 24;
				instruction += (1 << 20) + i;
				this.code.add(instruction);
			}
			instruction = CentralProcessingUnit.Instruction.JMP.ordinal() << 24;
			matcher = fn_name_pattern.matcher(value);
			matcher.find();
			String fn_name = matcher.group().replace("(", "");
			instruction += this.functions.get(fn_name);
			code.add(instruction);
		}
	}

}
