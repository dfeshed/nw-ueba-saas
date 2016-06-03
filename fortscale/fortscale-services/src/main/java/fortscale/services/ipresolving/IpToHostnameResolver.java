package fortscale.services.ipresolving;

import fortscale.domain.events.*;
import fortscale.services.ComputerService;
import fortscale.streaming.service.ipresolving.IpToHostnameResolverMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

/**
 * IP resolving service that aggregate results from all available providers to determine the
 * what is the host name assignment for a given ip address and a certain time stamp
 */
public class IpToHostnameResolver implements InitializingBean {

	// Queue init size
	public static final int QUEUE_SIZE = 3;

	// Default message to be return when there is no resolve
	public static final String RESOLVING_DEFAULT_MESSAGE = null;

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ComputerLoginResolver computerLoginResolver;
	@Autowired
	private DhcpResolver dhcpResolver;
	@Autowired
	private IseResolver iseResolver;
	@Autowired
	private PxGridResolver pxGridResolver;
	@Autowired
	private DnsResolver dnsResolver;
	@Autowired
	private StaticFileBasedMappingResolver fileResolver;
	@Autowired
	private ComputerService computerService;

	//@Autowired
	protected StatsService statsService= null; // TODO

	@Value("${ip2hostname.hostnames.blacklist}")
	private String hostnameBlacklist;
	@Value("${ip2hostname.retention}")
	private int retentionInSeconds;
	@Value("${ip2hostname.retention.enabled}")
	private boolean retentionEnabled;

	private Pattern blacklistMatcher;

	@Value("${ip2hostname.fileProvider.enabled:true}")
	private boolean fileProviderEnabled;
	@Value("${ip2hostname.loginProvider.enabled:true}")
	private boolean loginProviderEnabled;
	@Value("${ip2hostname.dhcpProvider.enabled:true}")
	private boolean dhcpProviderEnabled;
	@Value("${ip2hostname.iseProvider.enabled:true}")
	private boolean iseProviderEnabled;
	@Value("${ip2hostname.dnsProvider.enabled:true}")
	private boolean dnsProviderEnabled;
	@Value("${ip2hostname.pxGridProvider.enabled:true}")
	private boolean pxGridProviderEnabled;

	protected IpToHostnameResolverMetrics metrics;

	/**
	 * Resolve ip address into hostname using all available resolvers (dhcp, login, file, dns)
	 *
	 * @param restrictToADName should we return only hostnames that appear in AD and leave un-resolved otherwise
	 * @return hostname in capital letters, stripped up to the first dot in name. Null in case resolve did not match.
	 */
	public String resolve(String ip, long timestamp, boolean restrictToADName) {
		return resolve(ip, timestamp, restrictToADName, true, false);
	}

