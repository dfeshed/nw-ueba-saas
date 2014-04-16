package fortscale.domain.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class NotificationAggregate implements Serializable {

	private static final long serialVersionUID = 8732010071833651983L;
	
	private List<NotificationAggregateFsIdInfo> aggregated;
	private String generator_name;
	private String cause;
	private String type;
	private long ts;
	private Long eventsStart;
	private Long eventsEnd;
	private HashMap<String, Set<String>> aggAttributes;
	private int commentsCount;

	public NotificationAggregate(List<Notification> list) {
		Notification notification = list.get(0);
		generator_name = notification.getGenerator_name();

		setAggFsId(list);
		
		cause = notification.getCause();
		
		// get put the earliest timestamp in the aggregation timestamp
		ts = notification.getTs();
		for (Notification item : list)
			ts = Math.min(ts, item.getTs());
		
		
		aggAttributes = getAttributes(list);
		type = "agg";
		commentsCount = calcCommentsCount(list);
	}
	
	public void setAggFsId(List<Notification> list) {
		aggregated = new ArrayList<NotificationAggregateFsIdInfo>();
		
		Set<String> aggFsIdSet = new HashSet<>();
		for(Notification notification: list){
			if(!aggFsIdSet.contains(notification.getFsId())){
				aggFsIdSet.add(notification.getFsId());
				aggregated.add(new NotificationAggregateFsIdInfo(notification));
			}
		}		
	}

	public HashMap<String, Set<String>> getAggAttributes() {
		return aggAttributes;
	}

	private HashMap<String, Set<String>> getAttributes(List<Notification> aggregated) {
		HashMap<String, Set<String>> aggAtt = new HashMap<>();

		for (Notification n : aggregated) {
			if (n.getAttributes() != null) {
				Set<Entry<String, String>> entrySet = n.getAttributes().entrySet();
				for (Entry<String, String> entry : entrySet) {
					String key = entry.getKey();
					if (aggAtt.containsKey(key) == false) {
						aggAtt.put(key, new HashSet<String>());
					}
					aggAtt.get(key).add(entry.getValue());
				}
			}
			if (n.getEventsStart()!=null) {
				if (eventsStart==null || eventsStart > n.getEventsStart())
					eventsStart = n.getEventsStart();
			}
			if (n.getEventsEnd()!=null) {
				if (eventsEnd==null || eventsEnd < n.getEventsEnd())
					eventsEnd = n.getEventsEnd();
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

	public List<NotificationAggregateFsIdInfo> getAggregated() {
		return aggregated;
	}
	
	public int getCommentsCount() {
		return commentsCount;
	}
	
	private int calcCommentsCount(List<Notification> list) {
		// get aggregated count from all notifications
		int sum = 0;
		for (Notification notification: list) {
			sum += notification.getCommentsCount();
		}
		return sum;
	}

	public Long getEventsStart() {
		return eventsStart;
	}

	public Long getEventsEnd() {
		return eventsEnd;
	}

}