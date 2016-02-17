package fortscale.collection.jobs.demo;

import org.joda.time.DateTime;

/**
 * Created by Amir Keren on 2/15/16.
 */
public class DemoEvent implements Comparable<DemoEvent> {

	private String lineToWrite;
	private DateTime dateTime;

	public DemoEvent() {}

	public DemoEvent(String lineToWrite, DateTime dateTime) {
		this.lineToWrite = lineToWrite;
		this.dateTime = dateTime;
	}

	@Override public int compareTo(DemoEvent o) {
		return dateTime.isAfter(o.getDateTime()) == true ? 1 : -1;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getLineToWrite() {
		return lineToWrite;
	}

	public void setLineToWrite(String lineToWrite) {
		this.lineToWrite = lineToWrite;
	}

}