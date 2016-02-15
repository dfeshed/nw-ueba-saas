package fortscale.collection.jobs.demo;

/**
 * Created by Amir Keren on 14/02/16.
 */
public class DataSourceProperties {

	private String impalaTable;
	private String fileName;
	private String hdfsPartition;
	private String topic;
	private String fields;
	private DemoUtils.DataSource dataSource;

	public DataSourceProperties(String impalaTable, String fileName, String hdfsPartition, String topic,
			String fields, DemoUtils.DataSource dataSource) {
		this.impalaTable = impalaTable;
		this.fileName = fileName;
		this.hdfsPartition = hdfsPartition;
		this.topic = topic;
		this.fields = fields;
		this.dataSource = dataSource;
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

	public String getTopic() { return topic; }

	public String getFields() { return fields; }

	public DemoUtils.DataSource getDataSource() {
		return dataSource;
	}

}