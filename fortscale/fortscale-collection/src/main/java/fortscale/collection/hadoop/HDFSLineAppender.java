package fortscale.collection.hadoop;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * HDFS file writer that appends lines to file
 */
public class HDFSLineAppender {

	private BufferedWriter writer;

	public void open(String filename) throws IOException {
		Configuration configuration = new Configuration();
		configuration.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		configuration.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		
		FileSystem fs = FileSystem.get(configuration);
		
		Path path = new Path(filename);
		if (fs.exists(path)) {
			// open in append mode
			writer = new BufferedWriter(new OutputStreamWriter(fs.append(path)));
		} else {
			// open in create mode
			writer = new BufferedWriter(new OutputStreamWriter(fs.create(path,true)));
		}
	}

	
	public void writeLine(String line) throws IOException {
		if (writer!=null && line!=null) {
			writer.write(line);
		}
	}
	
	public void flush() throws IOException {
		if (writer!=null) {
			writer.flush();
		}
	}
	
	public void close() throws IOException {
		if (writer!=null) {
			writer.close();
		}
	}
	
}
