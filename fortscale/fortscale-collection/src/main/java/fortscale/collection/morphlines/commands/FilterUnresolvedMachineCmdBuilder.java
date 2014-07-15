package fortscale.collection.morphlines.commands;


import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.typesafe.config.Config;

public class FilterUnresolvedMachineCmdBuilder  implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("FilterUnresolvedMachine");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new FilterUnresolvedMachine(this, config, parent, child, context);
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable()
	public static class FilterUnresolvedMachine extends AbstractCommand {
		@Value("${ipresolving.fail.filter:}") 
		private String filterOnFail = "";
		private String machineNameField;
		public FilterUnresolvedMachine(CommandBuilder builder,
				Config config, Command parent, Command child,
				MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.machineNameField = getConfigs().getString(config,
					"machineNameField");
		}
		@Override
		protected boolean doProcess(Record inputRecord) {
			String machineName = (String) inputRecord
					.getFirstValue(this.machineNameField);
			if(filterOnFail.equals("true") && StringUtils.isEmpty(machineName)){
				return true;
			}
			return super.doProcess(inputRecord);
		}
	}
}

