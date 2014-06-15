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

import fortscale.services.SensitiveMachineService;

public class SensitiveMachineMorphCmdBuilder implements CommandBuilder {

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new IsSensitiveMachine(this, config, parent, child, context);
	}

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IsSensitiveMachine");
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction = true)
	public static class IsSensitiveMachine extends AbstractCommand {
		@Autowired
		private SensitiveMachineService service;

		private String machineNameField;
		private String isSensitiveMachineField;

		public IsSensitiveMachine(CommandBuilder builder, Config config,
				Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.machineNameField = getConfigs().getString(config,
					"machineNameField");
			this.isSensitiveMachineField = getConfigs().getString(config,
					"isSensitiveMachineField");
		}
		
		public IsSensitiveMachine(CommandBuilder builder, Config config,
				Command parent, Command child, MorphlineContext context, SensitiveMachineService service) {
			this(builder, config, parent, child, context);
			this.service = service;
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
			// get the machine_name from the record
			String machineName = (String) inputRecord
					.getFirstValue(this.machineNameField);
			boolean isSensitive = false;
			if (!StringUtils.isEmpty(machineName) && service != null) {
				isSensitive = service.isMachineSensitive(machineName);
			}
			inputRecord.put(this.isSensitiveMachineField, isSensitive);
			return super.doProcess(inputRecord);
		}

	}
}
