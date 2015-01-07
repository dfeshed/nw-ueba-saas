package fortscale.services.ipresolving;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import fortscale.utils.TimestampUtils;


@Service("dnsResolver")
public class DnsResolver implements InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(DnsResolver.class);
	
	private static final char DNS_SERVERS_SEPERATOR = ',';

	private Cache<String, String> dnsCache;
	private Cache<String, Boolean> blackIpHashSetCache;

	@Value("${dns.resolver.cache.size:100000}")
	private int maxCacheSize;
	@Value("${dns.resolver.cache.timeToExpireSec:3600}")
	private int cacheExpirationSec;
	@Value("${dns.resolver.blacklist.size:100000}")
	private int maxBlacklistSize;
	@Value("${dns.resolver.blacklist.timeToExpireSec:3600}")
	private int blacklistExpirationSec;
	@Value("${dns.resolver.maxQueriesPerHour:1000}")
	private int maxQueries;
	@Value("${dns.resolver.dnsServers:}")
	private String dnsServers;
	@Value("${dns.resolver.timeoutInSeconds:-1}")
	private int timeoutInSeconds;
	@Value("${dns.resolver.skip.past.events:false}")
	private boolean skipPastEvents;
	@Value("${dns.resolver.past.events.period.minutes:720}")
	private long pastEventPeriodMin;
	@Value("${dns.resolver.can.return.ipAddress:false}")
	private boolean returnIpAddresses;
	
	private long lookupTimestamp = 0L;
	private int dnsLookupCounter = 0;

	private String[] dnsServersArray;

	@Override
	public void afterPropertiesSet() throws Exception {
		// create resolving and blacklist caches according to configured sizes
		dnsCache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
				.expireAfterWrite(cacheExpirationSec, TimeUnit.SECONDS).build();

		blackIpHashSetCache = CacheBuilder.newBuilder().maximumSize(maxBlacklistSize)
				.expireAfterWrite(blacklistExpirationSec, TimeUnit.SECONDS).build();

		// prepare dns servers array from configuration
		if (!StringUtils.isEmpty(dnsServers)) {
			dnsServersArray = StringUtils.split(dnsServers, DNS_SERVERS_SEPERATOR);
		}
	}


	public String getHostname(String ip_address, long timestamp) {
		// check that the dns resolve action is not too old in case we are configured 
		// to run only on recent entries
		if (shouldSkipPastEvent(timestamp)) 
			return null;
		
		if (blackIpHashSetCache.getIfPresent(ip_address)!=null) {
			logger.debug("IP {} is in the black list. Skipping it.", ip_address);
			return null;
		}

		String hostname = dnsCache.getIfPresent(ip_address);
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
				resolvedHostname = reverseDns(ip_address,dnsServersArray,this.timeoutInSeconds);
				if (StringUtils.isNotEmpty(resolvedHostname)) {
					// some dns might return the ip address as part of the name in case it cannot be resolved correctly
					// skip them in case the service is configured to do so
					if (!returnIpAddresses && resolvedHostname.startsWith(ip_address))
						resolvedHostname = null;
					else
						dnsCache.put(ip_address, resolvedHostname);
				}
			}
			catch (Exception e) {
				logger.debug("Exception while running reverseDns resolving for IP: {}. Adding it to black list.", ip_address);
				blackIpHashSetCache.put(ip_address, Boolean.TRUE);
				return null;
			}

			if (null==resolvedHostname || resolvedHostname.isEmpty() || resolvedHostname.equalsIgnoreCase(ip_address)) {
				blackIpHashSetCache.put(ip_address, Boolean.TRUE);
				return null;
			}
		}
		
		return resolvedHostname;
	}
	
	private static String reverseDns(String hostIp,String[] dnsServers,int timeoutInSecs) throws IOException {
		Resolver res = null;
		if ((dnsServers!= null ) && (dnsServers.length > 0) && (!dnsServers[0].equals(""))){
			res = new ExtendedResolver(dnsServers);
		}
		else {
			res = new ExtendedResolver();
		}

		if (timeoutInSecs != -1) {				
			res.setTimeout(timeoutInSecs);
		}
		Name name = ReverseMap.fromAddress(hostIp);
		int type = Type.PTR;
		int dclass = DClass.IN;

		org.xbill.DNS.Record rec = org.xbill.DNS.Record.newRecord(name, type, dclass);
		Message query = Message.newQuery(rec);
		Message response = res.send(query);

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
