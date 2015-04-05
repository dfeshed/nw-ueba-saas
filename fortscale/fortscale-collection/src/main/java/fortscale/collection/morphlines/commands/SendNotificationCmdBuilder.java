package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;

import fortscale.services.notifications.AmtLoginAsMailNotificationGenerator;
import fortscale.services.notifications.AmtResetPwdNotificationGenerator;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by idanp on 9/18/2014.
 * This command send notification related to the record using the specific sending method implementation
 */
//@Configurable
public class SendNotificationCmdBuilder implements CommandBuilder {


	private static Logger logger = LoggerFactory.getLogger(SendNotificationCmdBuilder.class);

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("SendNotification");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new SendNotification(this, config, parent, child, context);
	}

	@Configurable(preConstruction=true)
	public class SendNotification extends AbstractCommand  {


		@Autowired
		private AmtResetPwdNotificationGenerator amtResetPwdNotificationGenerator;

		@Autowired
		private AmtLoginAsMailNotificationGenerator amtLoginAsMailNotificationGenerator;

		private final String notificationType;



		public SendNotification(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);


			this.notificationType = getConfigs().getString(config, "notificationType");


			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			if ("AmtPwdReset".equals(notificationType)) {
				amtResetPwdNotificationGenerator.createNotifications(inputRecord);
			} else if ("AmtLoginAsMail".equals(notificationType)) {
				amtLoginAsMailNotificationGenerator.createNotifications(inputRecord);
			}
			return super.doProcess(inputRecord);

		}
	}



}
