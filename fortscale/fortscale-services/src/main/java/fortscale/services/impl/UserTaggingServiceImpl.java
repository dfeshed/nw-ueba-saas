package fortscale.services.impl;

import fortscale.services.UserTagService;
import fortscale.services.UserTaggingService;
import fortscale.utils.logging.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("userTaggingService")
public class UserTaggingServiceImpl implements UserTaggingService {

	private static Logger logger = Logger.getLogger(UserTaggingServiceImpl.class);

	private Map<String, UserTagService> userTagServiceMap = new HashMap();
	
	@Override
	public void putUserTagService(String tag, UserTagService userTagService) {
		userTagServiceMap.put(tag, userTagService);
	}

	@Override
	public UserTagService getUserTagService(String tag) {
		return userTagServiceMap.get(tag);
	}

	@Override
	public void update(String tag) throws Exception {
		UserTagService userTagService = userTagServiceMap.get(tag);
		if (userTagService != null) {
			userTagService.update();
		} else {
			logger.error("there is no userTagService for the tag ({})", tag);
			throw new Exception(String.format("there is no userTagService for the tag (%s)", tag));
		}
	}

	@Override
	public void updateAll() throws Exception {
		for (UserTagService userTagService: userTagServiceMap.values()) {
			userTagService.update();
		}
	}

}