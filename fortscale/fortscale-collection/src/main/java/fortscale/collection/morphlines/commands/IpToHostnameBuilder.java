package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.ipresolving.ComputerLoginResolver;
import fortscale.services.ipresolving.DhcpResolver;
import fortscale.services.ipresolving.DnsResolver;
import fortscale.utils.actdir.LogsToADConversions;

public class IpToHostnameBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IpToHostname");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new IpToHostname(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public class IpToHostname extends AbstractCommand {
		
		@Autowired
		private DhcpResolver dhcpResolver;
		@Autowired
		private DnsResolver dnsResolver;
		@Autowired
		private ComputerLoginResolver computerLoginResolver;

		
		private static final String STRING_EMPTY = "";
		private final String ipAddress;
		private final String timeStamp;
		private boolean useLoginResolver = false;
		private boolean useDhcpResolver = false;
		private boolean useDnsResolver = false;
		private final String outputRecordName;
		
		private boolean shortName = true;
		private boolean isRemoveLastDot = false;

		
		
		public IpToHostname(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.ipAddress = getConfigs().getString(config, "ipAddress");
			this.timeStamp = getConfigs().getString(config, "timeStamp");
			this.outputRecordName = getConfigs().getString(config, "outputRecordName");
			this.shortName = getConfigs().getBoolean(config, "short_name", true);
			this.isRemoveLastDot = getConfigs().getBoolean(config, "remove_last_dot", false);
			
			List<String> resolvers = getConfigs().getStringList(config, "resolvers");
			if(resolvers == null || resolvers.isEmpty()){
				useDhcpResolver = true;
				useDnsResolver = true;
				useLoginResolver = true;
			} else{
				for(String resolver: resolvers){
					switch(resolver){
					case "dhcp":
						useDhcpResolver = true;
						break;
					case "dns":
						useDnsResolver = true;
						break;
					case "logins":
						useLoginResolver = true;
						break;
					}					
				}
			}
			
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			// If we weren't able to connect or access the collection,
			// return an empty string
			
			try {
				
				String ip = RecordExtensions.getStringValue(inputRecord, this.ipAddress);
				Long ts = RecordExtensions.getLongValue(inputRecord, this.timeStamp);
				
				// Try and get a hostname to the IP
                inputRecord.put(this.outputRecordName, getHostname(ip, ts));

				
			} catch (IllegalArgumentException e) {
				// did not found ip or ts fields in input record
				inputRecord.put(this.outputRecordName, STRING_EMPTY);
			}
			

			return super.doProcess(inputRecord);

		}


		private String getHostname(String ip, long ts) {
			if (ip==null)
				return STRING_EMPTY;
			
			String ret = null;
			if(ret == null || ret.isEmpty() ){
				if(useLoginResolver && computerLoginResolver != null){
					ret = computerLoginResolver.getHostname(ip, ts);
				}
			}
			if(ret == null || ret.isEmpty() ){
				if(useDhcpResolver && dhcpResolver != null){
					ret = dhcpResolver.getHostname(ip, ts);
				}
			}
			if(ret == null || ret.isEmpty() ){
				if(useDnsResolver && dnsResolver != null){
					ret = dnsResolver.getHostname(ip);
				}
			}
			
			if (ret != null) {
				if (shortName ) {
                    ret = LogsToADConversions.getHostShortName(ret);
				} else if(isRemoveLastDot){
					ret = removeLastDot(ret);
				}
			}
			
			return ret;
		}
		
		private String removeLastDot(String input) {
			return input.endsWith(".") ? input.substring(0, input.length()-1) : input ; 
		}
		
	}
}
