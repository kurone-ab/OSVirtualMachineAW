package os;

import global.IllegalFormatException;
import global.IllegalInstructionException;
import pc.mainboard.cpu.CentralProcessingUnit;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserAW {
	private static final String allocate = "allocate", staticData = "static", main = "start", assignment = "assn",
			imports = "import", function = "func", use = "use", as = "as", annotation = "/--";
	private static final Pattern number_pattern = Pattern.compile("[0-9]+");
	private static final Pattern alpha_pattern = Pattern.compile("[a-zA-Z]+");
	private static final Pattern fnc_pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*\\.?)+\\([a-zA-Z0-9, ?]*\\)");
	private static final Pattern fn_name_pattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*\\(");
	private static final Pattern var_pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*\\.?)+");
	private static final Pattern parameter_pattern = Pattern.compile("\\([a-zA-Z0-9, ]+\\)");
	private Scanner scanner;
	private Hashtable<String, Integer> variables, functions, instances;
	private Hashtable<String, ParserAW> importModules;
	private Vector<Integer> codeV, dataV;
	private int size, address, heap, heapAddress;
	private boolean isMain;

	public ParserAW(String sentence) {
		this.scanner = new Scanner(sentence);
		this.variables = new Hashtable<>();
		this.functions = new Hashtable<>();
		this.instances = new Hashtable<>();
		this.importModules = new Hashtable<>();
		this.codeV = new Vector<>();
		this.dataV = new Vector<>();
		this.size = address = 0;
	}

	public void parse() throws IllegalInstructionException, IllegalFormatException {// TODO: 2019-11-28 main의 주소를 pc로 세팅
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
				case staticData:
					next = tokenizer.nextToken();
					if (var_pattern.matcher(next).matches()) {
						if (tokenizer.hasMoreTokens()) {//assignment and initialize
							String operator = tokenizer.nextToken();
							if (operator.equals("=")) {
								String value = tokenizer.nextToken();
								if (number_pattern.matcher(value).matches()) {
									this.variables.put(next, this.address++);
									this.dataV.add(Integer.parseInt(value));
								} else if (var_pattern.matcher(value).matches()) {
									Object adr = this.variables.get(next);
									if (adr == null) throw new IllegalFormatException();
									this.variables.put(next, this.address++);
									this.dataV.add(this.dataV.get((int) adr));
								}
								break;
							} else throw new IllegalFormatException();
						} else {//assignment only
							this.variables.put(next, this.address++);
							this.dataV.add(0);
						}
					} else throw new IllegalInstructionException();
				case assignment:
					next = tokenizer.nextToken();
					if (var_pattern.matcher(next).matches()) {// TODO: 2019-11-28 use hds & ldpi, ldni
						if (tokenizer.hasMoreTokens()) {//assignment and initialize
							String operator = tokenizer.nextToken();
							if (operator.equals("=")) {
								String value = tokenizer.nextToken();
								if (number_pattern.matcher(value).matches()) {
									this.variables.put(next, this.address++);
									this.dataV.add(Integer.parseInt(value));
								} else if (var_pattern.matcher(value).matches()) {
									Object adr = this.variables.get(next);
									if (adr == null) throw new IllegalFormatException();
									this.variables.put(next, this.address++);
									this.dataV.add(this.dataV.get((int) adr));
								}
								break;
							} else throw new IllegalFormatException();
						} else {//assignment only
							this.variables.put(next, this.address++);
							this.dataV.add(0);
						}
					} else throw new IllegalInstructionException();
					break;
				case imports:
					String filename = tokenizer.nextToken();
					ParserAW parserAW = new ParserAW(OperatingSystem.fileManagerAW.getFile(filename));
					this.importModules.put(filename, parserAW);
					break;
				case function:
					this.address = 0;
					HashMap<String, Integer> arVariables = new HashMap<>();
					next = tokenizer.nextToken();
					if (fnc_pattern.matcher(next).matches()) {
						Matcher matcher = fn_name_pattern.matcher(next);
						matcher.find();
						String fn_name = matcher.group().replace("(", "");
						this.functions.put(fn_name, this.codeV.size());
						if (fn_name.equals(main)) this.isMain = true;
						line = this.scanner.nextLine();// TODO: 2019-11-28 파라미터를 읽어야한다.
						matcher = parameter_pattern.matcher(next);
						if (matcher.find()) {
							String s = matcher.group();
							Matcher m = alpha_pattern.matcher(s);
							while (m.find()) {
								arVariables.put(m.group(), this.address++);
							}
						}
						while (!line.equals("}")) {
							tokenizer = new StringTokenizer(line);
							String temp = tokenizer.nextToken();
							if (var_pattern.matcher(temp).matches()) {
								if (tokenizer.hasMoreTokens()) {//assignment and initialize
									String operator = tokenizer.nextToken();
									if (operator.equals("=")) {
										String value = tokenizer.nextToken();
										if (number_pattern.matcher(value).matches()) {
											int x = Integer.parseInt(value);
											if (!this.variables.containsKey(temp))
												arVariables.put(temp, this.address++);
											if (x < 0) {
												int instruction = CentralProcessingUnit.Instruction.LDNI.ordinal() << 24;
												instruction += -x;
												this.codeV.add(instruction);
											} else {
												int instruction = CentralProcessingUnit.Instruction.LDPI.ordinal() << 24;
												instruction += x;
												this.codeV.add(instruction);
											}
										} else if (var_pattern.matcher(value).matches()) {
											Object adr = arVariables.get(next);
											if (adr == null) adr = this.variables.get(next);
											if (adr == null) throw new IllegalFormatException();
											int instruction = CentralProcessingUnit.Instruction.LDA.ordinal() << 24;
											instruction += (int) adr;
											this.codeV.add(instruction);
										} else if (fnc_pattern.matcher(value).matches()) {
											int instruction = CentralProcessingUnit.Instruction.FNC.ordinal() << 24;
											matcher = fn_name_pattern.matcher(value);
											matcher.find();
											fn_name = matcher.group().replace("(", "");
											instruction += this.functions.get(fn_name);
											this.codeV.add(instruction);
											matcher = parameter_pattern.matcher(value);
											if (matcher.find()) {
												String parameters = matcher.group();
												matcher = var_pattern.matcher(parameters);
												while (matcher.find()) {
													instruction = CentralProcessingUnit.Instruction.STP.ordinal() << 24;
												}
												matcher = number_pattern.matcher(parameters);
												while (matcher.find()) {
													instruction = CentralProcessingUnit.Instruction.STP.ordinal() << 24;
												}

											}
										}
										while (tokenizer.hasMoreTokens()) {
											operator = tokenizer.nextToken();
											switch (operator) {
												case "+":
													this.computeOperand(CentralProcessingUnit.Instruction.ADD.ordinal(), CentralProcessingUnit.Instruction.SUB.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												case "-":
													this.computeOperand(CentralProcessingUnit.Instruction.SUB.ordinal(), CentralProcessingUnit.Instruction.ADD.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												case "*":
													this.computeOperand(CentralProcessingUnit.Instruction.MUL.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												case "/":
													this.computeOperand(CentralProcessingUnit.Instruction.DIV.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												case "&":
													this.computeOperand(CentralProcessingUnit.Instruction.AND.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												case "|":
													this.computeOperand(CentralProcessingUnit.Instruction.OR.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												case "~":
													this.computeOperand(CentralProcessingUnit.Instruction.NOT.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												case "^":
													this.computeOperand(CentralProcessingUnit.Instruction.XOR.ordinal(), tokenizer.nextToken(), next, arVariables);
													break;
												default:
													throw new IllegalFormatException();
											}
										}
										int instruction = CentralProcessingUnit.Instruction.STA.ordinal() << 24;
										Object adr = this.variables.get(next);
										if (adr == null) {
											arVariables.putIfAbsent(next, this.address++);
											adr = arVariables.get(next);
											instruction += 1 << 12;
										}
										instruction += (int) adr;
										this.codeV.add(instruction);

									} else throw new IllegalFormatException();


								} else arVariables.put(next, this.address++);//assignment only

							} else if (temp.equals("use")) {
								ParserAW parser = this.importModules.get(tokenizer.nextToken());
								if (parser != null)
									if (tokenizer.nextToken().equals(as)) {
										String instance = tokenizer.nextToken();
										if (var_pattern.matcher(instance).matches()) {
											this.instances.put(instance, this.heap++);
											int instruction = CentralProcessingUnit.Instruction.NEW.ordinal() << 24;
										} else throw new IllegalFormatException();
									} else throw new IllegalFormatException();
								else throw new IllegalFormatException();
							} else if (temp.equals("irpt")) {
								int instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << 24;
								String id = tokenizer.nextToken();
								if (number_pattern.matcher(id).matches()) {
									instruction += Integer.parseInt(id);
									this.codeV.add(instruction);
								} else throw new IllegalFormatException();
							} else if (temp.equals("if")) {// TODO: 2019-11-28 complete if statement
								int instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << 24;
								String id = tokenizer.nextToken();
								if (number_pattern.matcher(id).matches()) {
									instruction += Integer.parseInt(id);
									this.codeV.add(instruction);
								} else throw new IllegalFormatException();
							} else if (temp.equals("while")) {// TODO: 2019-11-28 complete while statement
								int instruction = CentralProcessingUnit.Instruction.ITR.ordinal() << 24;
								String id = tokenizer.nextToken();
								if (number_pattern.matcher(id).matches()) {
									instruction += Integer.parseInt(id);
									this.codeV.add(instruction);
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
		int[] array = new int[codeV.size()];
		for (int a : codeV) array[i++] = a;
		return array;
	}

	public int[] convertData() {
		int i = 0;
		int[] array = new int[dataV.size()];
		for (int a : dataV) array[i++] = a;
		return array;
	}

	private void computeOperand(int operation, int alternative, String operand, String next, HashMap<String, Integer> arVariables) throws IllegalFormatException {
		if (number_pattern.matcher(operand).matches()) {
			int x = Integer.parseInt(operand);
			if (x >= 0) {
				int instruction = operation << 24;
				instruction += x;
				this.codeV.add(instruction);
			} else {
				int instruction = alternative << 24;
				instruction += -x;
				this.codeV.add(instruction);
			}
		} else extractPattern(operation, operand, next, arVariables);
	}

	private void computeOperand(int operation, String operand, String next, HashMap<String, Integer> arVariables) throws IllegalFormatException {
		if (number_pattern.matcher(operand).matches()) {
			int x = Integer.parseInt(operand);
			if (x >= 0) {
				int instruction = operation << 24;
				instruction += x;
				this.codeV.add(instruction);
			} else throw new IllegalFormatException();
		} else extractPattern(operation, operand, next, arVariables);
	}

	private void extractPattern(int operation, String operand, String next, HashMap<String, Integer> arVariables) throws IllegalFormatException {
		if (var_pattern.matcher(operand).matches()) {
			Object adr = arVariables.get(next);
			if (adr == null) adr = this.variables.get(next);
			if (adr == null) throw new IllegalFormatException();
			int instruction = operation << 24;
			instruction += (int) adr;
			this.codeV.add(instruction);
		}
	}

}
