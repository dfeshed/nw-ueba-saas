package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import fortscale.streaming.task.monitor.TaskMonitoringHelper;
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

public class LogFilterEventCmdBuilder implements CommandBuilder {

	public static final String ERROR_MESSAGE = "errorMessage";

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("SendNotification");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new LogFilterEvent(this, config, parent, child, context);
	}

	@Configurable(preConstruction=true)
	public class LogFilterEvent extends AbstractCommand  {


		@Autowired
		TaskMonitoringHelper<String> taskMonitoringHelper;


		private final String errorMessage;



		public LogFilterEvent(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.errorMessage = getConfigs().getString(config, ERROR_MESSAGE);
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			if (taskMonitoringHelper!=null && taskMonitoringHelper.isMonitoredTask()){
				taskMonitoringHelper.countNewFilteredEvents("",this.errorMessage);
			}
			return super.doProcess(inputRecord);

		}
	}



}
