package fortscale.streaming.scorer;

import java.util.List;

public class FeatureScore {
	private String name;
	private Double score;
	private List<FeatureScore> featureScores;
	
	public FeatureScore(String name, Double score){
		this.name = name;
		this.score = score;
	}
	
	public FeatureScore(String name, Double score, List<FeatureScore> featureScores){
		this.name = name;
		this.score = score;
		this.featureScores = featureScores;
	}
	
	public String getName() {
		return name;
	}
	public Double getScore() {
		return score;
	}
	public List<FeatureScore> getFeatureScores() {
		return featureScores;
	}
	
	public double getCertainty() {
		return 1.0d;
	}

}
