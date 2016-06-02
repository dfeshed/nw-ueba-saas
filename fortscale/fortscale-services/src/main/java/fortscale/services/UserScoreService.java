package fortscale.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.Severity;
import org.apache.commons.lang3.tuple.Pair;

public interface UserScoreService {
	


    double recalculateUserScore(String userName);
    double getUserScoreContributionForAlertSeverity(Severity severity, AlertFeedback feedback, long alertStartDate);

    void calculateUserSeverities(List<Pair<Double, Integer>> scoresHistogram);
    List<Pair<Double, Integer>> calculateAllUsersScores();

}
