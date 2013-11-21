package fortscale.services.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

public class ImpalaWriter{
	private static Logger logger = Logger.getLogger(ImpalaWriter.class);
	
	private BufferedWriter writer = null;
	private ImpalaParser impalaParser;
	
	public ImpalaWriter(ImpalaParser impalaParser){
		this.impalaParser = impalaParser;
	}
	public ImpalaWriter(File file, ImpalaParser impalaParser) {
		this.impalaParser = impalaParser;
		writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			logger.error("got and exception while trying to load local file for impala use.", e);
		}
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
				writer.newLine();
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
