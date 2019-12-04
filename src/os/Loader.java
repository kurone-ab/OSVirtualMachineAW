package os;


import global.DuplicateVariableException;
import global.IllegalFormatException;
import global.IllegalInstructionException;
import os.compiler.CompilerAW;
import os.compiler.ConverterAW;

import java.security.SecureRandom;

public class Loader {
	private static final SecureRandom random = new SecureRandom();

	public synchronized static void load(ConverterAW.ExecutableAW executableAW){
		random.setSeed(System.currentTimeMillis());
		ProcessAW processAW = new ProcessAW(random.nextInt(((int) (Math.random() * 1000))),
				executableAW.code, executableAW.data, executableAW.size,
				executableAW.startLine);
		OperatingSystem.memoryManagerAW.load(processAW);
		System.out.println("process load");
	}
}
