package os;

import global.IllegalFileFormatException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pc.mainboard.MainBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileManagerAW {
    public static final String CSV = "csv", AWX = "awx", EXW = "exw", TXT = "txt";
    private static DirectoryAW CDrive;

    public FileManagerAW() {
        CDrive = new DirectoryAW("C:/");
    }

    public void on() {
        this.baseLoad("exe/loop.awx", "system/system.awx");
        this.baseLoad("exe/main.awx", "exe/project1/main.awx");
        this.baseLoad("exe/sub.awx", "exe/project1/sub.awx");
        this.baseLoad("exe/main2.awx", "exe/project2/main.awx");
        this.baseLoad("exe/calculator.awx", "exe/calculator.awx");
    }

    public void baseLoad(String exist, String make) {
        try {
            Scanner scanner = new Scanner(new File(exist));
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\r\n");
            }
            this.loadFile(make, builder.toString());
        } catch (FileNotFoundException | IllegalFileFormatException e) {
            e.printStackTrace();
        }
    }

    public DirectoryAW getRootDirectory() {
        return CDrive;
    }

    public FileAW getFile(String filename) throws IllegalFileFormatException {
        StringTokenizer tokenizer = new StringTokenizer(filename, "/");
        String directory = null;
        DirectoryAW directoryAW = CDrive;
        while (tokenizer.hasMoreTokens()) {
            directory = tokenizer.nextToken();
            Integer address = directoryAW.directoryMap.get(directory);
            if (address == null && tokenizer.hasMoreTokens()) throw new IllegalFileFormatException();
            if (address != null) directoryAW = directoryAW.directoryAWS.get(address);
        }
        tokenizer = new StringTokenizer(directory == null ? filename : directory, ".");
        filename = tokenizer.nextToken();
        String extension;
        try {
            extension = tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            throw new IllegalFileFormatException();
        }
        return extension.equals(EXW) ? this.getExecutable(directoryAW, filename + "." + extension) : this.getData(directoryAW, filename + "." + extension);
    }

    public FileAW getFile(int index) {
        return MainBoard.disk.getFile(index);
    }

    private FileAW<ExecutableAW> getExecutable(DirectoryAW directoryAW, String filename) {
        return MainBoard.disk.getFile(directoryAW.fileAWS.get(directoryAW.fileMap.get(filename)));
    }

    private FileAW<String> getData(DirectoryAW directoryAW, String filename) {
        return MainBoard.disk.getFile(directoryAW.fileAWS.get(directoryAW.fileMap.get(filename)));
    }

    public void loadFile(String filename, ExecutableAW executableAW) throws IllegalFileFormatException {
        StringTokenizer tokenizer = new StringTokenizer(filename, ".");
        String extension = null;
        StringBuilder directory = new StringBuilder();
        while (tokenizer.hasMoreTokens()) extension = tokenizer.nextToken();
        tokenizer = new StringTokenizer(filename, "/");
        while (tokenizer.countTokens() > 1) directory.append(tokenizer.nextToken()).append("/");
        String file = tokenizer.nextToken();
        FileAW<ExecutableAW> fileAW;
        if (extension == null) throw new IllegalFileFormatException();
        if (extension.equals(EXW)) fileAW = new FileAW<>(file, extension, directory.toString(), executableAW);
        else throw new IllegalFileFormatException();
        this.loadFile(filename, MainBoard.disk.saveFile(fileAW));
    }

    public void loadFile(String filename, String content) throws IllegalFileFormatException {
        StringTokenizer tokenizer = new StringTokenizer(filename, ".");
        String extension = null;
        StringBuilder directory = new StringBuilder();
        while (tokenizer.hasMoreTokens()) extension = tokenizer.nextToken();
        tokenizer = new StringTokenizer(filename, "/");
        while (tokenizer.countTokens() > 1) directory.append(tokenizer.nextToken()).append("/");
        String file = tokenizer.nextToken();
        FileAW<String> fileAW;
        if (extension == null) throw new IllegalFileFormatException();
        if (!extension.equals(EXW)) {
            fileAW = new FileAW<>(file, extension, directory.toString(), content);
        } else {
            throw new IllegalFileFormatException();
        }
        this.loadFile(filename, MainBoard.disk.saveFile(fileAW));
    }

    private void loadFile(String filename, int index) {
        StringTokenizer tokenizer = new StringTokenizer(filename, "/");
        DirectoryAW directoryAW = CDrive;
        while (tokenizer.countTokens() > 1) {
            DirectoryAW temp = this.createDirectory(directoryAW, tokenizer.nextToken());
            if (!directoryAW.contains(temp)) {
                directoryAW.directoryMap.put(temp.name, directoryAW.directoryAWS.size());
                directoryAW.directoryAWS.add(temp);
            }
            directoryAW = temp;
        }
        directoryAW.fileMap.put(tokenizer.nextToken(), directoryAW.fileAWS.size());
        directoryAW.fileAWS.add(index);
        OperatingSystem.uxManagerAW.updateFile();
    }

    public void createDirectory(String path) {
        DirectoryAW directoryAW = CDrive;
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while (tokenizer.hasMoreTokens()) {
            directoryAW = this.createDirectory(directoryAW, tokenizer.nextToken());
        }
    }

    public void createDirectory(String path, DirectoryAW directoryAW) {
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while (tokenizer.hasMoreTokens()) {
            directoryAW = this.createDirectory(directoryAW, tokenizer.nextToken());
        }
    }

    private DirectoryAW createDirectory(DirectoryAW directoryAW, String name) {
        DirectoryAW directory = new DirectoryAW(name);
        if (directoryAW.directoryMap.containsKey(name))
            return directoryAW.directoryAWS.get(directoryAW.directoryMap.get(name));
        directoryAW.directoryMap.put(name, directoryAW.directoryAWS.size());
        directoryAW.directoryAWS.add(directory);
        return directory;
    }

    public static class DirectoryAW {
        private String name;
        Vector<DirectoryAW> directoryAWS = new Vector<>();
        Vector<Integer> fileAWS = new Vector<>();//save absolute address
        private Hashtable<String, Integer> fileMap = new Hashtable<>();//save file vector index
        private Hashtable<String, Integer> directoryMap = new Hashtable<>();

        public DirectoryAW(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            DirectoryAW that = (DirectoryAW) o;

            return new EqualsBuilder()
                    .append(name, that.name)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(name)
                    .toHashCode();
        }

        public boolean contains(DirectoryAW temp) {
            return this.directoryAWS.contains(temp);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static class FileAW<T> {
        public int fid;
        public String extension, filename, directory;
        public T content;

        public FileAW(String filename, String extension, String directory, T content) {
            this.filename = filename;
            this.extension = extension;
            this.directory = directory;
            this.content = content;
        }

        public String getFilename() {
            return filename;
        }

        @Override
        public String toString() {
            return this.filename;
        }
    }
}
