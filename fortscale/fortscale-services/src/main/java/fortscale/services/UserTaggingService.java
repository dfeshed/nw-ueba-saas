package fortscale.services;

public interface UserTaggingService {

	void putUserTagService(String tag, UserTagService userTagService);
	UserTagService getUserTagService(String tag);
	void update(String tag) throws Exception;
	void updateAll() throws Exception;

}