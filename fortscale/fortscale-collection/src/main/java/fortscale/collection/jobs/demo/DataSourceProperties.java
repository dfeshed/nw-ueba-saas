package fortscale.collection.jobs.demo;

/**
 * Created by Amir Keren on 14/02/16.
 */
public class DataSourceProperties {

	private String impalaTable;
	private String fileName;
	private String hdfsPartition;
	private String topics;
	private String fields;

	public DataSourceProperties(String impalaTable, String fileName, String hdfsPartition, String topics,
			String fields) {
		this.impalaTable = impalaTable;
		this.fileName = fileName;
		this.hdfsPartition = hdfsPartition;
		this.topics = topics;
		this.fields = fields;
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

	public String getFields() { return fields; }

}