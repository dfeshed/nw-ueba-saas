package fortscale.domain.streaming.user.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.streaming.user.UserScoreSnapshot;

public interface UserScoreSnapshotRepository extends MongoRepository<UserScoreSnapshot, String>, UserScoreSnapshotCustom{
	UserScoreSnapshot findByUserNameAndClassifierId(String username, String classifierId);
}
