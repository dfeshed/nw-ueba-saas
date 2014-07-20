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
import fortscale.collection.tagging.service.UserServiceAccountService;

public class UserServiceMorphCmdBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IsUserServiceAccount");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new IsUserServiceAccount(this, config, parent, child, context);
	}
		
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static class IsUserServiceAccount extends AbstractCommand {
		
		private String usernameField;
		private String isUserServiceAccountField;
		@Autowired
		private UserServiceAccountService userServiceAccountService;
		
		public IsUserServiceAccount(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.usernameField = getConfigs().getString(config, "usernameField");
			this.isUserServiceAccountField = getConfigs().getString(config, "isUserServiceAccountField");			
		}

		public IsUserServiceAccount(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, UserServiceAccountService service) {
			this(builder, config, parent, child, context);
			this.userServiceAccountService = service;
		}


		protected boolean isUserServiceAccount(Record record){
			if (record.getFirstValue(usernameField) != null) {
				String ret = RecordExtensions.getStringValue(record, usernameField);
				if(userServiceAccountService != null && ret != null && ret != ""){
					return userServiceAccountService.isUserServiceAccount(ret);
				}
			}				
			return false;
			
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
		
			inputRecord.put(isUserServiceAccountField, isUserServiceAccount(inputRecord));

			return super.doProcess(inputRecord);

		}
	}
}
