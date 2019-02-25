package presidio.data.domain;

public interface IUser {
    String getEmail();

//    void setEmail(String email);

    String getUsername();

//    void setUsername(String username);

    String getUserId();

//    void setUserId(String userId);

    String getFirstName();

//    void setFirstName(String firstName);

    String getLastName();

//    void setLastName(String lastName);

    Boolean isAdministrator();

//    void setAdministrator(Boolean administrator);

    Boolean getAdministrator();

    Boolean getAnonymous();

//    void setAnonymous(Boolean anonymous);
}
