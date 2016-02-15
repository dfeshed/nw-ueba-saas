package fortscale.collection.jobs.demo;

import org.joda.time.DateTime;

/**
 * Created by Amir Keren on 2/15/16.
 */
public class LineAux {

	private String lineToWrite;
	private DateTime dateTime;

	public LineAux(String lineToWrite, DateTime dateTime) {
		this.lineToWrite = lineToWrite;
		this.dateTime = dateTime;
	}

	public String getLineToWrite() {
		return lineToWrite;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

}