package fortscale.streaming.service;

import java.util.Map;
import java.util.Map.Entry;

import fortscale.services.UserService;
import fortscale.services.impl.SpringService;
import net.minidev.json.JSONObject;

/**
 * Add fields to events according to user tags 
 */
public class UserTagsService {

	private Map<String, String> tags;
	protected UserService userService;
	
	public UserTagsService(Map<String, String> tags) {
		this.tags = tags;
		userService = SpringService.getInstance().resolve(UserService.class);
	}
	
	public void addTagsToEvent(String username, JSONObject message) {
		// go over all tags and check if the user is tagged
		for (Entry<String, String> tag : tags.entrySet()) {
			String fieldName = tag.getValue();
			String tagName = tag.getKey();
			message.put(fieldName, userService.isUserTagged(username, tagName));
		}
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
