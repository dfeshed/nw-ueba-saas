package fortscale.domain.tracer;

public class ScoreRange {

	private double minScore;
	private double maxScore;
	
	public ScoreRange() {}
	
	public ScoreRange(double minScore, double maxScore) {
		this.minScore = minScore;
		this.maxScore = maxScore;
	}
	
	public boolean isEmpty() {
		return minScore==0.0d && maxScore==0.0d;
	}
	
	public double getMinScore() {
		return minScore;
	}
	public void setMinScore(Double minScore) {
		this.minScore = minScore;
	}
	public double getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}
}
