package fortscale.services.analyst;

import java.util.List;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;


public interface AnalystService {

	public void replaceEmailAddress(String username, String emailAddress);
	public Analyst findByUsername(String username);
	public List<Analyst> findAll();
	public List<Analyst> findAllNonDisabledUsers();
	public void followUser(AnalystAuth analystAuth, String userId, boolean follow);
}
