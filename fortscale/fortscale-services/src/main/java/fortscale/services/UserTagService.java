package fortscale.services;

public interface UserTagService {

	void update() throws Exception;
	void addUserTag(String userName, String tag);
	void removeUserTag(String userName, String tag);

}