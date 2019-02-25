package presidio.data.generators.event.process;

import presidio.data.domain.IUser;

public class SystemUser implements IUser {
    private IUser user;
    private String systemName;

    public SystemUser(IUser user, String systemName) {
        this.user = user;
        this.systemName = systemName;
    }

    @Override
    public String getEmail() {
        return systemName;
    }

    @Override
    public String getUsername() {
        return systemName;
    }

    @Override
    public String getUserId() {
        return systemName;
    }

    @Override
    public String getFirstName() {
        return systemName;
    }

    @Override
    public String getLastName() {
        return systemName;
    }

    @Override
    public Boolean isAdministrator() {
        return user.isAdministrator();
    }

    @Override
    public Boolean getAdministrator() {
        return user.getAdministrator();
    }

    @Override
    public Boolean getAnonymous() {
        return user.getAnonymous();
    }
}
