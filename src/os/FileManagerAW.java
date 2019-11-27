package os;

import pc.mainboard.MainBoard;

import java.util.HashMap;

public class FileManagerAW {
	private HashMap<String, Integer> fileMap;
	public String getFile(String filename){
		return MainBoard.disk.getFile(this.fileMap.get(filename));
	}
}
