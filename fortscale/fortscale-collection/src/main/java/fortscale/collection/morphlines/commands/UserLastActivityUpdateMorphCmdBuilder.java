package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.domain.events.LogEventsEnum;
import fortscale.services.UserService;
import fortscale.utils.TimestampUtils;

public class UserLastActivityUpdateMorphCmdBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("UserLastActivityUpdate");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new UserLastActivityUpdate(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public class UserLastActivityUpdate extends AbstractCommand {

		@Autowired
		private UserService userService;
		
		Map<String, Long> userLastActivityMap = new HashMap<>();
		
		
		private final LogEventsEnum logEventsType;
		private final String normalizedUsernameField;
		private final String epochtimestampField;
		
		public UserLastActivityUpdate(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.logEventsType = LogEventsEnum.valueOf(getConfigs().getString(config, "logEventsType"));
			this.normalizedUsernameField = getConfigs().getString(config, "normalizedUsernameField");
			this.epochtimestampField = getConfigs().getString(config, "epochtimestampField");

		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			String normalizedUsername = RecordExtensions.getStringValue(inputRecord, normalizedUsernameField);
			Long epochtime = TimestampUtils.convertToSeconds(RecordExtensions.getLongValue(inputRecord, epochtimestampField));
			

			// Find the last activity of the user (if exist) and update it if the event is newer than the event's activity
			Long userLastActivity = userLastActivityMap.get(normalizedUsername);
			if(userLastActivity == null || userLastActivity < epochtime){
				this.userLastActivityMap.put(normalizedUsername, epochtime);
			}


			return super.doProcess(inputRecord);

		}
		
		@Override
		protected void doNotify(Record notification) {
			for (Object event : Notifications.getLifecycleEvents(notification)) {
				if (event == Notifications.LifecycleEvent.SHUTDOWN && userService!=null) {
					// update all the users in the map in mongo: both the last-activity and the last-activity-per-type
					userService.updateUsersLastActivityGeneralAndPerType(logEventsType, userLastActivityMap);
				}
			}
			super.doNotify(notification);
		}
	}
}
