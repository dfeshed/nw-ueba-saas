package fortscale.utils;

/**
 * Created by danal on 12/02/2015.
 */
public class TimeRange {

	private Long startTimestamp;

	private Long endTimestamp;

	public TimeRange(Long startTimestamp, Long endTimestamp) {
		if (startTimestamp != null && endTimestamp != null && endTimestamp < startTimestamp){
			throw new IllegalArgumentException("start timestamp should be before end timestamp");
		}
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}

	public Long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TimeRange timeRange = (TimeRange) o;

		if (endTimestamp != null ? !endTimestamp.equals(timeRange.endTimestamp) : timeRange.endTimestamp != null)
			return false;
		if (startTimestamp != null ? !startTimestamp.equals(timeRange.startTimestamp) : timeRange.startTimestamp != null)
			return false;

		return true;
	}

	@Override public int hashCode() {
		int result = startTimestamp != null ? startTimestamp.hashCode() : 0;
		result = 31 * result + (endTimestamp != null ? endTimestamp.hashCode() : 0);
		return result;
	}

	public boolean include(Long ts){
		boolean isBetween = false;
		if (startTimestamp != null && endTimestamp != null &&  startTimestamp <= ts && ts <= endTimestamp){
			isBetween =  true;
		}
		else if (startTimestamp == null  && endTimestamp != null && ts <= endTimestamp){
			isBetween = true;
		}
		else if (startTimestamp != null && endTimestamp == null && startTimestamp <= ts){
			isBetween =  true;
		}
		else if (startTimestamp == null && endTimestamp == null){
			isBetween = true;
		}
		return isBetween;
	}

	public boolean before(TimeRange timerange){
		return endTimestamp != null && timerange.startTimestamp != null && endTimestamp < timerange.startTimestamp;
	}

	public boolean after(TimeRange timerange){
		return timerange.endTimestamp != null && startTimestamp != null && timerange.endTimestamp < startTimestamp;
	}

	public boolean intersect(TimeRange timerange){
		return ! before(timerange) && ! after(timerange);
	}

}
