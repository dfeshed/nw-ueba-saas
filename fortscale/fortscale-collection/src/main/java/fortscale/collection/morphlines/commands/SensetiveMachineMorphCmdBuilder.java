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
import fortscale.services.SensetiveMachineService;

public class SensetiveMachineMorphCmdBuilder implements CommandBuilder {

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new IsSensetiveMachine(this, config, parent, child, context);
	}

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IsSensetiveMachine");
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction = true)
	public static class IsSensetiveMachine extends AbstractCommand {
		@Autowired
		private SensetiveMachineService service;

		private String machineNameField;
		private String isSensetiveMachineField;

		public IsSensetiveMachine(CommandBuilder builder, Config config,
				Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.machineNameField = getConfigs().getString(config,
					"machineNameField");
			this.isSensetiveMachineField = getConfigs().getString(config,
					"isSensetiveMachineField");
		}
		
		public IsSensetiveMachine(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, SensetiveMachineService service) {
			this(builder, config, parent, child, context);
			
			this.service = service;
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			// get the machine_name from the record
			String machineName = (String) inputRecord
					.getFirstValue(this.machineNameField);
			if (!StringUtils.isEmpty(machineName) && service != null) {
				boolean isSensetive = service.isMachineSensitive(machineName);
				inputRecord.put(this.isSensetiveMachineField, isSensetive);
			}
			return super.doProcess(inputRecord);
		}

	}
}
