package global;

import pc.mainboard.cpu.CentralProcessingUnit;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

public class ParserAW {
	private static final String allocate = "allocate", staticData = "static", main = "start",
			imports = "import", function = "func", use = "use", as = "as", annotation = "/--";
	private String sentence;
	private Scanner scanner;
	private Hashtable<String, Integer> variables, functions, instances;
	private Vector<Integer> codeV, dataV;
	private int size, address;
	private Pattern number_pattern;
	private Pattern alpha_pattern;
	private Pattern alnum_pattern;
	private Pattern not_alnum_pattern;
	private Pattern fnc_pattern;
	private Pattern var_pattern;

	public ParserAW(String sentence) {
		this.sentence = sentence;
		this.scanner = new Scanner(sentence);
		this.variables = new Hashtable<>();
		this.functions = new Hashtable<>();
		this.instances = new Hashtable<>();
		this.codeV = new Vector<>();
		this.dataV = new Vector<>();
		this.size = address = 0;
		this.number_pattern = Pattern.compile("[0-9]+");
		this.alpha_pattern = Pattern.compile("^[a-zA-Z]+");
		this.alnum_pattern = Pattern.compile("^[a-zA-Z0-9]+");
		this.not_alnum_pattern = Pattern.compile("[^A-Za-z0-9]");
		this.fnc_pattern = Pattern.compile("^([a-zA-Z][a-zA-Z0-9]*\\.?)+\\([a-zA-Z0-9\\,?]*\\)");
		this.var_pattern = Pattern.compile("^([a-zA-Z][a-zA-Z0-9]*\\.?)+");
	}

	public void parse() throws IllegalInstructionException, IllegalFormatException {
		read:
		while (this.scanner.hasNextLine()) {
			String line = this.scanner.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(line);
			StringReader reader;
			String next;
			switch (tokenizer.nextToken()) {
				case allocate:
					next = tokenizer.nextToken();
					if (this.number_pattern.matcher(next).matches()) this.size = Integer.parseInt(next);
					else throw new IllegalInstructionException();
					break read;
				case staticData:
					next = tokenizer.nextToken();
					if (this.var_pattern.matcher(next).matches()) {
						if (tokenizer.hasMoreTokens()){//assignment and initialize
							String operator = tokenizer.nextToken();
							if (operator.equals("=")) {
								String value = tokenizer.nextToken();
								if (this.number_pattern.matcher(value).matches()) {
									this.variables.put(next, this.address++);
									this.dataV.add(Integer.parseInt(value));
								} else if (this.var_pattern.matcher(value).matches()) {
									Object adr = this.variables.get(next);
									if (adr == null) throw new IllegalFormatException();
									this.variables.put(next, this.address++);
									this.dataV.add(this.dataV.get((int) adr));
								}
								break read;
							}else throw new IllegalFormatException();
						}else {//assignment only
							this.variables.put(next, this.address++);
							this.dataV.add(0);
						}
					} else throw new IllegalInstructionException();
				case imports:
				case function:
				case use:
				case as:
				case annotation:
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

}
