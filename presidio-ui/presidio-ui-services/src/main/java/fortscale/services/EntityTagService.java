package fortscale.services;

import java.util.List;

public interface EntityTagService {

	void update() throws Exception;
	void addEntityTags(String userName, List<String> tags) throws Exception;
	void addEntityTagsRegex(String userRegex, List<String> tags) throws Exception;
	void removeEntityTags(String userName, List<String> tags);
	int removeTagFromAllEntities(String tag);
}