package fortscale.services.ipresolving;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.services.ComputerService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

/**
 * IP resolving service that aggregate results from all available providers to determine the
 * what is the host name assignment for a given ip address and a certain time stamp
 */
public class IpToHostnameResolver {

	private static Logger logger = LoggerFactory.getLogger(IpToHostnameResolver.class);
	
	@Autowired
	private ComputerLoginResolver computerLoginResolver;
	@Autowired
	private DhcpResolver dhcpResolver;
	@Autowired
	private DnsResolver dnsResolver;
	@Autowired
	private StaticFileBasedMappingResolver fileResolver;
	@Autowired
	private ComputerService computerService;
	@Value("${ip2hostname.hostnames.blacklist}")	
	private String hostnameBlacklist;
	
	private Pattern blacklistMatcher;
	
	@Value("${ip2hostname.fileProvider.enabled:true}")
	private boolean fileProviderEnabled;
	@Value("${ip2hostname.loginProvider.enabled:true}")
	private boolean loginProviderEnabled;
	@Value("${ip2hostname.dhcpProvider.enabled:true}")
	private boolean dhcpProviderEnabled;
	@Value("${ip2hostname.dnsProvider.enabled:true}")
	private boolean dnsProviderEnabled;



	/**
	 * Resolve ip address into hostname using all available resolvers (dhcp, login, file, dns)
	 * @param restrictToADName should we return only hostnames that appear in AD and leave un-resolved otherwise
	 * @return hostname in capital letters, stripped up to the first dot in name. Null in case resolve did not match.
	 */
	public String resolve(String ip, long timestamp, boolean restrictToADName) {
		return resolve(ip, timestamp, restrictToADName, true, false);
	}
	
	/**
	 * Resolve ip address into hostname using all available resolvers (dhcp, login, file, dns)
	 * @param restrictToADName should we return only hostnames that appear in AD and leave un-resolved otherwise
	 * @param shortName truncate the hostname to be up to the first dot
	 * @param isRemoveLastDot remove dot character from the hostname in case it is the last character in the name
	 * @return hostname in capital letters, stripped up to the first dot in name. Null in case resolve did not match.
	 */
	public String resolve(String ip, long timestamp, boolean restrictToADName, boolean shortName, boolean isRemoveLastDot) {
		// get hostname from file resolver
		if (isFileProviderEnabled()) {
			String hostname = normalizeHostname(fileResolver.getHostname(ip), isRemoveLastDot, shortName);
			if (StringUtils.isNotEmpty(hostname))
				return hostname;
		}
		
		// get hostname from security events and dhcp
		ComputerLoginEvent loginEvent = isLoginProviderEnabled()? computerLoginResolver.getComputerLoginEvent(ip, timestamp) : null;
		DhcpEvent dhcpEvent = isDhcpProviderEnabled()? dhcpResolver.getLatestDhcpEventBeforeTimestamp(ip, timestamp) : null;
		
		String loginHostname = (loginEvent==null)? null : normalizeHostname(loginEvent.getHostname(), isRemoveLastDot, shortName);
		String dhcpHostname = (dhcpEvent==null)? null : normalizeHostname(dhcpEvent.getHostname(), isRemoveLastDot, shortName);
		
		// check if we can return the login event hostname
		if (loginEvent!=null) {
			// return login hostname in case:
			// 1. dhcp is missing
			// 2. it is newer than the dhcp event
			// 3. dhcp and login event hostnames are the same
			if (dhcpEvent==null || (loginEvent.getTimestampepoch() >= dhcpEvent.getTimestampepoch()) || (loginHostname.equals(dhcpHostname)))
				return loginHostname;
			else {
				// log conflicts between dhcp and security events
				if (dhcpEvent!=null && !loginHostname.equals(dhcpHostname))
					logger.debug("Conflict in ip resolving between dhcp event [hn={},time={},exp={}] and security event [hn={},time={}] for ip {} at time {}", 
							dhcpHostname, dhcpEvent.getTimestampepoch(), dhcpEvent.getExpiration(), loginHostname, loginEvent.getTimestampepoch(), ip, timestamp);
			}
		}

		// check if we can return dhcp event
		if (dhcpEvent!=null) {
			// return dhcp hostname if (AND between criteria):
			// 1. it is not in blacklist
			// 2. hostname is in AD and we were asked to restrictToADNames
			if (!isHostnameInBlacklist(dhcpHostname) && 
					(!restrictToADName || dhcpEvent.isAdHostName() ||  isHostnameInAD(dhcpHostname))) {
				return dhcpHostname;
			}
		}
		
		// at last resolve to dns if all other failed
		if (isDnsProviderEnabled()) {
			String hostname = normalizeHostname(dnsResolver.getHostname(ip, timestamp), isRemoveLastDot, shortName);
			
			// return dns name if (AND between criteria):
			// 1. it is not in blacklist
			// 2. hostname is in AD and we were asked to restrictToADNames
			if (hostname!=null && !isHostnameInBlacklist(hostname) && (!restrictToADName || isHostnameInAD(hostname)))
				return hostname;
		}
		
		// return un resolved if all providers failed
		return null;
	}
	
	private boolean isHostnameInAD(String hostname) {
		return computerService.isHostnameInAD(hostname);
	}
	
	private String normalizeHostname(String hostname, boolean isRemoveLastDot, boolean shortName) {
		if (StringUtils.isEmpty(hostname))
			return null;
		
		String retHostname = hostname.toUpperCase();
		if (isRemoveLastDot)
			retHostname = retHostname.endsWith(".") ? retHostname.substring(0, retHostname.length()-1) : retHostname ; 
		
		if (shortName)
			retHostname = retHostname.contains(".") ? retHostname.substring(0, hostname.indexOf('.')) : retHostname ;

		return retHostname;
	}
	
	private boolean isHostnameInBlacklist(String hostname) {
		// check for match only when we have blacklist set
		if (StringUtils.isEmpty(hostnameBlacklist))
			return false;
			
		// ensure regex matcher is build for the blacklist pattern
		if (blacklistMatcher==null) {
			blacklistMatcher = Pattern.compile(hostnameBlacklist, Pattern.CASE_INSENSITIVE);
		}
		
		return blacklistMatcher.matcher(hostname).matches();
	}
	
	
	public void setHostnameBlacklist(String hostnameBlacklist) {
		this.hostnameBlacklist = hostnameBlacklist;
	}

	public boolean isFileProviderEnabled() {
		return fileProviderEnabled;
	}

	public void setFileProviderEnabled(boolean fileProviderEnabled) {
		this.fileProviderEnabled = fileProviderEnabled;
	}

	public boolean isLoginProviderEnabled() {
		return loginProviderEnabled;
	}

	public void setLoginProviderEnabled(boolean loginProviderEnabled) {
		this.loginProviderEnabled = loginProviderEnabled;
	}

	public boolean isDhcpProviderEnabled() {
		return dhcpProviderEnabled;
	}

	public void setDhcpProviderEnabled(boolean dhcpProviderEnabled) {
		this.dhcpProviderEnabled = dhcpProviderEnabled;
	}

	public boolean isDnsProviderEnabled() {
		return dnsProviderEnabled;
	}

	public void setDnsProviderEnabled(boolean dnsProviderEnabled) {
		this.dnsProviderEnabled = dnsProviderEnabled;
	}

	public ComputerLoginResolver getComputerLoginResolver() {
		return computerLoginResolver;
	}

	public DhcpResolver getDhcpResolver() {
		return dhcpResolver;
	}

	public DnsResolver getDnsResolver() {
		return dnsResolver;
	}

    public ComputerService getComputerService() {
        return computerService;
    }
}
