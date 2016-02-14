package fortscale.collection.jobs.demo;

/**
 * Created by Amir Keren on 14/02/16.
 */
public class DataSourceProperties {

	private String impalaTable;
	private String fileName;
	private String hdfsPartition;
	private String topics;

	public DataSourceProperties(String impalaTable, String fileName, String hdfsPartition, String topics) {
		this.impalaTable = impalaTable;
		this.fileName = fileName;
		this.hdfsPartition = hdfsPartition;
		this.topics = topics;
	}

	public String getImpalaTable() {
		return impalaTable;
	}

	public String getFileName() {
		return fileName;
	}

	public String getHdfsPartition() {
		return hdfsPartition;
	}

	public String getTopics() { return topics; }

}