package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;
import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.geoip.IpToLocationGeoIPService;
import fortscale.services.impl.UserServiceAccountServiceImpl;
import fortscale.services.impl.UsernameNormalizer;


public class UserServiceMorphCmdBuilder implements CommandBuilder {
	
	protected String usernameField;
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IsUserServiceAccount");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new IsUserServiceAccount(this, config, parent, child, context);
	}
	
	protected UserServiceAccountServiceImpl getUserServiceAccount(){
		return null;
	}
	
	protected boolean isUserServiceAccount(Record record){
		String ret = RecordExtensions.getStringValue(record, usernameField);
		UserServiceAccountServiceImpl userServiceAccountService = getUserServiceAccount();
		if(userServiceAccountService != null){
			return userServiceAccountService.isUserServiceAccount(ret);
		}
		else {			
			return false;
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private class IsUserServiceAccount extends AbstractCommand {
		
		
		
		private String isUserServiceAccountField;
		
		
		public IsUserServiceAccount(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			usernameField = getConfigs().getString(config, "usernameField");
			this.isUserServiceAccountField = getConfigs().getString(config, "isUserServiceAccountField");
			
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
		
			inputRecord.put(isUserServiceAccountField, isUserServiceAccount(inputRecord));

			return super.doProcess(inputRecord);

		}
	}
}
