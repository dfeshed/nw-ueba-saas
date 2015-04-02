package fortscale.streaming.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.*;
import java.util.Map.Entry;

/*
 * AmtSession represents a user's AMT session.
 * It collects information about the user that will aggregate until the session ends.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AmtSession {
	public static long staleYidSessionTimeoutMillis = 2 * 60 * 60 * 1000L;

	public enum ActionType {Sensitive, Failed}

	private String username;
	private String normalizedUsername;
	private long startTimeUnix;
	private long endTimeUnix;
	private long duration;
	private long lastUpdated;
	private boolean closed;
	private boolean vipYid;
	private int sessionCountTillNow;
	private int yidCountInSession;
	private double avgTimeOnYid;
	private double avgYidCountInSession;
	private Map<ActionType, Integer> actionTypesCount;
	private Map<String, AmtYidStat> yids;
	private Map<String, Integer> hostCounts;
	private Map<String, Integer> ipCounts;
	// true if the session has at least one real action
	// (i.e. not just 'start session'), false otherwise
	private boolean hasRealActions;

	@JsonCreator
	public AmtSession(@JsonProperty("username") String username, @JsonProperty("normalizedUsername") String normalizedUsername) {
		this.username = username;
		this.normalizedUsername = normalizedUsername;
		this.sessionCountTillNow = 1;
		this.yidCountInSession = 0;
		this.startTimeUnix = 0;
		this.endTimeUnix = 0;
		this.duration = 0;
		this.actionTypesCount = new HashMap<>();
		this.yids = new LinkedHashMap<>();
		this.hostCounts = new HashMap<>();
		this.ipCounts = new HashMap<>();
		this.lastUpdated = System.currentTimeMillis();
		this.hasRealActions = false;
	}

	public AmtSession(String username, String normalizedUsername, long timestamp, String yid, int previousSessionCount, double avgYidCountInSession) {
		this(username, normalizedUsername);

		this.startTimeUnix = timestamp;
		this.endTimeUnix = timestamp;
		this.sessionCountTillNow = previousSessionCount + 1;
		this.avgYidCountInSession = avgYidCountInSession;

		addYid(yid, timestamp);
	}

	public void addYid(String yid, long timestamp) {
		// Cleanup old YIDs to reduce state size and to avoid long YID sessions
		removeOutstandingYids(timestamp);

		if (StringUtils.isNotEmpty(yid)) {
			// Check if there's an open session with the given YID
			if (yids.containsKey(yid)) {
				// Update the end time of this existing YID
				AmtYidStat stat = yids.get(yid);
				stat.updateEndTime(timestamp);

				// Remove and re-add the stat to the map, so it'll be first in the list
				yids.remove(yid);
				yids.put(yid, stat);
			} else {
				// Create a new YID stat in the session
				yids.put(yid, new AmtYidStat(timestamp));
			}
		}
	}

	/*
	 * Cleanup all YIDs that haven't been touched in the last 2 hours
	 */
	private void removeOutstandingYids(long currentTimestamp) {
		long minTimestampMillis = currentTimestamp * 1000 - staleYidSessionTimeoutMillis;

		int counter = 0;
		long durationSum = 0;
		List<String> yidsToRemove = new LinkedList<>();

		// Sum up duration of (new) untouched YIDs and count them
		for (Entry<String, AmtYidStat> entry : yids.entrySet()) {
			AmtYidStat stat = entry.getValue();
			if (stat.getEndTime() * 1000 < minTimestampMillis) {
				counter++;
				durationSum += stat.getDurationMillis();
				yidsToRemove.add(entry.getKey());
			} else
				break;
		}

		// Update global average and counter
		if (counter > 0) {
			avgTimeOnYid = (avgTimeOnYid * yidCountInSession + durationSum) / (yidCountInSession + counter);
			yidCountInSession += counter;
		}

		for (String yid : yidsToRemove)
			yids.remove(yid);
	}

	public String getUsername() {
		return username;
	}

	public String getNormalizedUsername() {
		return normalizedUsername;
	}

	public long getStartTimeUnix() {
		return startTimeUnix;
	}

	public String getStartDate() {
		return formatTime(startTimeUnix);
	}

	public long getEndTimeUnix() {
		return endTimeUnix;
	}

	public String getEndDate() {
		return formatTime(endTimeUnix);
	}

	public void setEndTimeUnix(long endTime) {
		endTimeUnix = endTime;
		duration = endTimeUnix - startTimeUnix;
		lastUpdated = System.currentTimeMillis();
	}

	public double getDuration() {
		// Return duration in hours, with 2 digits after the decimal point
		return roundTo2Digits((double)duration / (60 * 60));
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public boolean isClosed() {
		return closed;
	}

	public double getAverageTimeInYid() {
		// Return time in minutes
		return roundTo2Digits(avgTimeOnYid / (60 * 1000));
	}

	public void closeSession() {
		closed = true;
		calculateAvgTimeOnYid();
	}

	/*
	 * Go over all the YIDs in the session and calculate the average duration on a YID
	 */
	private void calculateAvgTimeOnYid() {
		Collection<AmtYidStat> values = yids.values();
		long durationSum = 0;

		for (AmtYidStat yid : values)
			durationSum += yid.getDurationMillis();

		if (values.size() > 0) {
			avgTimeOnYid = (avgTimeOnYid * yidCountInSession + durationSum) / (yidCountInSession + values.size());
			yidCountInSession += values.size();
		}
	}

	public boolean isVipYid() {
		return vipYid;
	}

	public void markVipYid() {
		vipYid = true;
	}

	public int getSessionsCount() {
		return sessionCountTillNow;
	}

	public int getYidCount() {
		return yidCountInSession;
	}

	public double getYidRate() {
		return roundTo2Digits((getDuration() == 0) ? 0.0 : yidCountInSession / getDuration());
	}

	public double getAverageYids() {
		return roundTo2Digits((getYidRate() + avgYidCountInSession * (sessionCountTillNow - 1)) / sessionCountTillNow);
	}

	public boolean isHasRealActions() {
		return hasRealActions;
	}

	public void setHasRealActions(boolean hasRealActions) {
		this.hasRealActions = hasRealActions;
	}

	public int getActionTypeCount(ActionType actionType) {
		return actionType != null && actionTypesCount.get(actionType) != null ? actionTypesCount.get(actionType) : 0;
	}

	public void incActionTypeCount(ActionType actionType) {
		Integer count = actionTypesCount.get(actionType);
		if (count != null)
			actionTypesCount.put(actionType, ++count);
		else
			actionTypesCount.put(actionType, 1);
	}

	public String getDominantHostname() {
		int maxCount = 0;
		String maxHostname = null;

		for (Entry<String, Integer> entry : hostCounts.entrySet()) {
			if (maxCount < entry.getValue()) {
				maxHostname = entry.getKey();
				maxCount = entry.getValue();
			}
		}

		return maxHostname;
	}

	public void addHostname(String hostname) {
		if (StringUtils.isNotEmpty(hostname)) {
			Integer count = hostCounts.get(hostname);
			count = (count == null) ? 1 : count + 1;
			hostCounts.put(hostname, count);
		}
	}

	public String getDominantIp() {
		int maxCount = 0;
		String maxIp = null;

		for (Entry<String, Integer> entry : ipCounts.entrySet()) {
			if (maxCount < entry.getValue()) {
				maxIp = entry.getKey();
				maxCount = entry.getValue();
			}
		}

		return maxIp;
	}

	public void addIpAddress(String ip) {
		if (StringUtils.isNotBlank(ip)) {
			Integer count = ipCounts.get(ip);
			count = (count == null) ? 1 : count + 1;
			ipCounts.put(ip, count);
		}
	}

	private static String formatTime(long instant) {
		DateTime when = new DateTime(TimestampUtils.convertToMilliSeconds(instant), DateTimeZone.forID("UTC"));
		return when.toString("yyyy-MM-dd HH:mm:ss");
	}

	private static double roundTo2Digits(double value) {
		return (double)Math.round(value * 100) / 100;
	}
}
