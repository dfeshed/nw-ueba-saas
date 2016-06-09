package fortscale.services;

import fortscale.domain.core.UserTagEnum;

public interface UserTagService {

	void update() throws Exception;
	void addUserTag(String userName, String tag);
	void removeUserTag(String userName, String tag);
	UserTagEnum getTag();
	//for testing purposes
	boolean isUserTagged(String username, String tag);

}