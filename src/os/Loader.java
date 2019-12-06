package os;


import global.DuplicateVariableException;
import global.IllegalFileFormatException;
import global.IllegalFormatException;
import global.IllegalInstructionException;
import os.compiler.CompilerAW;
import os.compiler.ConverterAW;

import java.security.SecureRandom;
import java.util.StringTokenizer;

public class Loader {
	private static final SecureRandom random = new SecureRandom();

	public synchronized static void load(ExecutableAW executableAW){
		random.setSeed(System.currentTimeMillis());
		ProcessAW processAW = new ProcessAW(random.nextInt(((int) (Math.random() * 1000))),
				executableAW.code, executableAW.data, executableAW.size,
				executableAW.startLine);
		OperatingSystem.memoryManagerAW.load(processAW);
		System.out.println("process load");
	}

	public synchronized static void load(FileManagerAW.FileAW<String> awx){
		random.setSeed(System.currentTimeMillis());
		try {
			CompilerAW compilerAW = new CompilerAW(awx);
			compilerAW.initialize();
			compilerAW.parse();
			ConverterAW<CompilerAW> converterAW = new ConverterAW<>();
			ExecutableAW executableAW = converterAW.convert(compilerAW);
			StringTokenizer tokenizer = new StringTokenizer(awx.filename, ".");
			StringBuilder builder = new StringBuilder();
			while(tokenizer.countTokens()>1) builder.append(tokenizer.nextToken());
			builder.append(".").append(FileManagerAW.EXW);
			OperatingSystem.fileManagerAW.loadFile(builder.toString(), executableAW);
			Loader.load(executableAW);
			System.out.println("process load");
		} catch (IllegalFormatException | IllegalInstructionException | DuplicateVariableException | IllegalFileFormatException e) {
			e.printStackTrace();
		}
	}
}
