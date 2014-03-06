package fortscale.domain.core;

public interface ITotalScore {
	public Long getRuntime();
	public Long getScore_runtime();
	public String getUsername();
	public String getScore_type();
	public Double getScore();
	public String getScore_explanation();
	public Double getAvg_score();
	public Double getTrend();
	public Double getWeight();
	public String getDn();
	public String getUserId();
}
