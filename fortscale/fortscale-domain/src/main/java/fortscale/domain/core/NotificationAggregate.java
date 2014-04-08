package fortscale.domain.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class NotificationAggregate implements Serializable {

	private static final long serialVersionUID = 8732010071833651983L;
	
	private List<Notification> aggregated = new ArrayList<Notification>();
	private String generator_name;
	private String cause;
	private String type;
	private long ts;
	private HashMap<String, List<String>> aggAttributes;

	public NotificationAggregate(List<Notification> list) {
		Notification notification = list.get(0);
		generator_name = notification.getGenerator_name();
		aggregated.addAll(list);
		cause = notification.getCause();
		
		// get put the earliest timestamp in the aggregation timestamp
		ts = notification.getTs();
		for (Notification item : list)
			ts = Math.min(ts, item.getTs());
		
		
		aggAttributes = getAttributes(aggregated);
		type = "agg";
	}

	public HashMap<String, List<String>> getAggAttributes() {
		return aggAttributes;
	}

	private HashMap<String, List<String>> getAttributes(List<Notification> aggregated) {
		HashMap<String, List<String>> aggAtt = new HashMap<>();

		for (Notification n : aggregated) {
			if (n.getAttributes() != null) {
				Set<Entry<String, String>> entrySet = n.getAttributes().entrySet();
				for (Entry<String, String> entry : entrySet) {
					String key = entry.getKey();
					if (aggAtt.containsKey(key) == false) {
						aggAtt.put(key, new ArrayList<String>());
					}
					aggAtt.get(key).add(entry.getValue());
				}
			}
		}
		return aggAtt;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getGenerator_name() {
		return generator_name;
	}

	public String getCause() {
		return cause;
	}

	public String getType() {
		return type;
	}

	public long getTs() {
		return ts;
	}

	public List<Notification> getAggregated() {
		return aggregated;
	}
	
	public int getCommentsCount() {
		// get aggregated count from all notifications
		int sum = 0;
		for (Notification notification: aggregated) {
			sum += notification.getCommentsCount();
		}
		return sum;
	}

}