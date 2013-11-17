package fortscale.domain.fe;

import org.codehaus.jackson.map.annotate.JsonDeserialize;


@JsonDeserialize(as=FeatureExplanation.class)
public interface IFeatureExplanation {
	public Double getFeatureDistribution();	
	public String[] getFeatureReference();
	public Integer getFeatureCount();
	public String getFeatureDescription();
}
