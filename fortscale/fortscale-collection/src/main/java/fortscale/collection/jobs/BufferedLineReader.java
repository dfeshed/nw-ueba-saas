package fortscale.collection.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a file line by line using buffered reader
 */
class BufferedLineReader {

	private static Logger logger = LoggerFactory.getLogger(BufferedLineReader.class);
	
	private BufferedReader reader;
	private String filename;
	private boolean hasErrors = false;
	private boolean hasWarnings = false;
	private IOException exception;
	
	public void open(File file) {
		try {
			if (file!=null) {
				
				if (!file.exists()) {
					logger.error("file {} does not exists", file.getName());
					return;
				}
				
				if (!file.isFile()) {
					logger.error("{} is not a file", file.getName());
					return;
				}
				
				if (!file.canRead()) {
					logger.error("cannot read from file {}", file.getName());
					return;
				}
				
				filename = file.getName();
				reader = new BufferedReader(new FileReader(file));
			}
		} catch (IOException e) {
			logger.error("error reading from file " + filename, e);
			hasErrors = true;
			exception = e;
		}
	}
	
	public String readLine() {
		if (reader==null)
			return null;
		
		try {
			return reader.readLine();
		} catch (IOException e) {
			logger.error("error reading line from file", e);
			hasErrors = true;
			exception = e;
			return null;
		}
	}
	
	public void close() {
		if (reader!=null) {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("error closing file " + filename, e);
				hasWarnings = true;
				exception = e;
			}
		}
	}
	
	public boolean HasErrors() {
		return hasErrors;
	}
	
	public boolean hasWarnings() {
		return hasWarnings;
	}
	
	public IOException getException() {
		return exception;
	}
	
}
