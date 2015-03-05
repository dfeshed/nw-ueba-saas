package fortscale.domain.streaming.user.dao;

public interface UserScoreSnapshotRepositoryCustom {
	void removeAllSnapshotsForClassifier(String classifierId);
}
