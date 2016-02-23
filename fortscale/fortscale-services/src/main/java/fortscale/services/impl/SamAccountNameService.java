package fortscale.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.CachingService;
import fortscale.services.cache.CacheHandler;

/**
 * Created by amirk on 09/06/2015.
 */
public class SamAccountNameService implements CachingService {

	@Autowired
	private CacheHandler<String, List<String>> sAMAccountNameToUsernameCache;

	@Autowired
	private CacheHandler<String, String> sAMAccountNamethatBlackList;

	@Autowired
	private UserRepository userRepository;


	public List<String> getUsersBysAMAccountName(String sAMAccountName) {

		List<String> result = new ArrayList<String>();

		sAMAccountName = sAMAccountName.toLowerCase();

		if (sAMAccountNamethatBlackList.containsKey(sAMAccountName))
			return result;


		//In case that the SamaccountName exist in the cache
		if (sAMAccountNameToUsernameCache.containsKey(sAMAccountName))
			return sAMAccountNameToUsernameCache.get(sAMAccountName);

		//In case that the SamaccountName appear at Mongo
		List<User> usersSamAccountNames = userRepository.findUsersBysAMAccountName(sAMAccountName);


		//Add to blacklist if needed
		if(usersSamAccountNames == null || usersSamAccountNames.size()==0)
		{
			sAMAccountNamethatBlackList.put(sAMAccountName,"");
		}

		else {

			for (User user : usersSamAccountNames) {
				updateSamAccountnameCache(user);
				result.add(user.getUsername());
			}
		}

		return result;
	}


	public boolean updateSamAccountnameCache(User user){


		if (user!=null && user.getAdInfo() != null && user.getAdInfo().getsAMAccountName() != null) {

			List<String> users;

			String sAMAccountName = user.getAdInfo().getsAMAccountName().toLowerCase();

			//There are no users in the cache
			// 1.clean the SAMAccountName from the blacklist cahce if needed
			// 2.Add the result to the cache

			if (! sAMAccountNameToUsernameCache.containsKey(sAMAccountName)) {

				// clear from the black list
				if (sAMAccountNamethatBlackList.containsKey(sAMAccountName))
					sAMAccountNamethatBlackList.remove(sAMAccountName);

				users = new ArrayList<String>();
				users.add(user.getUsername());

				sAMAccountNameToUsernameCache.put(sAMAccountName,users);
				return true;
			}

			//There is one or more users in the cache that related to this sAMAccountName
			users = sAMAccountNameToUsernameCache.get(sAMAccountName);
			if (!users.contains(user.getUsername()))
			{
				users.add(user.getUsername());
				sAMAccountNameToUsernameCache.put(sAMAccountName, users);
				return true;
			}
		}
		return false;
	}

	public void clearCache()
	{
		if (this.getCache() != null)
			this.getCache().clear();
	}


	@Override public CacheHandler getCache() {
		return sAMAccountNameToUsernameCache;
	}

	@Override public void setCache(CacheHandler cache) {
		sAMAccountNameToUsernameCache = cache;
	}

	@Override public void handleNewValue(String key, String value) throws Exception {
		if(value == null){
			getCache().remove(key);
		}
		else {
			getCache().putFromString(key, value);
		}
	}
}