package fortscale.domain.fe.dao;

public class EventLoginDayCount {
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_FAILURE = "FAILURE";
	private String day;
	private String status;
	private int count;
	
	public EventLoginDayCount(String day, String status, int count){
		this.day = day;
		this.status = status;
		this.count = count;
	}
	
	public String getDay() {
		return day;
	}
	public String getStatus() {
		return status;
	}
	public int getCount() {
		return count;
	}
}
