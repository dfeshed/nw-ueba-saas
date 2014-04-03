package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.machine.EndpointDetectionService;
import fortscale.services.machine.MachineInfo;

/**
 * This morphline command receive a hostname and sets values indicating if it is
 * a server or endpoint in the morphline record
 */
public class ClassifyHostBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("ClassifyHost");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new ClassifyHost(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static final class ClassifyHost extends AbstractCommand {
		
		@Autowired
		private EndpointDetectionService service;
		
		private String hostnameField;
		private String isEndpointField;
		private String isServerField;
		
		public ClassifyHost(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			
			this.hostnameField = getConfigs().getString(config, "hostnameField");
			this.isEndpointField = getConfigs().getString(config, "isEndpointField", null);
			this.isServerField = getConfigs().getString(config, "isServerField", null);
		}
		
		public ClassifyHost(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, EndpointDetectionService service) {
			this(builder, config, parent, child, context);
			
			this.service = service;
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
			
			// if both endpoint output field and server output field is missing do nothing
			if (StringUtils.isNotEmpty(isEndpointField) || StringUtils.isNotEmpty(isServerField)) {
				// get the hostname from the record
				String hostname = (String)inputRecord.getFirstValue(hostnameField);
				if (!StringUtils.isEmpty(hostname)) {
					// lookup the hostname in the endpoint detection service
					MachineInfo info = service.getMachineInfo(hostname);
					if (info!=null) {
						// put output values in the record
						if (info.isEndpoint()!=null && StringUtils.isNotEmpty(isEndpointField)) 
							inputRecord.put(isEndpointField, info.isEndpoint());
						
						if (info.isServer()!=null && StringUtils.isNotEmpty(isServerField))
							inputRecord.put(isServerField, info.isServer());
					}
				}
			}
			return super.doProcess(inputRecord);
		}
	}
	
	
}
