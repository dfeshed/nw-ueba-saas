package fortscale.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fortscale.domain.core.UserSingleScorePercentile;
import fortscale.domain.dto.AlertWithUserScore;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.core.User;
import fortscale.domain.fe.IFeature;

public interface UserScoreService {
	

    List<AlertWithUserScore> getAlertsWithUserScore(String userName);
    double recalculateUserScore(String userName);

    void calculateUserSeverities(List<Pair<Double, Integer>> scoresHistogram);
    List<Pair<Double, Integer>> calculateAllUsersScores();

}
