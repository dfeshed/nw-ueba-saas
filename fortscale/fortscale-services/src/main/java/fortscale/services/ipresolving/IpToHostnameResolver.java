package fortscale.services.ipresolving;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.services.ComputerService;

/**
 * IP resolving service that aggregate results from all available providers to determine the
 * what is the host name assignment for a given ip address and a certain time stamp
 */
@Service("ipToHostnameResolver")
@Scope("singleton")
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
		// get hostname from file resolver
		if (isFileProviderEnabled()) {
			String hostname = normalizeHostname(fileResolver.getHostname(ip));
			if (StringUtils.isNotEmpty(hostname))
				return hostname;
		}
		
		// get hostname from security events and dhcp
		ComputerLoginEvent loginEvent = isLoginProviderEnabled()? computerLoginResolver.getComputerLoginEvent(ip, timestamp) : null;
		DhcpEvent dhcpEvent = isDhcpProviderEnabled()? dhcpResolver.getLatestDhcpEventBeforeTimestamp(ip, timestamp) : null;
		
		String loginHostname = (loginEvent==null)? null : normalizeHostname(loginEvent.getHostname());
		String dhcpHostname = (dhcpEvent==null)? null : normalizeHostname(dhcpEvent.getHostname());
		
		// check if we can return the login event hostname
		if (loginEvent!=null) {
			// return login hostname in case:
			// 1. dhcp is missing
			// 2. it is newer than the dhcp event
			// 3. dhcp and login event hostnames are the same
			if (dhcpEvent==null || (loginEvent.getTimestampepoch() > dhcpEvent.getTimestampepoch()) || (loginHostname.equals(dhcpHostname)))
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
					(!restrictToADName || dhcpEvent.isADHostName() ||  isHostnameInAD(dhcpHostname))) {
				return dhcpHostname;
			}
		}
		
		// at last resolve to dns if all other failed
		if (isDnsProviderEnabled()) {
			String hostname = normalizeHostname(dnsResolver.getHostname(ip, timestamp));
			
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
	
	private String normalizeHostname(String hostname) {
		if (StringUtils.isEmpty(hostname))
			return null;
		else
			return (hostname.contains("."))? hostname.substring(0, hostname.indexOf('.')).toUpperCase() : hostname.toUpperCase();
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
}
