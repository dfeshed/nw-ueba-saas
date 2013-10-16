package fortscale.services.analyst;

import java.util.List;

import fortscale.domain.analyst.Analyst;


public interface AnalystService {

	public void replaceEmailAddress(String username, String emailAddress);
	public Analyst findByUsername(String username);
	public List<Analyst> findAll();
}
