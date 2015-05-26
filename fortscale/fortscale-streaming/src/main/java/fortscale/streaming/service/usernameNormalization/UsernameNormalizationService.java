package fortscale.streaming.service.usernameNormalization;

import fortscale.services.impl.UsernameNormalizer;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

/**
 * Service for normalization of usernames
 * Date: 24/01/2015.
 */
public class UsernameNormalizationService {

	@Value("${normalizedUser.fail.filter:false}")
	protected boolean dropOnFail;

	protected UsernameNormalizer usernameNormalizer;

	/**
	 * Normalize username
	 * @param username the original username
	 * @return the normalized username (or null if failed to normalize)
	 */
	public String normalizeUsername(String username, String domain){
		//if normalizedUsers.fail filter is set: function returns null if username normalization failed.
		String ret = null;
		UsernameNormalizer usernameNormalizer = getUsernameNormalizer();
		if(usernameNormalizer != null){
			ret = usernameNormalizer.normalize(username.toLowerCase());
			if(ret == null){
				String tempUsername = username.toLowerCase() + "@" + domain.toLowerCase();
				if (usernameNormalizer.isUsernameExists(tempUsername)) {
					ret = tempUsername;
				}
			}
		}

		return ret;
	}

	/**
	 * Should drop record if normalization failed
	 * @param username the user name
	 * @param normalizedUsername the normalized username
	 * @return	true if record should be dropped
	 */
	public boolean shouldDropRecord(String username, String normalizedUsername){
		return normalizedUsername == null && dropOnFail;
	}

	/**
	 * Get the username as the normalized username
	 * @param username    the original username
	 * @param message	the entire message
	 * @return	the normalized username to use
	 */
	public String getUsernameAsNormalizedUsername(String username, JSONObject message){
		return username.toLowerCase();
	}

	// -- Getters and Setters

	public boolean isDropOnFail() {
		return dropOnFail;
	}

	public void setDropOnFail(boolean dropOnFail) {
		this.dropOnFail = dropOnFail;
	}

	public UsernameNormalizer getUsernameNormalizer() {
		return usernameNormalizer;
	}

	public void setUsernameNormalizer(UsernameNormalizer usernameNormalizer) {
		this.usernameNormalizer = usernameNormalizer;
	}
}
