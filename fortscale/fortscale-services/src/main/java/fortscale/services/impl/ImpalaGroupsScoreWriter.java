package fortscale.services.impl;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import fortscale.domain.core.User;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.IFeatureExplanation;
import fortscale.utils.hdfs.HDFSWriter;
import fortscale.utils.impala.ImpalaParser;

public class ImpalaGroupsScoreWriter extends ImpalaWriter{
	
	public ImpalaGroupsScoreWriter(File file, ImpalaParser impalaParser){
		super(file, impalaParser);
	}
		
	public ImpalaGroupsScoreWriter(ImpalaParser impalaParser) {
		super(impalaParser);
	}
	
	public ImpalaGroupsScoreWriter(HDFSWriter writer, ImpalaParser impalaParser) {
		super(writer, impalaParser);
	}

	public void writeScore(User user, AdUserFeaturesExtraction extraction, double avgScore){
		Date timestamp = extraction.getTimestamp();
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s|%s", getRuntime(timestamp), user.getId(),user.getAdInfo().getDn(), user.getUsername(), extraction.getScore(), avgScore,getRundate(timestamp));
		for(IFeature feature: extraction.getAttributes()){
			String featureLine = writeFeature(feature);
			writeLine(csvLineString + featureLine, getRuntime(timestamp));
		}
	}
	
	public String writeFeature(IFeature feature){
		IFeatureExplanation explanation = feature.getFeatureExplanation();
		String ref = "";
		String refs = "";
		String count = "";
		String dist = "";
		if(explanation != null){
			if(explanation.getFeatureReference().length > 0){
				ref = explanation.getFeatureReference()[0];
				refs = StringUtils.join(explanation.getFeatureReference(), ",");
			}
			count = explanation.getFeatureCount().toString();
			dist = explanation.getFeatureDistribution().toString();
		}
		String csvLineString = String.format("|%s|%s|%s|%s|%s|%s", feature.getFeatureUniqueName(), feature.getFeatureScore(),
				dist, count, ref, refs);
		return csvLineString;
	}
	
}
