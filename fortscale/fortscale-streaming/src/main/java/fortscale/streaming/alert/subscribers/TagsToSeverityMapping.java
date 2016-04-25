package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Severity;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by shays on 05/04/2016.
 */
public class TagsToSeverityMapping implements InitializingBean {

    private int minScoreForLow;
    private int minScoreForMedium;
    private int minScoreForHigh;
    private int minScoreForCritical;

    /**
     * Keeps mapping between score and severity
     */
    private NavigableMap<Integer,Severity> scoreToSeverity = new TreeMap<>();

    public TagsToSeverityMapping(int minScoreForLow, int minScoreForMedium, int minScoreForHigh, int minScoreForCritical) {
        this.minScoreForLow = minScoreForLow;
        this.minScoreForMedium = minScoreForMedium;
        this.minScoreForHigh = minScoreForHigh;
        this.minScoreForCritical = minScoreForCritical;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // init scoring to severity map
        scoreToSeverity.put(0, Severity.Low);
        scoreToSeverity.put(minScoreForLow, Severity.Low);
        scoreToSeverity.put(minScoreForMedium, Severity.Medium);
        scoreToSeverity.put(minScoreForHigh, Severity.High);
        scoreToSeverity.put(minScoreForCritical, Severity.Critical);

    }

    /**
     * @param  roundedScore
     * @return the severity relevant to the score and isRestrictedTag
     */
    public Severity getSeverityByScore(int roundedScore){
        return scoreToSeverity.floorEntry(roundedScore).getValue();
    }


}
