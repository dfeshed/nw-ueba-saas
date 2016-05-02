package fortscale.utils.splunk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SplunkEventsHandlerLogger extends SplunkEventsHandler {

	private String localCsvFullPath;
	private BufferedWriter writer = null;

	public SplunkEventsHandlerLogger(String fullPathFileName) {
		this.localCsvFullPath = fullPathFileName;
	}
	
	public void open() throws IOException {
		File file = new File(localCsvFullPath);
		if(writer != null){
			writer.close();
		}
		writer = new BufferedWriter(new FileWriter(file));
	}
	
	public void close() throws Exception {
		writer.close();
	}
	
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void write(String str) throws IOException {
		writer.write(str);
	}

	@Override
	public void newLine() throws IOException {
		writer.newLine();
	}

}