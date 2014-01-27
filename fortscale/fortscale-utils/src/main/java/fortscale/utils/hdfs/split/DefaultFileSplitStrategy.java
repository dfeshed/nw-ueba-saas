package fortscale.utils.hdfs.split;

/**
 * Split strategy that does not split files 
 */
public class DefaultFileSplitStrategy implements FileSplitStrategy {

	@Override
	public String getFilePath(String basePath, String filename, long timestamp) {
		if (basePath==null)
			throw new IllegalArgumentException("base path is required");
		if (filename==null)
			throw new IllegalArgumentException("file name is required");
		if (!filename.contains("."))
			throw new IllegalArgumentException("file name must contain suffix");
		
		// normalize base path
		String normalize = basePath.replace("\\", "/");
		if (normalize.endsWith("/"))
			return normalize + filename;
		else
			return normalize + "/" + filename;
	}

}
