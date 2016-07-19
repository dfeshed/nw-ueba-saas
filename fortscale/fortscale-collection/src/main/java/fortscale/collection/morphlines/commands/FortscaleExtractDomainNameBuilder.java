package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

public class FortscaleExtractDomainNameBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("FortscaleExtractDomainName");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new FortscaleExtractDomainName(this, config, parent, child, context);
	}

	///////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	///////////////////////////////////////////////////////////////////////////////
	private static final class FortscaleExtractDomainName extends AbstractCommand {

		private final String recordField;
		private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		public FortscaleExtractDomainName(CommandBuilder builder, Config config, Command parent, Command child,
				MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.recordField = getConfigs().getString(config, "field");
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord)  {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);
			if (morphlineMetrics != null) {
				morphlineMetrics.extractingDomainName++;
			}

			String domain = getField(inputRecord, recordField);
			domain = extractDomainFromServiceName(domain);
			inputRecord.replaceValues(recordField,domain);
			return super.doProcess(inputRecord);

		}


		private String getField(Record record,String fieldName) {
			Object[] tmp = record.getFields().get(fieldName).toArray();
			if (tmp.length > 0)
			{
				return (String) tmp[0];
			}
			return "";
		}
		// service name is build in this format: service/domain
		private String extractDomainFromServiceName(String serviceName){
			return serviceName.split("/")[1];

		}

		/*private String extractGenericDnsName(String fullDnsName) {
			String genericDnsName = fullDnsName;
			try {
				String[] dns_prefixes = fullDnsName.split("\\.");
				genericDnsName = dns_prefixes[dns_prefixes.length-1];
				for (int i=dns_prefixes.length-2; i>=0; i--) {
					if (TLD_SET.contains(dns_prefixes[i].toUpperCase())) {
						genericDnsName = dns_prefixes[i] + "." + genericDnsName; 
					}
					else {
						genericDnsName = dns_prefixes[i] + "." + genericDnsName;
						break;
					}
				}
			}
			catch (Exception e) {
				System.out.println("Catched Exception on Request init: Could not parse generic domain name.");
				e.printStackTrace();
			}

			return genericDnsName;
		}		*/
	}
}
