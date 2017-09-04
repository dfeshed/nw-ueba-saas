package presidio.output.processor.services.user;

/**
 * Created by efratn on 22/08/2017.
 */
public class UserDetails {
    private String userName;
    private String userDisplayName;
    private String userId;
    private Boolean isAdmin;

    public UserDetails(String userName, String userDisplayName, String userId, Boolean isAdmin) {
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.userId = userId;
        this.isAdmin = isAdmin;
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

    public Boolean isAdmin() {
        return isAdmin;
    }
}