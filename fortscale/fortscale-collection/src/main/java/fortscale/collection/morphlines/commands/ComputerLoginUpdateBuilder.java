package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.dao.ComputerLoginEventRepository;



@Configurable(preConstruction=true)
public class ComputerLoginUpdateBuilder implements CommandBuilder{
	private static Logger logger = LoggerFactory.getLogger(ComputerLoginUpdateBuilder.class);

	@Autowired
	private ComputerLoginEventRepository computerLoginEventRepository;
	
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("ComputerLoginUpdate");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new ComputerLoginUpdate(this, config, parent, child, context);
	}

	
	private class ComputerLoginUpdate extends AbstractCommand {
		

		private final String timestampepochFieldName;
		private final String ipaddressFieldName;
		private final String hostnameFieldName;
		private final String domainFieldName;
		
		

		public ComputerLoginUpdate(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.timestampepochFieldName = getConfigs().getString(config, "timestampepoch_field");
			this.ipaddressFieldName = getConfigs().getString(config, "ipaddress_field");
			this.hostnameFieldName = getConfigs().getString(config, "hostname_field");
			this.domainFieldName = getConfigs().getString(config, "domain_field");
			
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			try{
				Long timestampepoch = RecordExtensions.getLongValue(inputRecord, timestampepochFieldName);
				String ipaddress = RecordExtensions.getStringValue(inputRecord, ipaddressFieldName);
				String hostname = RecordExtensions.getStringValue(inputRecord, hostnameFieldName);
				String domain = RecordExtensions.getStringValue(inputRecord, domainFieldName);
				
				ComputerLoginEvent computerLoginEvent = new ComputerLoginEvent();
				computerLoginEvent.setTimestampepoch(timestampepoch);
				computerLoginEvent.setIpaddress(ipaddress);
				computerLoginEvent.setHostname(String.format("%s.%s", hostname, domain));
				computerLoginEventRepository.save(computerLoginEvent);
			} catch(Exception e){
				logger.error("Got an exception while processing morphline record", e);
			}

			return super.doProcess(inputRecord);

		}
	}
}
