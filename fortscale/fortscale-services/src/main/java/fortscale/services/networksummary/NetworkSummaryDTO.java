package fortscale.services.networksummary;

public class NetworkSummaryDTO {
	private long value;
	private long previousValue;
	
	public long getValue() {
		return value;
	}

	public long getPreviousValue() {
		return previousValue;
	}

	public NetworkSummaryDTO(long value, long previousValue) {
		super();
		this.value = value;
		this.previousValue = previousValue;
	}


	

}
