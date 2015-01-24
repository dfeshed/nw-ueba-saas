package fortscale.services.service;


public interface SupportedUsersService {
	boolean isSupportedUser(String userGUID);
	int getSupportedUsersNumber();
	void addSupportedUser(String userGUID);
	public boolean isSupportedUsername(String username);
}
