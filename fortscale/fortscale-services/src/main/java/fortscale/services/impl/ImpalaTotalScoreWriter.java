package fortscale.services.impl;

import java.io.File;
import java.util.Date;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.services.fe.Classifier;

public class ImpalaTotalScoreWriter extends ImpalaWriter{
	
	public ImpalaTotalScoreWriter(File file){
		super(file);
	}
	
	public void writeScore(User user, Date timestamp){
		writeTotalScore(user, timestamp);
		writeScore(user, Classifier.groups.getId());
		writeScore(user, Classifier.auth.getId());
		writeScore(user, Classifier.vpn.getId());
		newLine();
	}
	
	private void writeTotalScore(User user, Date timestamp){
		ClassifierScore classifierScore = user.getScore(Classifier.total.getId());
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s|%s", getRuntime(timestamp), getRuntime(classifierScore.getTimestamp()),user.getId(),
				user.getAdDn(), user.getUsername(), classifierScore.getScore(), classifierScore.getAvgScore());
		write(csvLineString);
	}
	
	private void writeScore(User user, String classifierId){
		ClassifierScore classifierScore = user.getScore(classifierId);
		if(classifierScore == null){
			write("| | | ");
		} else{
			String csvLineString = String.format("|%s|%s|%s",getRuntime(classifierScore.getTimestamp()), classifierScore.getScore(), classifierScore.getAvgScore());
			write(csvLineString);
		}
	}
}
