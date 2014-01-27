package fortscale.utils.hdfs.split;

/**
 * Sets file name according to a split strategy
 */
public interface FileSplitStrategy {

	/**
	 * Gets the resulting filename according to the split.
	 * The resulting filename can be changed and the file is expected to have a valid extension name.
	 */
	String getFilePath(String basePath, String filename, long timestamp);
}
