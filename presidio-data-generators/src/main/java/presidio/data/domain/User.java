package presidio.data.domain;

public class    User {
    private String username;
    private String userId; // normalisedUserName
    private String firstName;
    private String lastName;
    private Boolean isAdministrator;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String userId, String firstName, String lastName, Boolean isAdministrator) {
        this.username = username;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdministrator = isAdministrator;
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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username.toString() + '\'' +
                ", userId='" + userId.toString() + '\'' +
                ", firstName='" + firstName.toString() + '\'' +
                ", lastName='" + lastName.toString() + '\'' +
                ", isAdministrator=" + isAdministrator.toString() +
                '}';
    }
}
