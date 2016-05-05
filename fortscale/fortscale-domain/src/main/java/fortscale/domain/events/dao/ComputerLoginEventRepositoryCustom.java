package fortscale.domain.events.dao;

/**
 * Created by idanp on 4/17/2016.
 */
public interface ComputerLoginEventRepositoryCustom {

	public void updateResolvingExpireDueToVPNSessionEnd(String ipAddress,long startSessionTime,long endSessionTime);
}
