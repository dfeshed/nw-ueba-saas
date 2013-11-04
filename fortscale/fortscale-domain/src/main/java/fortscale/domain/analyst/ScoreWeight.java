package fortscale.domain.analyst;

public class ScoreWeight {
	private final String id;
	private final double weight;
	
	
	public ScoreWeight(String id, double weight){
		this.id = id;
		this.weight = weight;
	}
	
	public String getId() {
		return id;
	}

	public double getWeight() {
		return weight;
	}
	
	
}
