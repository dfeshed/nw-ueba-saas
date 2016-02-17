package fortscale.collection.jobs.demo;

import org.joda.time.DateTime;

/**
 * Created by Amir Keren on 2/15/16.
 */
public class DemoEventAux implements Comparable<DemoEventAux> {

	private String lineToWrite;
	private DateTime dateTime;

	public DemoEventAux(String lineToWrite, DateTime dateTime) {
		this.lineToWrite = lineToWrite;
		this.dateTime = dateTime;
	}

	public String getLineToWrite() {
		return lineToWrite;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	@Override public int compareTo(DemoEventAux o) {
		return dateTime.isAfter(o.getDateTime()) == true ? 1 : -1;
	}

}