package fortscale.collection.morphlines.commands;

import static org.apache.commons.lang.StringUtils.isNotEmpty; 

import java.util.Collection;
import java.util.Collections;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.services.ComputerService;

/**
 * This morphline command receive a hostname and sets a value with the 
 * normalized cluster name for that computer
 */
public class GetComputerClusterNameBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetComputerClusterName");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new GetComputerClusterName(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static final class GetComputerClusterName extends AbstractCommand {

		@Autowired
		private ComputerService service;
		
		private String hostnameField;
		private String clusterField;

		public GetComputerClusterName(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			
			this.hostnameField = getConfigs().getString(config, "hostnameField");
			this.clusterField = getConfigs().getString(config, "clusterField");
		}
		
		public GetComputerClusterName(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, ComputerService service) {
			this(builder, config, parent, child, context);
			this.service = service;
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {			
			// get the hostname from the record
			String hostname = (String)inputRecord.getFirstValue(hostnameField);
			if (isNotEmpty(hostname) && service!=null) {
				String clusterName = service.getClusterGroupNameForHostname(hostname);
				inputRecord.put(clusterField, clusterName);
			}

			return super.doProcess(inputRecord);
		}

	
	}
}
