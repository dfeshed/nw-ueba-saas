package fortscale.services.impl;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.system.DcConfiguration;
import fortscale.domain.system.SystemConfiguration;
import fortscale.domain.system.SystemConfigurationEnum;
import fortscale.domain.system.dao.SystemConfigurationRepository;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ServersListConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Service("ServersListConfiguration")
public class ServersListConfigurationImpl implements ServersListConfiguration {

	private static final String AD_ATTRIBUTE_CN = "CN";
	private static final String AD_DOMAIN_CONTROLLERS_FILTER = "(&(objectCategory=computer)(userAccountControl:1.2.840.113556.1.4.803:=8192))";

	private static Logger logger = LoggerFactory.getLogger(ServersListConfigurationImpl.class);

	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;

	@Value("${login.account.name.regex:}")
	private String loginAccountNameRegex;



	public static final String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

	@Override
	public List<String> getDomainControllers() {
		List<String> domainControllers = new ArrayList<>();
		try {
			logger.info("Trying to retrieve Domain Controllers from DB");
			domainControllers = activeDirectoryService.getDomainControllersFromDatabase();
			if(domainControllers.isEmpty()) {
				logger.warn("No Domain Controllers were found in DB. Trying to retrieve DCs from Active Directory");
				domainControllers = getDomainControllersFromActiveDirectory();
				logger.debug("Found domain controllers in Active Directory");
				activeDirectoryService.saveDomainControllersInDatabase(domainControllers);
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve domain controllers");
		}

		return domainControllers;
	}

	private List<String> getDomainControllersFromActiveDirectory() throws Exception {
		boolean connected = false;
		LdapContext context = null;
		List<String> domainControllers = new ArrayList<>();
		List<AdConnection> adConnections = activeDirectoryService.getAdConnectionsFromDatabase();
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

	@Override
	public String getLoginServiceRegex(){
		StringBuilder builder = new StringBuilder(loginServiceNameRegex);
		boolean isFirst = true;
		if(!StringUtils.isEmpty(loginServiceNameRegex)){
			isFirst = false;
		}
		for(String server: getDomainControllers()){
			if(isFirst){
				isFirst = false;
			} else{
				builder.append("|");
			}
			builder.append(".*").append(server).append(".*");
		}
		return builder.toString();
	}

	@Override
	public String getLoginAccountNameRegex(){
		return loginAccountNameRegex;
	}

}
