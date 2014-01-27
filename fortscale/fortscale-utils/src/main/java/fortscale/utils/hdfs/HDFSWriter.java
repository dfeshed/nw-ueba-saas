package fortscale.utils.hdfs;

import java.io.IOException;

public interface HDFSWriter {
	
	public void open(String filename) throws IOException;
	
	public void writeLine(String line) throws IOException;
	public void writeLine(String line, long timestamp) throws IOException;
	public void write(String str) throws IOException;
	
	public void flush() throws IOException;
	
	public void close() throws IOException;	
}
