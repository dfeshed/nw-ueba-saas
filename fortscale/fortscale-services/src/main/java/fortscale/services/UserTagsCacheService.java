package fortscale.services;

import java.util.List;
import java.util.Set;

/**
 * Created by idanp on 22/03/2016.
 */
public interface UserTagsCacheService {

    Set<String> getUserTags(String NormalizedUsername);
    void addUserTags(String NormalizedUsername,Set<String> tags);
}
