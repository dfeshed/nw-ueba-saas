package fortscale.collection.morphlines.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import com.typesafe.config.Config;

public class GetHostnameFromDNSBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetHostnameFromDNS");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new GetHostnameFromDNS(this, config, parent, child, context);
	}

	///////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	///////////////////////////////////////////////////////////////////////////////
	private static final class GetHostnameFromDNS extends AbstractCommand {

		private final String ipAddress;
		private boolean isRemoveLastDot;
		private int timeoutInSeconds = -1;
		private int maxQueries = -1;
		private List<String> dnsServers;
		private boolean isShortName = false;
		private final String outputRecordName;
		private int dnsLookupCounter = 0;
		private HashMap<String,String> dnsCacheMap = new HashMap<String,String>();
		private HashSet<String> blackIpHashSetCache = new HashSet<String>();
		private String EMPTY_STRING = "";
		
		private static final Logger logger = LoggerFactory.getLogger(GetHostnameFromDNS.class);

		public GetHostnameFromDNS(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			
			this.ipAddress = getConfigs().getString(config, "ip_address");
			if (getConfig().hasPath("dns_servers")) {
				this.dnsServers = getConfigs().getStringList(config, "dns_servers");
			}
			if (getConfig().hasPath("remove_last_dot")) {
				this.isRemoveLastDot = getConfigs().getBoolean(config, "remove_last_dot");
			}
			if (getConfig().hasPath("timeout_in_seconds")) {
				this.timeoutInSeconds = getConfigs().getInt(config, "timeout_in_seconds");
			}
			if (getConfig().hasPath("short_name")) {
				this.isShortName = getConfigs().getBoolean(config, "short_name");
			}			
			if (getConfig().hasPath("max_queries")) {
				this.maxQueries = getConfigs().getInt(config, "max_queries");
			}						
			this.outputRecordName = getConfigs().getString(config, "output_record_name");
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord)  {
			try {
				Object field = inputRecord.getFirstValue(this.ipAddress);
				if (null == field) {
					inputRecord.replaceValues(this.outputRecordName, EMPTY_STRING);
					return super.doProcess(inputRecord);
				}
				
				String ip_address = (String) field;
				String resolvedHostname = EMPTY_STRING;
				
				if (!blackIpHashSetCache.isEmpty() && blackIpHashSetCache.contains(ip_address)) {
					logger.debug("IP {} is in the black list. Skipping it.", ip_address);
					inputRecord.replaceValues(this.outputRecordName, EMPTY_STRING);
					return super.doProcess(inputRecord);
				}

				if (dnsCacheMap.containsKey(ip_address)) {
					resolvedHostname = dnsCacheMap.get(ip_address);
					inputRecord.replaceValues(this.outputRecordName, resolvedHostname);
					return super.doProcess(inputRecord);
				}

				if ((this.maxQueries == -1) || (this.maxQueries > dnsLookupCounter)) {
					String[] dnsServersArray = null;
					if (this.dnsServers != null && this.dnsServers.size() > 0) {
						dnsServersArray = Arrays.copyOf(this.dnsServers.toArray(), this.dnsServers.toArray().length, String[].class);
					}
					dnsLookupCounter++;
					try {
						resolvedHostname = reverseDns(ip_address,dnsServersArray,this.timeoutInSeconds);
					}
					catch (Exception e) {
						logger.debug("Exception while running reverseDns resolving for IP: {}. Adding it to black list.", ip_address);
						blackIpHashSetCache.add(ip_address);
						inputRecord.replaceValues(this.outputRecordName, EMPTY_STRING);
						return super.doProcess(inputRecord);
					}

					if (null==resolvedHostname || resolvedHostname.equalsIgnoreCase(EMPTY_STRING) || resolvedHostname.equalsIgnoreCase(ip_address)) {
						resolvedHostname = EMPTY_STRING;
					}
					else {
						if (this.isRemoveLastDot) {
							resolvedHostname = removeLastDot(resolvedHostname);
						}
						if (this.isShortName) {
							resolvedHostname = getShortName(resolvedHostname);
						}
						dnsCacheMap.put(ip_address, resolvedHostname);
					}
				}

				inputRecord.replaceValues(this.outputRecordName, resolvedHostname);
				return super.doProcess(inputRecord);
				
			}
			catch (Exception e) {
				// log debug as the log file can be very big with lots of messages like this
				logger.debug("Exception while doing DNS resolving of IP to Hostname", e);
				inputRecord.replaceValues(this.outputRecordName, EMPTY_STRING);
				return super.doProcess(inputRecord);
			}
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

		private static String removeLastDot(String input) {
			return input.endsWith(".") ? input.substring(0, input.length()-1) : input ; 
		}

		private static String getShortName(String input) {
			int firstDotIndex = input.indexOf('.') ;
			return (firstDotIndex > 0) ? input.substring(0, firstDotIndex) : input; 
		}

	}
}
