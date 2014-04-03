package fortscale.collection.morphlines.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;


@Configurable(preConstruction=true)
public class DnsResolver {
	private static Logger logger = LoggerFactory.getLogger(DnsResolver.class);
	
	private static final char DNS_SERVERS_SEPERATOR = ',';
	
	private HashMap<String,String> dnsCacheMap = new HashMap<String,String>();
	private HashSet<String> blackIpHashSetCache = new HashSet<String>();
	
	@Value("${dns.resolver.maxQueries}")
	private int maxQueries;
	@Value("${dns.resolver.dnsServers:}")
	private String dnsServers;
	@Value("${dns.resolver.timeoutInSeconds}")
	private int timeoutInSeconds;
	
	private int dnsLookupCounter = 0;
	
	
	public String getHostname(String ip_address) {	
		if (!blackIpHashSetCache.isEmpty() && blackIpHashSetCache.contains(ip_address)) {
			logger.debug("IP {} is in the black list. Skipping it.", ip_address);
			return null;
		}

		if (dnsCacheMap.containsKey(ip_address)) {
			return dnsCacheMap.get(ip_address);
		}
		
		
		String resolvedHostname = null;
		if ((this.maxQueries == -1) || (this.maxQueries > dnsLookupCounter)) {
			String[] dnsServersArray = null;
			if (!StringUtils.isEmpty(dnsServers)) {
				dnsServersArray = StringUtils.split(dnsServers, DNS_SERVERS_SEPERATOR);
			}
			dnsLookupCounter++;
			try {
				resolvedHostname = reverseDns(ip_address,dnsServersArray,this.timeoutInSeconds);
			}
			catch (Exception e) {
				logger.debug("Exception while running reverseDns resolving for IP: {}. Adding it to black list.", ip_address);
				blackIpHashSetCache.add(ip_address);
				return null;
			}

			if (null==resolvedHostname || resolvedHostname.isEmpty() || resolvedHostname.equalsIgnoreCase(ip_address)) {
				blackIpHashSetCache.add(ip_address);
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
			return hostIp;
		else
			return answers[0].rdataToString();
	}
}
