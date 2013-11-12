package fortscale.services.impl;

import java.io.File;
import java.util.Date;

import fortscale.domain.core.User;
import fortscale.domain.fe.AdUserFeaturesExtraction;

public class ImpalaGroupsScoreWriter extends ImpalaWriter{
	
	public ImpalaGroupsScoreWriter(File file){
		super(file);
	}
		
	public ImpalaGroupsScoreWriter() {}

	public void writeScore(User user, AdUserFeaturesExtraction extraction, double avgScore){
		writeScore(extraction.getTimestamp(), user, extraction.getScore(), avgScore);
	}
	
	public void writeScore(Date timestamp, User user, double score, double avgScore){
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s",getRuntime(timestamp),user.getId(),user.getAdDn(), user.getUsername(), score, avgScore);
		write(csvLineString);
		newLine();
	}
}
