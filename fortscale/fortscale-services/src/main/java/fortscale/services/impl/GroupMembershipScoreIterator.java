package fortscale.services.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import fortscale.domain.core.User;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.IGroupMembershipScore;
import fortscale.utils.impala.ImpalaDateTime;
import fortscale.utils.impala.ImpalaParser;

public class GroupMembershipScoreIterator implements IGroupMembershipScore{
	private User user;
	private AdUserFeaturesExtraction extraction;
	private double avgScore;
	private Long runtime;
	private int groupIndex = -1;
	private IFeature feature = null;
	private ImpalaDateTime timestamp;
	
	public GroupMembershipScoreIterator(ImpalaParser impalaParser, User user, AdUserFeaturesExtraction extraction, double avgScore){
		this.user = user;
		this.extraction = extraction;
		this.avgScore = avgScore;
		this.runtime = impalaParser.getRuntime(extraction.getTimestamp());
		this.timestamp = new ImpalaDateTime(new DateTime(extraction.getTimestamp()));
	}
	
	public boolean hasNext(){
		return (groupIndex + 1) < extraction.getAttributes().size();
	}
	
	public IGroupMembershipScore next(){
		groupIndex++;
		feature = extraction.getAttributes().get(groupIndex);
		return this;
	}
	
	@Override
	public Long getRuntime(){
		return runtime;
	}
	@Override
	public String getUid(){
		return user.getId();
	}
	@Override
	public String getDn(){
		return user.getAdInfo().getDn();
	}
	@Override
	public String getUsername(){
		return user.getUsername();
	}
	@Override
	public Double getScore(){
		return extraction.getScore();
	}
	@Override
	public Double getAvgScore(){
		return avgScore;
	}
	@Override
	public ImpalaDateTime getTime_stamp(){
		return timestamp;
	}
	@Override
	public String getGroup_dn(){
		return feature.getFeatureUniqueName();
	}
	@Override
	public Double getFdist(){
		Double dist = null;
		if(feature.getFeatureExplanation() != null){
			dist = feature.getFeatureExplanation().getFeatureDistribution();
		}
		
		return dist;
	}
	@Override
	public Integer getFcount(){
		Integer count = null;
		if(feature.getFeatureExplanation() != null){
			count = feature.getFeatureExplanation().getFeatureCount();
		}
		
		return count;
	}
	@Override
	public String getFref(){
		String ref = null;
		if(feature.getFeatureExplanation() != null && feature.getFeatureExplanation().getFeatureReference().length > 0){
			ref = feature.getFeatureExplanation().getFeatureReference()[0];
		}
		
		return ref;
	}
	@Override
	public String getFrefs(){
		String refs = null;
		if(feature.getFeatureExplanation() != null && feature.getFeatureExplanation().getFeatureReference().length > 0){
			refs = StringUtils.join(feature.getFeatureExplanation().getFeatureReference(), ",");
		}
		
		return refs;
	}
}
