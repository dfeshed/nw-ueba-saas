package presidio.data.domain;

public class User {
    private String username;
    private String userId; // normalisedUserName
    private String firstName;
    private String lastName;
    private Boolean isAdministrator;
    private Boolean isAnonymous;


    public User(String username) {
        this.username = username;
        this.isAnonymous = false;
    }

    public User(String username, String userId, String firstName, String lastName, Boolean isAdministrator) {
        this.username = username;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdministrator = isAdministrator;
        this.isAnonymous = false;
    }

    public User(String username, String userId, String firstName, String lastName, Boolean isAdministrator, Boolean isAnonymous) {
        this.username = username;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdministrator = isAdministrator;
        this.isAnonymous = isAnonymous;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean isAdministrator() {
        return isAdministrator;
    }

    public void setAdministrator(Boolean administrator) {
        isAdministrator = administrator;
    }

    public Boolean getAdministrator() {
        return isAdministrator;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isAdministrator=" + isAdministrator +
                ", isAnonymous=" + isAnonymous +
                '}';
    }
}
