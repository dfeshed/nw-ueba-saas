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

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.ComputerService;

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
		private ComputerService service;
		
		private String hostnameField;
		private String classificationField;
		
		public ClassifyHost(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			
			this.hostnameField = getConfigs().getString(config, "hostnameField");
			this.classificationField = getConfigs().getString(config, "classificationField");
		}
		
		public ClassifyHost(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, ComputerService service) {
			this(builder, config, parent, child, context);
			
			this.service = service;
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {			
			// get the hostname from the record
			String hostname = (String)inputRecord.getFirstValue(hostnameField);
			if (!StringUtils.isEmpty(hostname)) {
				// lookup the hostname and get the usage type
				ComputerUsageType usage = service.getComputerUsageType(hostname);
				inputRecord.put(classificationField, usage);
			}

			return super.doProcess(inputRecord);
		}
	}
	
	
}
