package fortscale.services.impl;

import fortscale.services.exceptions.HdfsException;
import fortscale.utils.hdfs.BufferedHDFSWriter;
import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Service for hdfs and impala related operations
 */
public class HdfsService {

	private static Logger logger = LoggerFactory.getLogger(HdfsService.class);
	
	private HDFSPartitionsWriter appender;
	private String tableName;
	private String fileName;
	private ImpalaClient impalaClient;
	private BufferedHDFSWriter buffer;

	// minimum time to refresh impala table (will not refresh it more often than this, unless there are new partitions)
	private long impalaRefreshWindow;

	// latest time we refreshed impala table
	private long lastImpalaRefreshEpoch;
	
	public HdfsService(String hdfsRootPath, String fileName, PartitionStrategy partition, FileSplitStrategy split, String tableName, int flushPerItemsCount, long impalaRefreshWindow, String separator) throws IOException {
		// create hdfs appender
		this.fileName = fileName;
		appender = new HDFSPartitionsWriter(hdfsRootPath, partition, split, separator);
		
		// create impala client
		this.tableName = tableName;
		impalaClient = SpringService.getInstance().resolve(ImpalaClient.class);
		this.impalaRefreshWindow = impalaRefreshWindow;
		
		// create buffer for items written to hdfs
		buffer = new BufferedHDFSWriter(appender, fileName, flushPerItemsCount);
	}
	
	public void writeLineToHdfs(String line, long timestamp) throws HdfsException {
		try {
			buffer.writeLine(line, timestamp, false);
			if (buffer.isFull())
				writeBuffer();
		} catch (IOException e) {
			throw new HdfsException("cannot write to " + fileName, e);
		}
	}
	
	public void flushHdfs() throws HdfsException {
		// force flush requires us to refresh impala, so we set the last time to be start of epoch
		lastImpalaRefreshEpoch = 0L;
		writeBuffer();
	}
	
	private void writeBuffer() throws HdfsException {
		Exception firstException = null;
		try {
			// flush the buffer 
			buffer.flush();
			
			// add new partitions to impala
			for (String partition : appender.getNewPartitions()) {
				try {
					impalaClient.addPartitionToTable(tableName, partition);
				} catch (Exception e) {
					logger.error("error adding partition " + partition + " to table " + tableName, e);
					if (firstException==null)
						firstException = e;
				}
			}
			// clear the new partitions list from the hdfs appender, once they were added to impala
			appender.clearNewPartitions();
			
			// refresh the impala table with new partitions data
			try {
				// check if we passed the refresh window duration
				if (System.currentTimeMillis() - lastImpalaRefreshEpoch >= impalaRefreshWindow) {
					impalaClient.refreshTable(tableName);
					lastImpalaRefreshEpoch = System.currentTimeMillis();
				}
			} catch (Exception e) {
				logger.error("error refreshing table " + tableName, e);
				if (firstException==null)
					firstException = e;
			}
		} catch (Exception e){
			if (firstException==null)
				firstException = e;
		}
		
		if (firstException!=null){
			throw new HdfsException(String.format("flushHdfs failed. fileName: %s tablename: %s.", fileName, tableName), firstException);
		}
	}
	 
		
	public void close() throws Exception {
		// call flush to ensure that all partitions were added to impala
		flushHdfs();
	}
}
