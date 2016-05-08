package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.domain.events.ComputerLoginEvent;
import fortscale.services.ipresolving.ComputerLoginResolver;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ComputerLoginUpdateBuilder implements CommandBuilder{
	private static Logger logger = LoggerFactory.getLogger(ComputerLoginUpdateBuilder.class);
	
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("ComputerLoginUpdate");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new ComputerLoginUpdate(this, config, parent, child, context);
	}

	@Configurable(preConstruction=true)
	public static final class ComputerLoginUpdate extends AbstractCommand {
		
		@Autowired
		private ComputerLoginResolver computerLoginResolver;

		private final String timestampepochFieldName;
		private final String ipaddressFieldName;
		private final String hostnameFieldName;
		private final String domainFieldName;
		private final int maxBatchSize;
		private List<ComputerLoginEvent> computerLoginEvents;
		
		

		public ComputerLoginUpdate(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.timestampepochFieldName = getConfigs().getString(config, "timestampepoch_field");
			this.ipaddressFieldName = getConfigs().getString(config, "ipaddress_field");
			this.hostnameFieldName = getConfigs().getString(config, "hostname_field");
			this.domainFieldName = getConfigs().getString(config, "domain_field");
			this.maxBatchSize = getConfigs().getInt(config, "max_batch_size", 1);
			if(maxBatchSize > 1){
				computerLoginEvents = new ArrayList<>();
			}
			
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			if(computerLoginResolver == null){
				logger.error("computerLoginResolver is null");
				return super.doProcess(inputRecord);
			}
			try{
				Long timestampepoch = RecordExtensions.getLongValue(inputRecord, timestampepochFieldName);
				String ipaddress = RecordExtensions.getStringValue(inputRecord, ipaddressFieldName);
				String hostname = RecordExtensions.getStringValue(inputRecord, hostnameFieldName);
				String domain = RecordExtensions.getStringValue(inputRecord, domainFieldName);
				
				ComputerLoginEvent computerLoginEvent = new ComputerLoginEvent();
				computerLoginEvent.setTimestampepoch(timestampepoch);
				computerLoginEvent.setIpaddress(ipaddress);
				computerLoginEvent.setPartOfVpn(false);
				hostname = hostname.substring(0, hostname.length() - 1);
				computerLoginEvent.setHostname(String.format("%s.%s", hostname.toLowerCase(), domain.toLowerCase()));
				if(maxBatchSize > 1){
					computerLoginEvents.add(computerLoginEvent);
					if(computerLoginEvents.size() >= maxBatchSize){
						computerLoginResolver.addComputerLogins(computerLoginEvents);
						computerLoginEvents.clear();
					}
				} else{
					computerLoginResolver.addComputerLogin(computerLoginEvent);
				}
			} catch(Exception e){
				logger.error("Got an exception while processing morphline record", e);
			}

			return super.doProcess(inputRecord);

		}
		
		@Override
		protected void doNotify(Record notification) {
			for (Object event : Notifications.getLifecycleEvents(notification)) {
				if (event == Notifications.LifecycleEvent.SHUTDOWN && computerLoginResolver!=null && computerLoginEvents != null) {
					if(computerLoginEvents.size() > 0){
						computerLoginResolver.addComputerLogins(computerLoginEvents);
					}
				}
			}
			super.doNotify(notification);
		}
	}
	
	
}
