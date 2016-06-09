package fortscale.services;


public interface UserTaggingService {
	public void putUserTagService(String tag, UserTagService userTagService);
	public UserTagService getUserTagService(String tag);
	public void update(String tag) throws Exception;
	public void updateAll() throws Exception;
}
