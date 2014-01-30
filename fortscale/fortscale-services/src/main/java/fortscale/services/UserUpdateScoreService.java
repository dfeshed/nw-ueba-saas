package fortscale.services;

import java.util.Date;

import fortscale.domain.core.User;
import fortscale.services.fe.Classifier;

public interface UserUpdateScoreService {
	public User updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore, boolean isToSave, boolean isSaveMaxScore);
	
	public void updateUserWithAuthScore(Classifier classifier);
	
	public void updateUserWithAuthScore(Classifier classifier, Date lastRun);
	
	public void updateUserWithVpnScore();
	
	public void updateUserWithVpnScore(Date lastRun);
	
	public void updateUserWithGroupMembershipScore();
	
	public void recalculateUsersScores();

	public void recalculateTotalScore();
	
	public void updateUserTotalScore();
}
