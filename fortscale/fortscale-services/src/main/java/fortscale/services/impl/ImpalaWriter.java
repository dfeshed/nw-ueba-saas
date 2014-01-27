package fortscale.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import fortscale.utils.hdfs.HDFSWriter;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

public class ImpalaWriter{
	private static Logger logger = Logger.getLogger(ImpalaWriter.class);
	
	private HDFSWriter writer = null;
	private ImpalaParser impalaParser;
	
	public ImpalaWriter(ImpalaParser impalaParser){
		this.impalaParser = impalaParser;
	}
	public ImpalaWriter(File file, ImpalaParser impalaParser) {
		this.impalaParser = impalaParser;
		writer = new LocalFileWriter(file);
	}
	
	public ImpalaWriter(HDFSWriter writer, ImpalaParser impalaParser) {
		this.impalaParser = impalaParser;
		this.writer = writer;
	}
	
	public void write(String str){
		if (writer != null) {
			try {
				writer.write(str);
			} catch (IOException e) {
				logger.error("got and exception while trying to write to local file for impala use.", e);
			}
		}
	}

	public void newLine(){
		if (writer != null) {
			try {
				writer.writeLine("");
			} catch (IOException e) {
				logger.error("got and exception while trying to write new line to local file for impala use.", e);
			}
		}
	}
	
	public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				logger.error("got and exception while trying to close local file for impala use.", e);
			}
		}
		writer = null;
	}
	
	protected long getRuntime(Date timestamp){
		return impalaParser.getRuntime(timestamp);
	}
	
	protected String getRundate(Date date){
		return impalaParser.formatTimeDate(date);
	}
}
