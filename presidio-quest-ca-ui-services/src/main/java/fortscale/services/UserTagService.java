package fortscale.services;

import java.util.List;

public interface UserTagService {

	void update() throws Exception;
	void addUserTags(String userName, List<String> tags) throws Exception;
	void addUserTagsRegex(String userRegex, List<String> tags) throws Exception;
	void removeUserTags(String userName, List<String> tags);
	int removeTagFromAllUsers(String tag);
}