package fortscale.services.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fortscale.utils.hdfs.HDFSWriter;
import fortscale.utils.logging.Logger;

public class LocalFileWriter implements HDFSWriter {
	private static Logger logger = Logger.getLogger(LocalFileWriter.class);
	
	private BufferedWriter writer = null;
	
	public LocalFileWriter(File file){
		writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			logger.error("got and exception while trying to load local file for impala use.", e);
		}
	}

	@Override
	public void writeLine(String line) throws IOException {
		if (writer != null) {
			writer.newLine();
		}
	}

	@Override
	public void write(String str) throws IOException {
		if (writer != null) {
			writer.write(str);
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
		if (writer != null) {
			writer.close();
		}
		writer = null;
	}

	@Override
	public void open(String filename) throws IOException {
		if(writer != null){
			writer.close();
		}
		File file = new File(filename);
		writer = new BufferedWriter(new FileWriter(file));
	}

	@Override
	public void writeLine(String line, long timestamp) throws IOException {
		writeLine(line);
	}

}
