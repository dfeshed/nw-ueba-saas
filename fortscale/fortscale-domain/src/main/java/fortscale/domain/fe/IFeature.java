package fortscale.domain.fe;

import java.util.Comparator;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.data.domain.Sort.Direction;

@JsonDeserialize(as=ADFeature.class)
public interface IFeature {

	public String getFeatureUniqueName();

	public String getFeatureDisplayName();

	public Double getFeatureValue();

	public Double getFeatureScore();
	
	public Boolean getIsGroupDistributionList();
	
	public IFeatureExplanation getFeatureExplanation();
	
	public static class OrderByFeatureScore implements Comparator<IFeature>{
		private int isDesc = 1;
		
		public OrderByFeatureScore(Direction direction){
			if(Direction.DESC.equals(direction)){
				this.isDesc = 1;
			} else{
				this.isDesc = -1;
			}
		}

		@Override
		public int compare(IFeature o1, IFeature o2) {
			int ret = o2.getFeatureScore() > o1.getFeatureScore() ? 1 : (o2.getFeatureScore() < o1.getFeatureScore() ? -1 : 0);
			return ret * isDesc;
		}
		
	}
	
	public static class OrderByFeatureUniqueName implements Comparator<IFeature>{
		private int isDesc = 1;
		
		public OrderByFeatureUniqueName(Direction direction){
			if(Direction.DESC.equals(direction)){
				this.isDesc = 1;
			} else{
				this.isDesc = -1;
			}
		}

		@Override
		public int compare(IFeature o1, IFeature o2) {
			int ret = o2.getFeatureUniqueName().compareTo(o1.getFeatureUniqueName());
			return ret * isDesc;
		}
		
	}
	
	public static class OrderByFeatureDescription implements Comparator<IFeature>{
		private int isDesc = 1;
		
		public OrderByFeatureDescription(Direction direction){
			if(Direction.DESC.equals(direction)){
				this.isDesc = 1;
			} else{
				this.isDesc = -1;
			}
		}

		@Override
		public int compare(IFeature o1, IFeature o2) {
			int ret = o2.getFeatureExplanation().getDescription().compareTo(o1.getFeatureExplanation().getDescription());
			return ret * isDesc;
		}
		
	}
	
	public static class OrderByFeatureExplanationDistribution implements Comparator<IFeature>{
		private int isDesc = 1;
		
		public OrderByFeatureExplanationDistribution(Direction direction){
			if(Direction.DESC.equals(direction)){
				this.isDesc = 1;
			} else{
				this.isDesc = -1;
			}
		}

		@Override
		public int compare(IFeature o1, IFeature o2) {
			double dist1 = o1.getFeatureExplanation().getFeatureDistribution();
			double dist2 = o2.getFeatureExplanation().getFeatureDistribution();
			
			int ret = dist2 > dist1 ? 1 : (dist2 < dist1 ? -1 : 0);
			return ret * isDesc;
		}
		
	}
	
	public static class OrderByFeatureExplanationCount implements Comparator<IFeature>{
		private int isDesc = 1;
		
		public OrderByFeatureExplanationCount(Direction direction){
			if(Direction.DESC.equals(direction)){
				this.isDesc = 1;
			} else{
				this.isDesc = -1;
			}
		}

		@Override
		public int compare(IFeature o1, IFeature o2) {
			double count1 = o1.getFeatureExplanation().getFeatureCount();
			double count2 = o2.getFeatureExplanation().getFeatureCount();
			
			int ret = count2 > count1 ? 1 : (count2 < count1 ? -1 : 0);
			return ret * isDesc;
		}
		
	}
	
	
}
