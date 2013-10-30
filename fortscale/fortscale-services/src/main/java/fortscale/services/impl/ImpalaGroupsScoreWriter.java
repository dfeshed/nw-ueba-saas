package fortscale.services.impl;

import java.io.File;
import java.util.Date;

import fortscale.domain.core.User;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.services.fe.impl.ImpalaWriter;

public class ImpalaGroupsScoreWriter {

	private ImpalaWriter writer;
	
	public ImpalaGroupsScoreWriter(File file){
		this.writer = new ImpalaWriter(file);
	}
	
	public void close(){
		writer.close();
	}
	
	public void writeScore(User user, AdUserFeaturesExtraction extraction, double avgScore){
		writeScore(extraction.getTimestamp(), user, extraction.getScore(), avgScore);
	}
	
	public void writeScore(Date timestamp, User user, double score, double avgScore){
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s",timestamp.getTime()/1000,user.getId(),user.getAdDn(), user.getAdUserPrincipalName(), score, avgScore);
		writer.write(csvLineString);
		writer.newLine();
	}
}
