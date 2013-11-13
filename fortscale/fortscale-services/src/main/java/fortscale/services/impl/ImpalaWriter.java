package fortscale.services.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import fortscale.utils.impala.ImpalaParser;

public class ImpalaWriter{
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(String str){
		if (writer != null) {
			try {
				writer.write(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void newLine(){
		if (writer != null) {
			try {
				writer.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
