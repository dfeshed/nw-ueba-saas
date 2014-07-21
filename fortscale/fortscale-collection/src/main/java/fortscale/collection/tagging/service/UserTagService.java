package fortscale.collection.tagging.service;


public interface UserTagService {
	public void update() throws Exception;
	public boolean isUserTagged(String username);
}
