package presidio.output.processor.services.user;

import java.util.List;

/**
 * Created by efratn on 22/08/2017.
 */
public class UserDetails {
    private String userName;
    private String userDisplayName;
    private String userId;
    private List<String> tags;

    public UserDetails(String userName, String userDisplayName, String userId, List<String> tags) {
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.userId = userId;
        this.tags = tags;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getTags() {
        return tags;
    }
}