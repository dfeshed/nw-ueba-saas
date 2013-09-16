package fortscale.services.fe.impl;

import java.util.List;

import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.IScoreDistribution;

public class ClassifierScoreDistribution implements	IClassifierScoreDistribution {
	
	private String classifierId;
	private List<IScoreDistribution> dist;
	
	public ClassifierScoreDistribution(String classifierId, List<IScoreDistribution> dist){
		this.classifierId = classifierId;
		this.dist = dist;
	}

	@Override
	public String getClassifierId() {
		return classifierId;
	}

	@Override
	public List<IScoreDistribution> getDist() {
		return dist;
	}

	
}
