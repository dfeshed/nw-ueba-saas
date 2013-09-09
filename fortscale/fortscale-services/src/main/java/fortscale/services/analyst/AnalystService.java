package fortscale.services.analyst;

import fortscale.domain.core.EmailAddress;

public interface AnalystService {

	public void create(String userName, String password, String emailAddress, String firstName, String lastName);
}
