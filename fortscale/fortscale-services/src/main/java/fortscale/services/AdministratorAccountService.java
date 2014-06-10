package fortscale.services;

public interface AdministratorAccountService {

	boolean isUserAdministrator(String username);
	void update();
}