	/**
	 * Resolve ip address into hostname using all available resolvers (dhcp, login, file, dns)
	 *
	 * @param restrictToADName should we return only hostnames that appear in AD and leave un-resolved otherwise
	 * @param shortName        truncate the hostname to be up to the first dot
	 * @param isRemoveLastDot  remove dot character from the hostname in case it is the last character in the name
	 * @return hostname in capital letters, stripped up to the first dot in name. Null in case resolve did not match.
	 */
	public String resolve(String ip, long timestamp, boolean restrictToADName, boolean shortName,
						  boolean isRemoveLastDot) {

		metrics.resolveAttempts++;
		metrics.resolveMessageEpoch = timestamp;

		//check if ip is already resolved, meaning we get a hostname/invalid ip as the ip - just return that hostname
		if (!InetAddressValidator.getInstance().isValid(ip)) {
			metrics.validIpAddressResolutions++;
			return ip;
		}

		//define the local priority queue
		PriorityQueue<IpToHostname> ipToHostnameQueue = null;

		// get hostname from file resolver
		if (isFileProviderEnabled()) {
			String hostname = normalizeHostname(fileResolver.getHostname(ip), isRemoveLastDot, shortName);
			if (StringUtils.isNotEmpty(hostname)) {
				metrics.fileProviderResolutions++;
				return hostname;
			}

		}

		// Init queue
		ipToHostnameQueue = initializeIpToHostnameQueue(ipToHostnameQueue);

		// Add events to queue
		if (isLoginProviderEnabled()) {
			addToHostnameQueue(ipToHostnameQueue, computerLoginResolver.getComputerLoginEvent(ip, timestamp));
		}

		if (isDhcpProviderEnabled()) {
			addToHostnameQueue(ipToHostnameQueue, dhcpResolver.getLatestDhcpEventBeforeTimestamp(ip, timestamp));
		}

		if (isIseProviderEnabled()) {
			addToHostnameQueue(ipToHostnameQueue, iseResolver.getLatestIseEventBeforeTimestamp(ip, timestamp));
		}

		if (isPxGridProviderEnabled()) {
			addToHostnameQueue(ipToHostnameQueue, pxGridResolver.getLatestPxGridEventBeforeTimestamp(ip, timestamp));
		}

		// Try resolving IP using queue
		String hostname = getHostNameFromHostnameQueue(ipToHostnameQueue, isRemoveLastDot, shortName, restrictToADName, timestamp);

		// If we found match, return the hostname
		if (hostname != null && !hostname.isEmpty() && hostname != RESOLVING_DEFAULT_MESSAGE) {
			metrics.standardResolutions++;
			return hostname;
		}

		// at last resolve to dns if all other failed
		if (isDnsProviderEnabled()) {
			hostname = normalizeHostname(dnsResolver.getHostname(ip, timestamp), isRemoveLastDot, shortName);

			// return dns name if (AND between criteria):
			// 1. it is not in blacklist
			// 2. hostname is in AD and we were asked to restrictToADNames
			if (hostname != null && !isHostnameInBlacklist(hostname) && (!restrictToADName || isHostnameInAD(hostname))) {
				metrics.dnsResolutions++;
				return hostname;
			}
		}

		// return un resolved if all providers failed
		return RESOLVING_DEFAULT_MESSAGE;
	}

	private boolean isHostnameInAD(String hostname) {
		metrics.hostnameInAD++;
		return computerService.isHostnameInAD(hostname);
	}

	public String normalizeHostname(String hostname, boolean isRemoveLastDot, boolean shortName) {
		if (StringUtils.isEmpty(hostname))
			return null;

		String retHostname = hostname.toUpperCase();
		if (isRemoveLastDot)
			retHostname = retHostname.endsWith(".") ? retHostname.substring(0, retHostname.length() - 1) : retHostname;

		if (shortName)
			retHostname = retHostname.contains(".") ? retHostname.substring(0, hostname.indexOf('.')) : retHostname;

		return retHostname;
	}

	private boolean isHostnameInBlacklist(String hostname) {
		// check for match only when we have blacklist set
		if (StringUtils.isEmpty(hostnameBlacklist))
			return false;

		// ensure regex matcher is build for the blacklist pattern
		if (blacklistMatcher == null) {
			blacklistMatcher = Pattern.compile(hostnameBlacklist, Pattern.CASE_INSENSITIVE);
		}

		boolean result = blacklistMatcher.matcher(hostname).matches();

		if (result) {
			metrics.hostnameInBlackList++;
		}

		return result;
	}

	/**
	 * Init the priority queue
	 * Save items by Timestamp epoch when newest is on top
	 */
	private PriorityQueue<IpToHostname> initializeIpToHostnameQueue(PriorityQueue<IpToHostname> ipToHostnameQueue) {

		ipToHostnameQueue = new PriorityQueue<IpToHostname>(QUEUE_SIZE, new Comparator<IpToHostname>() {
			@Override
			public int compare(IpToHostname o1, IpToHostname o2) {
				return o2.getTimestampepoch().compareTo(o1.getTimestampepoch());
			}
		});

		return ipToHostnameQueue;
	}

	/**
	 * Add item to queue
	 * Before insert, checks that hostname isn't in the hostname blacklist
	 *
	 * @param event to insert
	 */
	private void addToHostnameQueue(PriorityQueue<IpToHostname> ipToHostnameQueue, IpToHostname event) {
		if (event != null) {
			ipToHostnameQueue.add(event);
		}
	}

