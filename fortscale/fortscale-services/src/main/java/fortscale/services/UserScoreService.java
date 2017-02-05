package fortscale.services;

import fortscale.domain.core.alert.Alert;
import fortscale.domain.core.alert.AlertFeedback;
import fortscale.domain.core.Severity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface UserScoreService {

    double recalculateUserScore(String userId);
    double getUserScoreContributionForAlertSeverity(Severity severity, AlertFeedback feedback, long alertStartDate);
    Alert updateAlertContirubtion(Alert alert);
    void calculateUserSeverities(List<Pair<Double, Integer>> scoresHistogram);
    List<Pair<Double, Integer>> calculateAllUsersScores();
    Severity getUserSeverityForScore(double userScore);
    Map<Severity, Double[]> getSeverityRange();
}
