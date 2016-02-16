package fortscale.collection.jobs.demo;

import net.minidev.json.JSONObject;
import org.joda.time.DateTime;

import java.util.Comparator;

/**
 * Created by Amir Keren on 2/16/16.
 */
public class JSONComparator implements Comparator<JSONObject> {

	@Override public int compare(JSONObject o1, JSONObject o2) {
		DateTime t1 = new DateTime((Long)o1.get(DemoUtils.EPOCH_TIME_FIELD) * 1000);
		DateTime t2 = new DateTime((Long)o2.get(DemoUtils.EPOCH_TIME_FIELD) * 1000);
		return t1.isAfter(t2) == true ? 1 : -1;
	}

}