package fortscale.collection.tagging.service;


public interface UserTagService {
	void update() throws Exception;
	boolean isUserTagged(String username);
	String getTagMongoField();
	UserTagEnum getTag();
}
