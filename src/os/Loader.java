package os;


import global.*;
import os.compiler.CompilerAW;
import os.compiler.ConverterAW;

import java.security.SecureRandom;
import java.util.StringTokenizer;

public class Loader {
	private static final SecureRandom random = new SecureRandom();

	public synchronized static void load(ExecutableAW executableAW, int priority){
		random.setSeed(System.currentTimeMillis());
		ProcessAW processAW = new ProcessAW(random.nextInt(251648),
				executableAW.code, executableAW.data, executableAW.size,
				executableAW.startLine);
		OperatingSystem.memoryManagerAW.load(processAW, priority);
	}

	public synchronized static void load(FileManagerAW.FileAW<String> awx, int priority){
		random.setSeed(System.currentTimeMillis());
		try {
			CompilerAW compilerAW = new CompilerAW(awx);
			compilerAW.initialize();
			compilerAW.parse();
			ConverterAW<CompilerAW> converterAW = new ConverterAW<>();
			ExecutableAW executableAW = converterAW.convert(compilerAW);
			Loader.load(executableAW, priority);
		} catch (IllegalFormatException | IllegalInstructionException | DuplicateVariableException | IllegalFileFormatException e) {
			e.printStackTrace();
		} catch (NotMainAWXFileException e) {
			OperatingSystem.uxManagerAW.errorMessage(e.getMessage());
		}
	}
}
