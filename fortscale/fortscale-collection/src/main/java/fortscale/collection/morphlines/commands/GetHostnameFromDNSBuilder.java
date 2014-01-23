package fortscale.collection.morphlines.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.xbill.DNS.*;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import com.typesafe.config.Config;

public class GetHostnameFromDNSBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetHostnameFromDNS");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
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

		public GetHostnameFromDNS(CommandBuilder builder, Config config, Command parent, Command child,
				MorphlineContext context) {
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
			if ((this.maxQueries == -1) || (this.maxQueries > dnsLookupCounter)) {
				List<?> tmp = inputRecord.get(this.ipAddress );
				String ip = null;
				String[] dnsServersArray = null;
				if (tmp != null && tmp.size() > 0) {
					ip =(String) tmp.get(0);
				}
				if (this.dnsServers != null && this.dnsServers.size() > 0) {

					dnsServersArray = Arrays.copyOf(this.dnsServers.toArray(), this.dnsServers.toArray().length, String[].class);
				}
				if (ip!=null) {
					try {
						String result = reverseDns(ip,dnsServersArray,this.timeoutInSeconds);
						if (this.isRemoveLastDot) {
							result = removeLastDot(result);
						}
						if (this.isShortName) {
							result = getShortName(result,ip);
						}
						if ((!result.equals("")) && (!result.equals(ip))) {
							dnsLookupCounter++;
						}
						inputRecord.replaceValues(this.outputRecordName, result);
					} catch (IOException e) {
						inputRecord.replaceValues(this.outputRecordName, "");				
					}
				}
			}
			return super.doProcess(inputRecord);
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
			String result = null;
			if (input.endsWith(".")) {
				result = input.substring(0, input.length()-1);
			}
			else {
				result = input;
			}
			return result;
		}

		private static String getShortName(String input,String ipAddress) {
			String result = null;
			if ((!input.equals(ipAddress)) && (input.indexOf('.') > 0)) {
				result = input.substring(0, input.indexOf('.'));
			}
			else {
				result = input;
			}
			return result;
		}

	}
}

