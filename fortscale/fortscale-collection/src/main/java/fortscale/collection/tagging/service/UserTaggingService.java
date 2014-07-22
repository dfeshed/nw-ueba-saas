package fortscale.collection.tagging.service;


public interface UserTaggingService {
	public void putUserTagService(String tag, UserTagService userTagService);
	public void update(String tag) throws Exception;
	public void updateAll() throws Exception;
	public boolean isUserTagged(String tag, String username) throws Exception;
}
