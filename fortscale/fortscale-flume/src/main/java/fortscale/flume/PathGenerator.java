package fortscale.flume;

import java.io.File;

public class PathGenerator {

	private File baseDirectory;
	private File currentFile;

	private String filePrefix;
	private String fileSuffix;

	public PathGenerator() {
		filePrefix = "";
		fileSuffix = "";
	}

	public File nextFile() {
		currentFile = new File(baseDirectory,
				String.format("%s_%d.%s.part", filePrefix, System.currentTimeMillis(), fileSuffix));

		return currentFile;
	}

	public File getCurrentFile() {
		if (currentFile == null) {
			return nextFile();
		}

		return currentFile;
	}

	public void rotate() {
		if (currentFile!=null) {
			String newPath = currentFile.getAbsolutePath();
			newPath = newPath.substring(0,newPath.lastIndexOf(".part"));

			File newFile = new File(newPath);
			currentFile.renameTo(newFile);

			currentFile = null;
		}
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public void setFileSuffix(String fileSuffix){
		this.fileSuffix = fileSuffix;
	}

	public void setFilePrefix(String filePrefix){
		this.filePrefix = filePrefix;
	}

	public String getFileSuffix(){
		return fileSuffix;
	}

	public String getFilePrefix(){
		return filePrefix;
	}

}