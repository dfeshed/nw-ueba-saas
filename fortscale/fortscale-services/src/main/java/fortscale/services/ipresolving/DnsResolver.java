package fortscale.services.ipresolving;

import fortscale.services.cache.CacheHandler;
import fortscale.utils.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.xbill.DNS.*;

import java.io.IOException;


public class DnsResolver implements InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(DnsResolver.class);
	
	private static final char DNS_SERVERS_SEPARATOR = ',';

	// dnsCache is used to cache ip lookups results returned from the dns server, thus lowering the number of
	// requests against the dns server
	@Autowired
	@Qualifier("dnsResolverCache")
	private CacheHandler<String,String> dnsCache;
	// blackIpHashSetCache is used to keep track of ip addresses that couldn't not be resolved into hostname using
	// the dns servers. We keep track of those ip addresses to prevent us from looking them up over and over again
	@Autowired
	@Qualifier("dnsBlacklistCache")
	private CacheHandler<String,Boolean> blackIpHashSetCache;

	@Value("${dns.resolver.maxQueriesPerHour:1000}")
	private int maxQueries;
	@Value("${dns.resolver.dnsServers:}")
	private String dnsServers;
	@Value("${dns.resolver.timeoutInSeconds:1}")
	private int timeoutInSeconds;
	@Value("${dns.resolver.retries:1}")
	private int retries;
	@Value("${dns.resolver.skip.past.events:false}")
	private boolean skipPastEvents;
	@Value("${dns.resolver.past.events.period.minutes:720}")
	private long pastEventPeriodMin;
	@Value("${dns.resolver.can.return.ipAddress:false}")
	private boolean returnIpAddresses;
	
	private long lookupTimestamp = 0L;
	private int dnsLookupCounter = 0;

	private ExtendedResolver extendedResolver;

	public void setDnsCache(CacheHandler<String,String> dnsCache) {
		this.dnsCache = dnsCache;
	}

	public void setBlackIpHashSetCache(CacheHandler<String,Boolean> blackIpHashSetCache) {
		this.blackIpHashSetCache = blackIpHashSetCache;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// prepare dns servers array from configuration
		String[] dnsServersArray = {};
		if (!StringUtils.isEmpty(dnsServers)) {
			dnsServersArray = StringUtils.split(dnsServers, DNS_SERVERS_SEPARATOR);
			if((dnsServersArray.length > 0) && (!dnsServersArray[0].equals(""))) {
				extendedResolver = new ExtendedResolver(dnsServersArray);
			}
		}
		if (extendedResolver == null) {
			extendedResolver = new ExtendedResolver();
		}
		if (timeoutInSeconds != -1) {
			extendedResolver.setTimeout(timeoutInSeconds);
		}
		if (retries != -1){
			extendedResolver.setRetries(retries);
		}
	}


	public String getHostname(String ip_address, long timestamp) {
		// check that the dns resolve action is not too old in case we are configured 
		// to run only on recent entries
		if (shouldSkipPastEvent(timestamp)) 
			return null;

		if (blackIpHashSetCache.get(ip_address)!=null) {
			logger.debug("IP {} is in the black list. Skipping it.", ip_address);
			return null;
		}

		String hostname = dnsCache.get(ip_address);
		if (hostname!=null) {
			return hostname;
		}
		
		// check if we need to reset the lookup counter, once an hour as passed
		if (lookupTimestamp!=0L && (System.currentTimeMillis() - lookupTimestamp >= 60*60*1000)) {
			lookupTimestamp = System.currentTimeMillis();
			dnsLookupCounter = 0;
		}
		
		
		String resolvedHostname = null;
		if ((this.maxQueries == -1) || (this.maxQueries > dnsLookupCounter)) {
			// update counter and timestamp
			dnsLookupCounter++;
			lookupTimestamp = (lookupTimestamp==0L) ? System.currentTimeMillis() : lookupTimestamp;
			try {
				resolvedHostname = reverseDns(ip_address);
				if (StringUtils.isNotEmpty(resolvedHostname)) {
					// some dns might return the ip address as part of the name in case it cannot be resolved correctly
					// skip them in case the service is configured to do so
					if (!returnIpAddresses && resolvedHostname.startsWith(ip_address)) {
						resolvedHostname = null;
						blackIpHashSetCache.put(ip_address, Boolean.TRUE);
					} else
						dnsCache.put(ip_address, resolvedHostname);
				}
			}
			catch (Exception e) {
				logger.debug("Exception while running reverseDns resolving for IP: {}. Adding it to black list.", ip_address);
				blackIpHashSetCache.put(ip_address, Boolean.TRUE);
				return null;
			}

			if (StringUtils.isEmpty(resolvedHostname)) {
				blackIpHashSetCache.put(ip_address, Boolean.TRUE);
				return null;
			}
		}
		
		return resolvedHostname;
	}
	
	private String reverseDns(String hostIp) throws IOException {
		Name name = ReverseMap.fromAddress(hostIp);
		int type = Type.PTR;
		int dClass = DClass.IN;

		org.xbill.DNS.Record rec = org.xbill.DNS.Record.newRecord(name, type, dClass);
		Message query = Message.newQuery(rec);
		Message response = extendedResolver.send(query);

		org.xbill.DNS.Record[] answers = response.getSectionArray(Section.ANSWER);
		if (answers.length == 0)
			return null;
		else
			return answers[0].rdataToString();
	}
	
	
	private boolean shouldSkipPastEvent(long timestamp) {
		// check if we want to restrict past events
		if (skipPastEvents) {
			long currentTime = System.currentTimeMillis();
			long eventMillis = TimestampUtils.convertToMilliSeconds(timestamp);
			// check if the event is in the past
			return (eventMillis < currentTime - pastEventPeriodMin*60*1000); 
		}
		return false;
	}

}
