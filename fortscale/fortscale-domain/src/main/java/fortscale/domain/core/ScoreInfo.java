package fortscale.domain.core;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

public class ScoreInfo {
	public static final String scoreField = "score";
	public static final String trendField = "trend";
	
	@Indexed
	@Field(scoreField)
	private double score;
	private double avgScore;
	@Indexed
	@Field(trendField)
	private double trend = 0.0;
	private Date timestamp;
	
	
	
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getAvgScore() {
		return avgScore;
	}
	public void setAvgScore(double avgScore) {
		this.avgScore = avgScore;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public double getTrend() {
		return trend;
	}
	public void setTrend(double trend) {
		this.trend = trend;
	}
	
}
