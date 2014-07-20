package fortscale.collection.tagging.service;

public interface ExecutiveAccountService {

	boolean isUserExecutive(String username);
	void update();
	void refresh();
}