	/**
	 * Resolve IP using queue
	 *
	 * @param isRemoveLastDot
	 * @param shortName
	 * @return The resolved hostname
	 */
	private String getHostNameFromHostnameQueue(PriorityQueue<IpToHostname> ipToHostnameQueue, boolean isRemoveLastDot,
												boolean shortName, boolean restrictToADName, long timestamp) {

		while (!ipToHostnameQueue.isEmpty()) {
			IpToHostname event = ipToHostnameQueue.poll();
			if (event != null) {
				if (isEventInTimeframe(event, timestamp)) {
					String normalizeHostname = normalizeHostname(event.getHostname(), isRemoveLastDot, shortName);
					if (!isHostnameInBlacklist(normalizeHostname)) {
						// return the resolve only in the next cases with OR between them :
						// 1. the data source is not restricted to AD
						// 2. The data source is restricted to AD and also the resolving event is for AD machine
						if (!restrictToADName || event.checkIsAdHostname()) {
							return normalizeHostname;
						}
					}
				}
			}
		}

		metrics.queueResolutionsFailures++;

		return RESOLVING_DEFAULT_MESSAGE;
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

	public boolean isIseProviderEnabled() {
		return iseProviderEnabled;
	}

	public void setIseProviderEnabled(boolean iseProviderEnabled) {
		this.iseProviderEnabled = iseProviderEnabled;
	}

	public boolean isPxGridProviderEnabled() {
		return pxGridProviderEnabled;
	}

	public void setPxGridProviderEnabled(boolean pxGridProviderEnabled) {
		this.pxGridProviderEnabled = pxGridProviderEnabled;
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

	public IseResolver getIseResolver() {
		return iseResolver;
	}

	public PxGridResolver getPxGridResolver() {
		return pxGridResolver;
	}

	public DnsResolver getDnsResolver() {
		return dnsResolver;
	}

	public ComputerService getComputerService() {
		return computerService;
	}

	/**
	 * Check if the current request is in the event time frame
	 *
	 * @param event
	 * @param timestamp
	 * @return
	 */
	private boolean isEventInTimeframe(IpToHostname event, long timestamp) {
		if (event == null || event.getTimestampepoch() == null) {
			return true;
		}

		if (event.getTimestampepoch().compareTo(event.getExpiration()) < 0) {
			boolean result = TimestampUtils.convertToMilliSeconds(timestamp) > TimestampUtils.convertToMilliSeconds(event.getTimestampepoch());

			if (result == false) {
				metrics.eventOutsideTimeFrame++;
			}

			return result;
		}

		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		// Create the metrics (can't do that at ctor due to injection)
		createMetrics();

		//set expiration for all ip resolving collections
		updateRetentionTime(ComputerLoginEvent.collectionName);
		updateRetentionTime(PxGridIPEvent.collectionName);
		updateRetentionTime(IseEvent.collectionName);
		updateRetentionTime(DhcpEvent.collectionName);
	}

	/**
	 *
	 * This method updates the retention time in collection time
	 *
	 * @param collectionName
	 */
	private void updateRetentionTime(String collectionName) {

		metrics.updateRetentionTime++;
		String indexName = IpToHostname.CREATED_AT_FIELD_NAME;
		boolean indexExists = false;
		for (IndexInfo indexInfo: mongoTemplate.indexOps(collectionName).getIndexInfo()) {
			if (indexInfo.getName().equals(indexName)) {
				indexExists = true;
				if (!retentionEnabled) {
					mongoTemplate.indexOps(collectionName).dropIndex(indexName);
					metrics.updateRetentionTimeUpdates++;
				}
				break;
			}
		}
		if (retentionEnabled && !indexExists) {
			mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(indexName, Sort.Direction.ASC).
					expire(retentionInSeconds).named(indexName));
		}
	}


	/**
	 * This method responsible to sync the caching record at the ComputerLogin resolver cache with their new document at mongo
	 *
	 */
	public void removeIpFromComputerLoginResolverCache(String ip)
	{
		metrics.removeIpFromComputerLoginResolverCache++;
		this.computerLoginResolver.removeFromCache(ip);
	}

	/**
	 * Create the task metrics
	 */
	public void createMetrics() {
		metrics = new IpToHostnameResolverMetrics(statsService, "main");

	}


}
