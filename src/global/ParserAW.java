package global;

public class ParserAW {
	private static String sentence;
	public static void prepareParsing(String sentence){
		ParserAW.sentence = sentence;
	}

	public static int stackSize(){
		return 1;
	}
	public static int[] parseCode(){
		return new int[1];
	}

	public static int[] parseData(){
		return new int[1];
	}

}
