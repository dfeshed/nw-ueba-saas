package fortscale.utils.hdfs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * HDFS file writer that appends lines to file
 */
public class HDFSLineAppender implements HDFSWriter {

	private BufferedWriter writer;
	private FileSystem fs;
	
	@Override
	public void open(String filename) throws IOException {
		close();
		
		Configuration configuration = new Configuration();
		configuration.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		configuration.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		
		fs = FileSystem.get(configuration);
		
		Path path = new Path(filename);
		if (fs.exists(path)) {
			// open in append mode
			writer = new BufferedWriter(new OutputStreamWriter(fs.append(path)));
		} else {
			// open in create mode
			writer = new BufferedWriter(new OutputStreamWriter(fs.create(path,true)));
		}
	}

	@Override
	public void writeLine(String line, long timestamp) throws IOException {
		writeLine(line);
	}
	
	@Override
	public void writeLine(String line) throws IOException {
		if (writer!=null && line!=null) {
			writer.write(line);
			writer.newLine();
		}
	}
	
	@Override
	public void flush() throws IOException {
		if (writer!=null) {
			writer.flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		if (writer!=null) {
			try {
				writer.close();
				writer = null;
			} finally {
				fs = null;
			}
		}
	}

	@Override
	public void write(String str) throws IOException {
		if (writer!=null) {
			writer.write(str);
		}
	}
	
}
