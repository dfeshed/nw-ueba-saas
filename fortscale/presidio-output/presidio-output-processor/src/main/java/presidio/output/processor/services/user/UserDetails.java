package presidio.output.processor.services.user;

/**
 * Created by efratn on 22/08/2017.
 */
public class UserDetails {
    private String userName;
    private String userDisplayName;
    private String userId;

    public UserDetails(String userName, String userDisplayName, String userId) {
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.userId = userId;
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
}