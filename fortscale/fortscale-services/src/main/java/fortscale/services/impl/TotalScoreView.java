package fortscale.services.impl;

import java.util.Date;

import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ITotalScore;
import fortscale.domain.core.User;
import fortscale.utils.impala.ImpalaParser;

public class TotalScoreView implements ITotalScore {
	
	private User user;
	private ScoreWeight scoreWeight;
	private String scoreExplanation;
	private ClassifierScore classifierScore;
	private Long runtime;
	private Long scoreRuntime;
	
	public TotalScoreView(ImpalaParser impalaParser, User user, ScoreWeight scoreWeight, Date timestamp, String scoreExplanation, ClassifierScore classifierScore){
		this.user = user;
		this.scoreExplanation = scoreExplanation;
		this.scoreWeight = scoreWeight;
		this.classifierScore = classifierScore;
		runtime = impalaParser.getRuntime(timestamp);
		scoreRuntime = impalaParser.getRuntime(classifierScore.getTimestamp());
	}

	@Override
	public Long getRuntime() {
		return runtime;
	}

	@Override
	public Long getScore_runtime() {
		return scoreRuntime;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public String getScore_type() {
		return scoreWeight.getId();
	}

	@Override
	public Double getScore() {
		return classifierScore.getScore();
	}

	@Override
	public String getScore_explanation() {
		return scoreExplanation;
	}

	@Override
	public Double getAvg_score() {
		return classifierScore.getAvgScore();
	}

	@Override
	public Double getTrend() {
		return classifierScore.getTrend();
	}

	@Override
	public Double getWeight() {
		return scoreWeight.getWeight();
	}

	@Override
	public String getDn() {
		return user.getAdInfo().getDn();
	}

	@Override
	public String getUserId() {
		return user.getId();
	}

}
