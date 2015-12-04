package fortscale.collection.tagging.service;


public interface UserTagService {
	void update() throws Exception;
	boolean isUserTagged(String username);
	String getTagMongoField();
	void addUserTag(String userName);
	void removeUserTag(String userName);
	UserTagEnum getTag();
}
