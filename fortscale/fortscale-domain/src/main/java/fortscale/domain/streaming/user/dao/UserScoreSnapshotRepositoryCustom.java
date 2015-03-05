package fortscale.domain.streaming.user.dao;

public interface UserScoreSnapshotRepositoryCustom {
	void clearAllSnapshotsForClassifier(String classifierId);
}
