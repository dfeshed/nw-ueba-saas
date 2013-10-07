package fortscale.services.fe;

public interface IScoreDistribution {
	public String getName();
	public int getCount();
	public int getPercentage();
	public int getLowestScore();
	public int getHighestScore();
}
