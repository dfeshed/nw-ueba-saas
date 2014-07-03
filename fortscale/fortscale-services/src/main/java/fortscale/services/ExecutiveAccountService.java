package fortscale.services;

public interface ExecutiveAccountService {

	boolean isUserExecutive(String username);
	void update();
	void refresh();
}
