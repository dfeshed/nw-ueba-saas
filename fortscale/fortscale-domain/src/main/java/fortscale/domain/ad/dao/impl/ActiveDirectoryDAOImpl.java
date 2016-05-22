package fortscale.domain.ad.dao.impl;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.dao.ActiveDirectoryDAO;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.utils.logging.Logger;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ActiveDirectoryDAOImpl implements ActiveDirectoryDAO {
    private static final String AD_ATTRIBUTE_CN = "CN";
    private static final String AD_DOMAIN_CONTROLLERS_FILTER = "(&(objectCategory=computer)(userAccountControl:1.2.840.113556.1.4.803:=8192))";
    private static final String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private final Logger logger = Logger.getLogger(ActiveDirectoryDAOImpl.class);


    @Override
    public void getAndHandle(String filter, String adFields, int resultLimit, ActiveDirectoryResultHandler handler, List<AdConnection> adConnections) throws Exception {
        logger.debug("Connecting to domain controllers");
        byte[] cookie;
        int pageSize = 1000;
        int totalRecords = 0;
        for (AdConnection adConnection : adConnections) {
            logger.debug("Fetching from {}", adConnection.getDomainBaseSearch());
            Hashtable<String, String> environment = initializeAdConnectionEnv(adConnection);
            environment.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
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
                    handler.handleAttributes(attributes);
                    records++;
                }
                cookie = parseControls(context.getResponseControls());
                context.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
            } while ((cookie != null) && (cookie.length != 0));
            context.close();
            totalRecords += records;
            logger.debug("Fetched {} records", records);
        }
        handler.finishHandling();
        logger.debug("Fetched a total of {} records", totalRecords);
    }

    @Override
    public List<String> getDomainControllers(List<AdConnection> AdConnections) throws Exception {
        boolean connected = false;
        LdapContext context = null;
        List<String> domainControllers = new ArrayList<>();
        for (AdConnection adConnection : AdConnections) {
            final String domainName = adConnection.getDomainName();
            logger.debug("getting domain controllers from {}", domainName);
            Hashtable<String, String> environment = initializeAdConnectionEnv(adConnection);
            for (String dcAddress : adConnection.getIpAddresses()) {
                logger.debug("Trying to connect to domain controller at {}", dcAddress);
                environment.put(Context.PROVIDER_URL, "ldap://" + dcAddress);
                try {
                    context = new InitialLdapContext(environment, null);
                } catch (CommunicationException ex) {
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
            searchControls.setReturningAttributes(new String[]{AD_ATTRIBUTE_CN});
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


    private Hashtable<String, String> initializeAdConnectionEnv(AdConnection adConnection) throws Exception {
        String username = adConnection.getDomainUser();
        String password = fortscale.utils.EncryptionUtils.decrypt(adConnection.getDomainPassword());
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.SECURITY_PRINCIPAL, username);
        environment.put(Context.SECURITY_CREDENTIALS, password);
        environment.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
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
}