package fortscale.services;

import java.util.Date;

import fortscale.services.fe.Classifier;

public interface UserUpdateScoreService {
public void updateUserWithAuthScore(Classifier classifier);
	
	public void updateUserWithAuthScore(Classifier classifier, Date lastRun);
	
	public void updateUserWithVpnScore();
	
	public void updateUserWithVpnScore(Date lastRun);
	
	public void updateUserWithGroupMembershipScore();
	
	public void recalculateUsersScores();

	public void recalculateTotalScore();
	
	public void updateUserTotalScore();
}
