package fortscale.domain.core;

import java.util.Date;

public class ScoreInfo {
	private double score;
	private double avgScore;
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
}
