package fortscale.streaming.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.streaming.exceptions.HdfsException;
import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaClient;

/**
 * Service for hdfs and impala related operations
 */
public class HdfsService {

	private static Logger logger = LoggerFactory.getLogger(HdfsService.class);
	
	private HDFSPartitionsWriter appender;
	private String tableName;
	private String fileName;
	private ImpalaClient impalaClient;
	private WriteBuffer buffer;
	
	public HdfsService(String hdfsRootPath, String fileName, PartitionStrategy partition, FileSplitStrategy split, String tableName, int flushPerItemsCount) throws IOException {
		// create hdfs appender
		this.fileName = fileName;
		appender = new HDFSPartitionsWriter(hdfsRootPath, partition, split);
		
		// create impala client
		this.tableName = tableName;
		impalaClient = SpringService.getInstance().resolve(ImpalaClient.class);
		
		// create buffer for items written to hdfs
		buffer = new WriteBuffer(flushPerItemsCount);
	}
	
	public void writeLineToHdfs(String line, long timestamp) throws HdfsException {
		// add line to buffer
		buffer.add(timestamp, line);
		
		// see if the buffer exceeded the limit, if so write them all to hdfs
		if (buffer.isFull()) 
			writeBuffer();
	}
	
	public void flushHdfs() throws HdfsException {
		writeBuffer();
	}
	
	private void writeBuffer() throws HdfsException {
		Exception firstException = null;
		try {
			// write all items to hdfs appender
			appender.open(fileName);
			for (WriteBuffer.LineEntry item : buffer.getItems()) {
				appender.writeLine(item.line, item.timestamp);
			}
			appender.flush();
			appender.close();
			buffer.clear();
			
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
				impalaClient.refreshTable(tableName);
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
		try{
			flushHdfs();
		} finally{
			SpringService.shutdown();
		}
	}
	
	private class WriteBuffer {
		
		public class LineEntry {
			public LineEntry(long timestamp, String line) {
				this.timestamp = timestamp;
				this.line = line;
			}
			
			public long timestamp;
			public String line;
		}
		
		private int sizeLimit;
		private List<LineEntry> buffer;
		
		public WriteBuffer(int sizeLimit) {
			this.sizeLimit = sizeLimit;
			this.buffer = new ArrayList<LineEntry>(sizeLimit);
		}
		
		public void add(long timestamp, String line) {
			buffer.add(new LineEntry(timestamp, line));
		}
		
		public boolean isFull() {
			return buffer.size()>=sizeLimit;
		}
		
		public void clear() {
			buffer.clear();
		}
		
		public Iterable<LineEntry> getItems() {
			return buffer;
		}
	}
	
}
