package fortscale.utils.hdfs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;

/**
 * HDFS writer that writes to partitioned files
 */
public class HDFSPartitionsWriter implements HDFSWriter {

	private static final Logger logger = LoggerFactory.getLogger(HDFSLineAppender.class);
	
	private PartitionStrategy partitionStrategy;
	private FileSplitStrategy fileSplitStrategy;
	private String basePath;
	private String fileName;
	private Map<String, BufferedWriter> writers = new HashMap<String, BufferedWriter>(2);
	private FileSystem fs;
	private List<String> newPartitions = new LinkedList<String>();
	
	
	public HDFSPartitionsWriter(String basePath, PartitionStrategy partitionStrategy, FileSplitStrategy fileSplitStrategy) {
		Assert.hasText(basePath);
		Assert.notNull(partitionStrategy);
		Assert.notNull(fileSplitStrategy);
		
		this.basePath = basePath;
		this.partitionStrategy = partitionStrategy;
		this.fileSplitStrategy = fileSplitStrategy;
	}


	/**
	 * writes the given line to the current time's file partition and split
	 */
	@Override 
	public void writeLine(String line) throws IOException {
		doWrite(line, true, DateTime.now(DateTimeZone.UTC).getMillis());
	}

	
	/**
	 * writes the given line to hdfs according to the split and partition strategy for the event time
	 */
	@Override 
	public void writeLine(String line, long timestamp) throws IOException {
		doWrite(line, true, timestamp);
	}
	

	/**
	 * writes the given text to the current time's partition and split
	 */
	@Override
	public void write(String str) throws IOException {
		doWrite(str, false, DateTime.now(DateTimeZone.UTC).getMillis());		
	}

	private void doWrite(String text, boolean newLine, long timestamp) throws IOException {
		if (text!=null) {
			// get the writer needed according to the event time
			BufferedWriter writer = ensureWriter(timestamp);
			if (writer!=null) {
				writer.write(text);
				if (newLine)
					writer.newLine();
			}
		}
	}
	

	@Override
	public void flush() throws IOException {
		// go over all writers and flush each one
		IOException lastException = null;
		for (BufferedWriter writer : writers.values()) {
			try {
				writer.flush();
			} catch (IOException e) {
				lastException = e;
			}
		}
		
		if (lastException!=null)
			throw lastException;
	}


	@Override
	public void close() throws IOException {
		// go over all writers and close each one, continue on error
		IOException lastException = null;
		for (BufferedWriter writer : writers.values()) {
			try {
				writer.close();
			} catch (IOException e) {
				lastException = e;
				logger.error("error closing hdfs appender", e);
			}
		}
		
		fs.close();
		if (lastException!=null)
			throw lastException;
	}


	@Override
	public void open(String fileName) throws IOException {
		Assert.hasText(fileName);
		this.fileName = fileName;
		
		// connect to hadoop
		Configuration configuration = new Configuration();
		configuration.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		configuration.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		
		fs = FileSystem.get(configuration);
	}
	
	/**
	 * Returns newly created partitions by this appender.
	 * If no new partition was created, an empty list will be returned 
	 */
	public List<String> getNewPartitions() {
		return newPartitions;
	}
	
	private BufferedWriter ensureWriter(long timestamp) throws IOException {
		// get the file path for the writer needed
		String partitionPath = partitionStrategy.getPartitionPath(timestamp, basePath);
		String filePath = fileSplitStrategy.getFilePath(partitionPath, fileName, timestamp);
		
		// keep track of new partitions that we create
		if (!fs.exists(new Path(partitionPath))) {
			// create path
			if (!fs.mkdirs(new Path(partitionPath)))
				throw new IOException("cannot create hdfs path " + partitionPath);

			// stored the new partition to be used later for impala refresh
			String partitionName = partitionStrategy.getImpalaPartitionName(timestamp);
			newPartitions.add(partitionName);
		}
		
		// check if a writer already created for that file
		BufferedWriter writer = writers.get(filePath);
		if (writer==null) {
			// create a new writer
			writer = openWriter(filePath);
			writers.put(filePath, writer);
		}
		return writer;
	}
	
	private BufferedWriter openWriter(String filePath) throws IOException {
		Path path = new Path(filePath);
		if (fs.exists(path)) {
			// open in append mode
			return new BufferedWriter(new OutputStreamWriter(fs.append(path)));
		} else {
			// open in create mode
			return new BufferedWriter(new OutputStreamWriter(fs.create(path,true)));
		}
	}
}
