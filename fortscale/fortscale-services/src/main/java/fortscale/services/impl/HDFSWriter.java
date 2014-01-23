package fortscale.services.impl;

import java.io.IOException;

public interface HDFSWriter {
	public void writeLine(String line) throws IOException;
	
	public void write(String str) throws IOException;
	
	public void flush() throws IOException;
	
	public void close() throws IOException;
	
	public void open(String filename) throws IOException;
}
