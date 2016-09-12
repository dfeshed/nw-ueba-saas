package fortscale.services;

import java.util.List;

public interface UserTagService {

	void update() throws Exception;
	void addUserTags(String userName, List<String> tags);
	void removeUserTags(String userName, List<String> tags);

}