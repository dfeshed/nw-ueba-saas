package fortscale.services.impl;

import java.io.File;
import java.util.Date;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.impl.ImpalaWriter;

public class ImpalaTotalScoreWriter {
	private ImpalaWriter writer;
	
	public ImpalaTotalScoreWriter(File file){
		this.writer = new ImpalaWriter(file);
	}
	
	public void close(){
		writer.close();
	}
	
	public void writeScore(User user){
		writeTotalScore(user);
		writeScore(user, Classifier.groups.getId());
		writeScore(user, Classifier.auth.getId());
		writeScore(user, Classifier.vpn.getId());
		writer.newLine();
	}
	
	private void writeTotalScore(User user){
		ClassifierScore classifierScore = user.getScore(Classifier.total.getId());
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s",getRuntime(classifierScore.getTimestamp()),user.getId(),user.getAdDn(), user.getUsername(), classifierScore.getScore(), classifierScore.getAvgScore());
		writer.write(csvLineString);
	}
	
	private void writeScore(User user, String classifierId){
		ClassifierScore classifierScore = user.getScore(classifierId);
		if(classifierScore == null){
			writer.write("| | | ");
		} else{
			String csvLineString = String.format("|%s|%s|%s",getRuntime(classifierScore.getTimestamp()), classifierScore.getScore(), classifierScore.getAvgScore());
			writer.write(csvLineString);
		}
	}
	
	private long getRuntime(Date timestamp){
		return timestamp.getTime()/1000;
	}
}
