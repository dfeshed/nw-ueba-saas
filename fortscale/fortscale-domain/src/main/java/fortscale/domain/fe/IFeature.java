package fortscale.domain.fe;

public interface IFeature {

	public String getFeatureUniqueName();

	public String getFeatureDisplayName();

	public Double getFeatureValue();

	public Double getFeatureScore();
}
