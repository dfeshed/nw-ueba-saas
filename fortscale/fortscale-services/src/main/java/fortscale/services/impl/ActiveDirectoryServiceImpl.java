package fortscale.services.impl;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

@Service("ActiveDirectoryService")
public class ActiveDirectoryServiceImpl implements ActiveDirectoryService {

    private final ApplicationConfigurationService applicationConfigurationService;
    private static final String AD_CONNECTIONS_CONFIGURATION_KEY = "system.activeDirectory.settings";
    private static final String DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY = "system.activeDirectory.domainControllers";
    private static final String AD_ATTRIBUTE_CN = "CN";
    private static final String AD_DOMAIN_CONTROLLERS_FILTER = "(&(objectCategory=computer)(userAccountControl:1.2.840.113556.1.4.803:=8192))";
    private static final String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static Logger logger = Logger.getLogger(ActiveDirectoryServiceImpl.class);

    @Autowired
    public ActiveDirectoryServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    /**
     * This method connects to all of the domains by iterating
     * over each one of them and attempting to connect to their DCs until one such connection is successful.
     * It then performs the requested search according to the filter and saves the results using the {@code fileWriter}.
     *
     * @param  fileWriter      An object to save the results to (could be a file, STDOUT, String etc.)
     * @param  filter		   The Active Directory search filter (which object class is required)
     * @param  adFields	       The Active Directory attributes to return in the search
     * @param  resultLimit	   A limit on the search results (mostly for testing purposes) should be <= 0 for no limit
     */
    public void getFromActiveDirectory(BufferedWriter fileWriter, String filter, String
            adFields, int resultLimit, ActiveDirectoryResultHandler handler) throws Exception {
        logger.debug("Connecting to domain controllers");
        byte[] cookie;
        int pageSize = 1000;
        int totalRecords = 0;
        final List<AdConnection> adConnections = getAdConnectionsFromDatabase();
        for (AdConnection adConnection: adConnections) {
            logger.debug("Fetching from {}", adConnection.getDomainBaseSearch());
            Hashtable<String, String> environment = initializeAdConnectionEnv(adConnection);
            LdapContext context = null;
            boolean connected = false;
            int records = 0;
            for (String dcAddress : adConnection.getIpAddresses()) {
                logger.debug("Trying to connect to domain controller at {}", dcAddress);
                environment.put(Context.PROVIDER_URL, "ldap://" + dcAddress);
                connected = true;
                try {
                    context = new InitialLdapContext(environment, null);
                } catch (javax.naming.CommunicationException ex) {
                    logger.error("Connection to {} failed - {}", dcAddress, ex.getMessage());
                    connected = false;
                }
                if (connected) {
                    break;
                }
            }
            if (connected) {
                logger.debug("Connection established");
            } else {
                logger.error("Failed to connect to any domain controller for {}", adConnection.getDomainBaseSearch());
                continue;
            }
            String baseSearch = adConnection.getDomainBaseSearch();
            context.setRequestControls(new Control[]{new PagedResultsControl(pageSize, Control.CRITICAL)});
            SearchControls searchControls = new SearchControls();
            searchControls.setReturningAttributes(adFields.split(","));
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            if (resultLimit > 0) {
                searchControls.setCountLimit(resultLimit);
            }
            do {
                NamingEnumeration<SearchResult> answer = context.search(baseSearch, filter, searchControls);
                while (answer != null && answer.hasMoreElements() && answer.hasMore()) {
                    SearchResult result = answer.next();
                    Attributes attributes = result.getAttributes();
                    handler.handleAttributes(fileWriter, attributes);
                    records++;
                }
                cookie = parseControls(context.getResponseControls());
                context.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
            } while ((cookie != null) && (cookie.length != 0));
            context.close();
            totalRecords += records;
            logger.debug("Fetched {} records", records);
        }
        if (fileWriter != null) {
            fileWriter.flush();
            fileWriter.close();
        }
        logger.debug("Fetched a total of {} records", totalRecords);
    }

