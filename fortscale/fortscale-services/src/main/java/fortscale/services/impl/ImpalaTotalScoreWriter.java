package fortscale.services.impl;

import java.io.File;
import java.util.Date;

import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.services.fe.Classifier;
import fortscale.utils.impala.ImpalaParser;

public class ImpalaTotalScoreWriter extends ImpalaWriter{
	
	private static ScoreWeight TOTAL_SCORE_WEIGHT = new ScoreWeight(Classifier.total.getId(), 0.0);
	
	public ImpalaTotalScoreWriter(File file, ImpalaParser impalaParser){
		super(file, impalaParser);
	}
	
	public ImpalaTotalScoreWriter(ImpalaParser impalaParser) {
		super(impalaParser);
	}
	
	public ImpalaTotalScoreWriter(HDFSWriter writer, ImpalaParser impalaParser) {
		super(writer, impalaParser);
	}

	public void writeScores(User user, Date timestamp, ScoreConfiguration scoreConfiguration){
		writeScore(user, TOTAL_SCORE_WEIGHT, timestamp, getTotalScoreExplanation(user, scoreConfiguration));
		for(ScoreWeight scoreWeight: scoreConfiguration.getConfMap().values()){
			writeScore(user, scoreWeight, timestamp, " ");
		}
	}
	
	private String getTotalScoreExplanation(User user, ScoreConfiguration scoreConfiguration){
		StringBuilder builder = new StringBuilder();
		for(ScoreWeight scoreWeight: scoreConfiguration.getConfMap().values()){
			appendClassifierScoreWeightDescription(builder, user, scoreWeight);
		}
		return builder.toString();
	}
	
	private void appendClassifierScoreWeightDescription(StringBuilder builder, User user, ScoreWeight scoreWeight){
		ClassifierScore classifierScore = user.getScore(scoreWeight.getId());
		if(classifierScore == null){
			return;
		}
		builder.append(scoreWeight.getId()).append("(score = ").append(classifierScore.getScore()).append(", weight = ").append(scoreWeight.getWeight()).append(")").append("   ");
	}
	
	private void writeScore(User user, ScoreWeight scoreWeight, Date timestamp, String scoreExplanation){
		ClassifierScore classifierScore = user.getScore(scoreWeight.getId());
		if(classifierScore == null){
			return;
		}
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s", getRuntime(timestamp), getRuntime(classifierScore.getTimestamp()),
				user.getUsername(), scoreWeight.getId(), classifierScore.getScore(), scoreExplanation, classifierScore.getAvgScore(), classifierScore.getTrend(), scoreWeight.getWeight(),
				user.getAdInfo().getDn(), user.getId());
		write(csvLineString);
		newLine();
	}
	
//	private void writeScore(User user, String classifierId){
//		ClassifierScore classifierScore = user.getScore(classifierId);
//		if(classifierScore == null){
//			write("| | | ");
//		} else{
//			String csvLineString = String.format("|%s|%s|%s",getRuntime(classifierScore.getTimestamp()), classifierScore.getScore(), classifierScore.getAvgScore());
//			write(csvLineString);
//		}
//	}
}
