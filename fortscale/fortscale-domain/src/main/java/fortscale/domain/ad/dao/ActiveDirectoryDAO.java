package fortscale.domain.ad.dao;

import fortscale.domain.Exceptions.PasswordDecryptionException;
import fortscale.domain.ad.AdConnection;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import java.util.List;

public interface ActiveDirectoryDAO {

    /**
     * This method connects to all of the domains by iterating
     * over each one of them and attempting to connect to their DCs until one such connection is successful.
     * It then performs the requested search according to the filter and lets the {@code handler} handle the results
     *
     * @param filter        The Active Directory search filter (which object class is required)
     * @param adFields      The Active Directory attributes to return in the search
     * @param resultLimit   A limit on the search results (mostly for testing purposes) should be <= 0 for no limit
     * @param handler       the handler that will handle the search results
     * @param adConnections the AdConnections to try to connect to
     */
    void getAndHandle(String filter, String
            adFields, int resultLimit, ActiveDirectoryResultHandler handler, List<AdConnection> adConnections)
            throws Exception;
    List<String> getDomainControllers(List<AdConnection> AdConnections) throws Exception;

	/**
	 *
	 * This method tests to see if a connection to ad can be made
	 *
	 * @param adConnection  The Active Directory connection object
	 * @return				The result of the connection - empty for success or response error otherwise
	 * @throws Exception
	 */
    boolean connectToAD(AdConnection adConnection) throws CommunicationException, AuthenticationException, NamingException, PasswordDecryptionException;

}