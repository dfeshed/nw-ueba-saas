package fortscale.services;

import java.util.Date;

import fortscale.domain.core.User;

public interface UserUpdateScoreService {
	public User updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore, boolean isSaveMaxScore);
	
	public void recalculateTotalScore();
	
	public void updateUserTotalScore();
}
