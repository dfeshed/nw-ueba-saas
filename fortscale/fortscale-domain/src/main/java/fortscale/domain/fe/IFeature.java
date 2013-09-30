package fortscale.domain.fe;

import java.util.Comparator;

public interface IFeature {

	public String getFeatureUniqueName();

	public String getFeatureDisplayName();

	public Double getFeatureValue();

	public Double getFeatureScore();
	
	public static class OrderByScoreDesc implements Comparator<IFeature>{

		@Override
		public int compare(IFeature o1, IFeature o2) {
			return o2.getFeatureScore() > o1.getFeatureScore() ? 1 : (o2.getFeatureScore() < o1.getFeatureScore() ? -1 : 0);
		}
		
	}
}
