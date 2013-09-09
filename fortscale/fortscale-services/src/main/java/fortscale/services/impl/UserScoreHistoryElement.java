package fortscale.services.impl;

import java.util.Date;

import fortscale.services.IUserScoreHistoryElement;

public class UserScoreHistoryElement implements IUserScoreHistoryElement {
	
	private Date date;
	private double score;
	private double avgScore;
	
	public UserScoreHistoryElement( Date date, double score, double avgScore){
		this.date = date;
		this.score = score;
		this.avgScore = avgScore;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public int getScore() {
		return (int) Math.round(score);
	}

	@Override
	public int getAvgScore() {
		return (int) Math.round(avgScore);
	}

}
