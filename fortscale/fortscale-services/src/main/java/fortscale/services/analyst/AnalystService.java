package fortscale.services.analyst;

import java.util.List;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.analyst.AnalystSavedSearch;


public interface AnalystService {

	public void replaceEmailAddress(String username, String emailAddress);
	public Analyst findByUsername(String username);
	public List<Analyst> findAll();
	public List<Analyst> findAllNonDisabledUsers();
	public void followUser(AnalystAuth analystAuth, String userId, boolean follow);
	public String createSavedSearch(AnalystAuth analystAuth, String name, String category, String filter, String description);
	public List<AnalystSavedSearch> findSavedSearch(String savedSearchId);
}