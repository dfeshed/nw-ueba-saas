package fortscale.services.impl;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import fortscale.domain.core.User;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.IFeatureExplanation;
import fortscale.utils.impala.ImpalaParser;

public class ImpalaGroupsScoreWriter extends ImpalaWriter{
	
	public ImpalaGroupsScoreWriter(File file, ImpalaParser impalaParser){
		super(file, impalaParser);
	}
		
	public ImpalaGroupsScoreWriter(ImpalaParser impalaParser) {
		super(impalaParser);
	}

	public void writeScore(User user, AdUserFeaturesExtraction extraction, double avgScore){
		Date timestamp = extraction.getTimestamp();
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s|%s", getRuntime(timestamp), user.getId(),user.getAdDn(), user.getUsername(), extraction.getScore(), avgScore,getRundate(timestamp));
		for(IFeature feature: extraction.getAttributes()){
			write(csvLineString);
			writeFeature(feature);
			newLine();
		}
	}
	
	public void writeFeature(IFeature feature){
		IFeatureExplanation explanation = feature.getFeatureExplanation();
		String ref = "";
		String refs = "";
		if(explanation.getFeatureReference().length > 0){
			ref = explanation.getFeatureReference()[0];
			refs = StringUtils.join(explanation.getFeatureReference(), ",");
		}
		String csvLineString = String.format("|%s|%s|%s|%s|%s|%s", feature.getFeatureUniqueName(), feature.getFeatureScore(),
				explanation.getFeatureDistribution(), explanation.getFeatureCount(), ref, refs);
		write(csvLineString);
	}
	
}
