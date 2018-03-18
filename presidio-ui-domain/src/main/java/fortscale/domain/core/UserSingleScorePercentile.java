package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by shays on 29/05/2016.
 */
public class UserSingleScorePercentile {
    public static final String percentileField = "percentile";
    public static final String maxScoreInPercentileField = "maxScoreInPercentile";
    public static final String minScoreInPerecentileField = "minScoreInPerecentile";

    @Field(percentileField)
    private int percentile;

    @Field(maxScoreInPercentileField)
    private int maxScoreInPercentile;

    @Field(minScoreInPerecentileField)
    private int minScoreInPerecentile;



    public UserSingleScorePercentile() {
    }

    public int getPercentile() {
        return percentile;
    }

    public void setPercentile(int percentile) {
        this.percentile = percentile;
    }

    public int getMaxScoreInPercentile() {
        return maxScoreInPercentile;
    }

    public void setMaxScoreInPercentile(int maxScoreInPercentile) {
        this.maxScoreInPercentile = maxScoreInPercentile;
    }

    public int getMinScoreInPerecentile() {
        return minScoreInPerecentile;
    }

    public void setMinScoreInPerecentile(int minScoreInPerecentile) {
        this.minScoreInPerecentile = minScoreInPerecentile;
    }
}
