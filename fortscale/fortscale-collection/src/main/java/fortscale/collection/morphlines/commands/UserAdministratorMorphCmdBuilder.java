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
import fortscale.collection.tagging.service.AdministratorAccountService;

public class UserAdministratorMorphCmdBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IsUserAdministrator");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new IsUserAdministrator(this, config, parent, child, context);
	}
		
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static class IsUserAdministrator extends AbstractCommand {
		
		protected String usernameField;
		private String isUserAdministratorField;
		@Autowired
		private AdministratorAccountService administratorAccountService;
		
		public IsUserAdministrator(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.usernameField = getConfigs().getString(config, "usernameField");
			this.isUserAdministratorField = getConfigs().getString(config, "isUserAdministratorField");			
		}

		public IsUserAdministrator(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, AdministratorAccountService service) {
			this(builder, config, parent, child, context);
			this.administratorAccountService = service;
		}


		protected boolean isUserAdministrator(Record record){
			if (record.getFirstValue(usernameField) != null) {
				String ret = RecordExtensions.getStringValue(record, usernameField);
				if(administratorAccountService != null && ret != null && ret != ""){
					return administratorAccountService.isUserAdministrator(ret);
				}
			}				
			return false;
			
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
		
			inputRecord.put(isUserAdministratorField, isUserAdministrator(inputRecord));
			return super.doProcess(inputRecord);

		}
	}
}