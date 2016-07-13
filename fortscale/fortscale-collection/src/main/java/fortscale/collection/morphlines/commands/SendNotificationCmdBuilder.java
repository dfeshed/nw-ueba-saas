package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;


/**
 * Created by idanp on 9/18/2014.
 * This command send notification related to the record using the specific sending method implementation
 */
//@Configurable
public class SendNotificationCmdBuilder implements CommandBuilder {
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
		private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		private final String notificationType;

		public SendNotification(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);

			this.notificationType = getConfigs().getString(config, "notificationType");

			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			morphlineMetrics.sendNotification++;
			return super.doProcess(inputRecord);

		}
	}
}
