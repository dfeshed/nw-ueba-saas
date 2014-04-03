package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;



public class IpToHostnameBuilder implements CommandBuilder {
	
	private DhcpResolver dhcpResolver = new DhcpResolver();
	
	private DnsResolver dnsResolver = new DnsResolver();
	
	private ComputerLoginResolver computerLoginResolver = new ComputerLoginResolver();
	
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
	private class IpToHostname extends AbstractCommand {
		
		
		private static final String STRING_EMPTY = "";
		private final String ipAddress;
		private final String timeStamp;
		private final List<String> resolvers;
		private final String outputRecordName;
		
		private boolean shortName = true;
		private boolean isRemoveLastDot = false;

		
		
		public IpToHostname(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.ipAddress = getConfigs().getString(config, "ipAddress");
			this.timeStamp = getConfigs().getString(config, "timeStamp");
			this.resolvers = getConfigs().getStringList(config, "resolvers");
			this.outputRecordName = getConfigs().getString(config, "outputRecordName");
			this.shortName = getConfigs().getBoolean(config, "short_name", true);
			this.isRemoveLastDot = getConfigs().getBoolean(config, "remove_last_dot", false);
			
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
			for(String resolver: resolvers){
				switch(resolver){
				case "dhcp":
					ret = dhcpResolver.getHostname(ip, ts);
					break;
				case "dns":
					ret = dnsResolver.getHostname(ip);
					break;
				case "logins":
					ret = computerLoginResolver.getHostname(ip, ts);
					break;
				}
				
				if(ret != null && !ret.isEmpty()){
					break;
				}
			}
			
			if (ret != null) {
				if (shortName ) {
					ret = getShortName(ret);
				} else if(isRemoveLastDot){
					ret = removeLastDot(ret);
				}
			}
			
			return ret;
		}
		
		private String removeLastDot(String input) {
			return input.endsWith(".") ? input.substring(0, input.length()-1) : input ; 
		}
		
		private String getShortName(String input) {
			int firstDotIndex = input.indexOf('.') ;
			return (firstDotIndex > 0) ? input.substring(0, firstDotIndex) : input; 
		}
	}
}
