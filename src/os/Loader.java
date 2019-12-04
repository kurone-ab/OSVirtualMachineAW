package os;


import global.DuplicateVariableException;
import global.IllegalFormatException;
import global.IllegalInstructionException;
import os.compiler.CompilerAW;
import os.compiler.ConverterAW;

import java.security.SecureRandom;

public class Loader {
	private static final SecureRandom random = new SecureRandom();

	public synchronized static void load(String filename){
		random.setSeed(System.currentTimeMillis());
		try {
			CompilerAW compilerAW = new CompilerAW(OperatingSystem.fileManagerAW.getFile(filename));
			compilerAW.initialize(filename);
			compilerAW.parse();
			ProcessAW processAW = new ProcessAW(random.nextInt(((int) (Math.random() * 1000))),
					compilerAW.convertCode(), compilerAW.convertData(), compilerAW.stack(),
					compilerAW.getStartLine());
			OperatingSystem.memoryManagerAW.load(processAW);
			System.out.println("process load");
		} catch (IllegalInstructionException | IllegalFormatException | DuplicateVariableException e) {
			e.printStackTrace();
		}
	}

	public synchronized static void load(ConverterAW.ExecutableAW executableAW){
		random.setSeed(System.currentTimeMillis());
		ProcessAW processAW = new ProcessAW(random.nextInt(((int) (Math.random() * 1000))),
				executableAW.code, executableAW.data, executableAW.size,
				executableAW.startLine);
		OperatingSystem.memoryManagerAW.load(processAW);
		System.out.println("process load");
	}
}
