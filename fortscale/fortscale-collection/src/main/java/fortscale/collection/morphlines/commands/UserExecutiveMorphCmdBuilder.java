package fortscale.collection.morphlines.commands;

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

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.tagging.service.ExecutiveAccountService;

public class UserExecutiveMorphCmdBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IsUserExecutive");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new IsUserExecutive(this, config, parent, child, context);
	}
		
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static class IsUserExecutive extends AbstractCommand {
		
		protected String usernameField;
		private String isUserExecutiveField;
		@Autowired
		private ExecutiveAccountService executiveAccountService;
		
		public IsUserExecutive(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.usernameField = getConfigs().getString(config, "usernameField");
			this.isUserExecutiveField = getConfigs().getString(config, "isUserExecutiveField");			
		}

		public IsUserExecutive(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, ExecutiveAccountService service) {
			this(builder, config, parent, child, context);
			this.executiveAccountService = service;
		}


		protected boolean isUserExecutive(Record record){
			if (record.getFirstValue(usernameField) != null) {
				String ret = RecordExtensions.getStringValue(record, usernameField);
				if(executiveAccountService != null && ret != null && ret != ""){
					return executiveAccountService.isUserExecutive(ret);
				}
			}				
			return false;
			
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
			inputRecord.put(isUserExecutiveField, isUserExecutive(inputRecord));
			return super.doProcess(inputRecord);

		}
	}
}