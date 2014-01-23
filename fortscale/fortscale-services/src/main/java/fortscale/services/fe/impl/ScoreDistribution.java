package fortscale.services.fe.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import fortscale.services.fe.IScoreDistribution;

public class ScoreDistribution implements	IScoreDistribution {
	
	private String name;
	private int count;
	private int percentage;
	private int lowestScore;
	private int highestScore;
	
	public ScoreDistribution(String name, int count, int percentage, int lowestScore, int highestScore){
		this.name = name;
		this.count = count;
		this.percentage = percentage;
		this.lowestScore = lowestScore;
		this.highestScore = highestScore;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public int getPercentage() {
		return percentage;
	}

	@Override
	public int getLowestScore() {
		return lowestScore;
	}

	@Override
	public int getHighestScore() {
		return highestScore;
	}

	@Override
    public boolean equals(Object obj) {
            if(obj == null) return false;
            if(obj == this) return true;
            if(!(obj instanceof ScoreDistribution)) return false;
            ScoreDistribution scoreDistribution = (ScoreDistribution)obj;
            return new EqualsBuilder().append(scoreDistribution.getName(), getName()).append(scoreDistribution.getCount(), getCount()).append(scoreDistribution.getPercentage(), getPercentage()).
            		append(scoreDistribution.getLowestScore(), getLowestScore()).append(scoreDistribution.getHighestScore(), getHighestScore()).isEquals();
    }
    @Override
    public int hashCode() {
            return new HashCodeBuilder().append(getName()).append(getCount()).toHashCode();
    }
}
