package fortscale.collection.tagging.service;

public interface AdministratorAccountService {

	boolean isUserAdministrator(String username);
	void update();
	void refresh();
}