    private Hashtable<String, String> initializeAdConnectionEnv(AdConnection adConnection) throws Exception {
        String username = adConnection.getDomainUser();
        String password = fortscale.utils.EncryptionUtils.decrypt(adConnection.getDomainPassword());
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.SECURITY_PRINCIPAL, username);
        environment.put(Context.SECURITY_CREDENTIALS, password);
        environment.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
        environment.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
        return environment;
    }

    //used to determine if an additional page of results exists
    private byte[] parseControls(Control[] controls) throws NamingException {
        byte[] serverCookie = null;
        if (controls != null) {
            for (Control control : controls) {
                if (control instanceof PagedResultsResponseControl) {
                    PagedResultsResponseControl pagedResultsResponseControl = (PagedResultsResponseControl) control;
                    serverCookie = pagedResultsResponseControl.getCookie();
                }
            }
        }
        return (serverCookie == null) ? new byte[0] : serverCookie;
    }


    /**
     *
     * This method gets all the AD connections from the database
     *
     * @return a list of all the AD connections
     */
    public List<AdConnection> getAdConnectionsFromDatabase() {
        List<AdConnection> adConnections = new ArrayList<>();
        try {
            adConnections = applicationConfigurationService.getApplicationConfigurationAsObjects(AD_CONNECTIONS_CONFIGURATION_KEY, AdConnection.class);
        } catch (Exception e) {
            logger.error("Failed to get AD connections from database");
        }
        return adConnections;
    }


    @Override
    public void saveDomainControllersInDatabase(List<String> domainControllers) {
        logger.debug("Saving domain controllers in database");
        String value = String.join(",", domainControllers);
        applicationConfigurationService.insertConfigItem(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY, value);
    }

    @Override
    public List<String> getDomainControllers() {
        List<String> domainControllers = new ArrayList<>();
        try {
            logger.info("Trying to retrieve Domain Controllers from DB");
            domainControllers = getDomainControllersFromDatabase();
            if (domainControllers.isEmpty()) {
                logger.warn("No Domain Controllers were found in DB. Trying to retrieve DCs from Active Directory");
                domainControllers = getDomainControllersFromActiveDirectory();
                logger.debug("Found domain controllers in Active Directory");
                saveDomainControllersInDatabase(domainControllers);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve domain controllers");
        }

        return domainControllers;
    }


    /**
     *
     * This method gets all the AD domain controllers from the database
     *
     * @return a list of all the AD domain controllers
     */
    private List<String> getDomainControllersFromDatabase() {
        List<String> domainControllers = new ArrayList<>();
        try {
            domainControllers = new ArrayList<>(Arrays.asList(applicationConfigurationService.getApplicationConfigurationAsString(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY)
                    .map(s -> s.split(","))
                    .orElse(new String[0])));
        } catch (Exception e) {
            logger.error("Failed to get AD domain controllers from database");
        }
        return domainControllers;
    }

    private List<String> getDomainControllersFromActiveDirectory() throws Exception {
        boolean connected = false;
        LdapContext context = null;
        List<String> domainControllers = new ArrayList<>();
        List<AdConnection> adConnections = getAdConnectionsFromDatabase();
        for (AdConnection adConnection: adConnections) {
            final String domainName = adConnection.getDomainName();
            logger.debug("getting domain controllers from {}", domainName);
            String username = adConnection.getDomainUser() + "@" + domainName;
            String password = fortscale.utils.EncryptionUtils.decrypt(adConnection.getDomainPassword());
            Hashtable<String, String> environment = new Hashtable<>();
            environment.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
            environment.put(Context.SECURITY_PRINCIPAL, username);
            environment.put(Context.SECURITY_CREDENTIALS, password);
            for (String dcAddress: adConnection.getIpAddresses()) {
                logger.debug("Trying to connect to domain controller at {}", dcAddress);
                environment.put(Context.PROVIDER_URL, "ldap://" + dcAddress);
                try {
                    context = new InitialLdapContext(environment, null);
                } catch (javax.naming.CommunicationException ex) {
                    logger.error("Connection to {} failed - {}", dcAddress, ex.getMessage());
                    continue;
                }
                logger.debug("Connected to domain controller at {}", dcAddress);
                connected = true;
                break;
            }

            if (!connected) {
                logger.error("Failed to connect to all domain controllers for domain {}", domainName);
                return domainControllers;
            }

            String baseSearch = adConnection.getDomainBaseSearch();
            SearchControls searchControls = new SearchControls();
            searchControls.setReturningAttributes(new String[] { AD_ATTRIBUTE_CN });
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = context.search(baseSearch, AD_DOMAIN_CONTROLLERS_FILTER, searchControls);
            while (answer != null && answer.hasMoreElements() && answer.hasMore()) {
                SearchResult result = answer.next();
                final Attribute cnAttribute = result.getAttributes().get(AD_ATTRIBUTE_CN);
                domainControllers.add(cnAttribute.toString());
            }

            context.close();
            logger.debug("Retrieved domain controllers for domain {}", domainName);
        }

        return domainControllers;
    }
}
