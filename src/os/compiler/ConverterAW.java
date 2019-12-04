package os.compiler;

public class ConverterAW<T extends CompilerAW> {

    public ExecutableAW convert(T compiler){
        ExecutableAW executableAW = new ExecutableAW();
        executableAW.code = compiler.convertCode();
        executableAW.data = compiler.convertData();
        executableAW.size = compiler.size;
        executableAW.startLine = compiler.getStartLine();
        return executableAW;
    }

    public static class ExecutableAW{
        public int[] code, data;
        public int size, startLine;
    }
}
