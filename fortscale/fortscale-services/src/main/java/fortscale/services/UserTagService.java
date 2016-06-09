package fortscale.services;


public interface UserTagService {
	void update() throws Exception;
	boolean isUserTagged(String username, String tag);
	String getTagMongoField();
	void addUserTag(String userName, String tag);
	void removeUserTag(String userName, String tag);
	UserTagEnum getTag();
}
