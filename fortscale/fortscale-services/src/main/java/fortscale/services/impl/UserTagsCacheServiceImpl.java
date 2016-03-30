package fortscale.services.impl;

import fortscale.domain.core.dao.UserRepository;
import fortscale.services.UserTagsCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by idanp on 22/03/2016.
 */
@Service("userTagsCacheService")
public class UserTagsCacheServiceImpl implements UserTagsCacheService {

    private Map<String,Set<String>> userTagsCache;

    @Autowired
    UserRepository userRepository;


    @Override
    public Set<String> getUserTags(String NormalizedUsername)
    {
		if (userTagsCache == null)
		{
			userTagsCache = new HashMap<>();
		}

        if(userTagsCache.containsKey(NormalizedUsername))
            return userTagsCache.get(NormalizedUsername);

        return userRepository.getUserTags(NormalizedUsername);

    }

    @Override
    public void addUserTags(String NormalizedUsername,Set<String> tags)
    {
		if (userTagsCache == null) {
			userTagsCache = new HashMap<>();
			userTagsCache.put(NormalizedUsername,tags);
		}

        else if (userTagsCache.containsKey(NormalizedUsername)) {
			userTagsCache.replace(NormalizedUsername, tags);
		}
        else {
			userTagsCache.put(NormalizedUsername, tags);
		}
    }
}
