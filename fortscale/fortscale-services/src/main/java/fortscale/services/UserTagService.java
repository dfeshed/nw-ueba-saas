package fortscale.services;


import fortscale.domain.core.UserTagEnum;

public interface UserTagService {
	void update() throws Exception;
	boolean isUserTagged(String username, String tag);
	void addUserTag(String userName, String tag);
	void removeUserTag(String userName, String tag);
	UserTagEnum getTag();
}
