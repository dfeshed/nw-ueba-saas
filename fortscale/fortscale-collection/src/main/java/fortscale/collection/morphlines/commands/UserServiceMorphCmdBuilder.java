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
import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.UserTaggingService;
import fortscale.utils.logging.Logger;

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
		private static Logger logger = Logger.getLogger(IsUserServiceAccount.class);
		
		private String usernameField;
		private String isUserServiceAccountField;
		@Autowired
		private UserTaggingService userTaggingService;
		
		public IsUserServiceAccount(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.usernameField = getConfigs().getString(config, "usernameField");
			this.isUserServiceAccountField = getConfigs().getString(config, "isUserServiceAccountField");			
		}

		public IsUserServiceAccount(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context, UserTaggingService service) {
			this(builder, config, parent, child, context);
			this.userTaggingService = service;
		}


		protected boolean isUserServiceAccount(Record record) throws Exception{
			if (record.getFirstValue(usernameField) != null) {
				String username = RecordExtensions.getStringValue(record, usernameField);
				if(userTaggingService != null && username != null && username != ""){
					return userTaggingService.isUserTagged(UserTagEnum.service.getId(), username);
				}
			}				
			return false;
			
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
			try {
				inputRecord.put(isUserServiceAccountField, isUserServiceAccount(inputRecord));
			} catch (Exception e) {
				logger.error("got and exception while tagging event with isServiceAccount", e);
			}

			return super.doProcess(inputRecord);

		}
	}
}
