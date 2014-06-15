package fortscale.streaming.service;

import java.io.IOException;

import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;

/**
 * Service for hdfs and impala related operations
 */
public class HdfsService {

	private HDFSPartitionsWriter appender;
	private String tableName;	
	
	public HdfsService(String hdfsRootPath, String fileName, PartitionStrategy partition, FileSplitStrategy split, String tableName) throws IOException {
		// create hdfs appender
		appender = new HDFSPartitionsWriter(hdfsRootPath, partition, split);
		appender.open(fileName);
		
		// TODO: create impala client
		this.tableName = tableName;
	}
	
	public void writeLineToHdfs(String line, long timestamp) throws IOException {
		appender.writeLine(line, timestamp);
	}
	
	public void flushHdfs() throws IOException {
		appender.flush();
		
		// add new partitions to impala
		for (String partition : appender.getNewPartitions()) {
			//impalaClient.addPartitionToTable(tableName, partition);
		}
		appender.clearNewPartitions();
		//impalaClient.refreshTable(impalaTableName);
	}
	
	public void close() throws IOException {
		// call flush to ensure that all partitions were added to impala
		flushHdfs();
		appender.close();
	}
	
	
	
}
