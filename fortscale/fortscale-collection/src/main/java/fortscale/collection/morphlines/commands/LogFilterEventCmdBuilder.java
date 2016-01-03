package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import fortscale.collection.ItemContext;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

/**
 * Created by shays on 12/29/2015.
 * This command log the reason why the event filtered from morphline.
 * It should be called manually from morphline before event dropped.
 *
 * To be able write the event to the monitor you should follow following:
 * 1. The record must have "MONITORING_SOURCE" field which will be the name of the origion where the event come from,
 *    usually file name. If there is not "monitoring_source" the default value for this is empty string.
 * 2.  The morhpline must supply errorMessage- this is the couse from event dropping. I.E.:
 *        LogFilterEvent : {
 *            errorMessage : "User name is missing"
 *        }
 */

public class LogFilterEventCmdBuilder implements CommandBuilder {


	//getNames is the how the command will be called in the morphline files
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("LogFilterEvent");
	}

	/**
	 * Generate the log filter event instance
	 * @param config
	 * @param parent
	 * @param child
	 * @param context
	 * @return
	 */
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new LogFilterEvent(this, config, parent, child, context);
	}


	@Configurable(preConstruction=true)
	public class LogFilterEvent extends AbstractCommand  {

		public static final String ITEM_CONTEXT = "ITEM_CONTEXT";
		public static final String ERROR_MESSAGE = "errorMessage";


		private final String errorMessage;

		public LogFilterEvent(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.errorMessage = getConfigs().getString(config, ERROR_MESSAGE);
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			// Extract the event source name (usually file name), or use empty string
			// as default
			ItemContext monitoringSource=null;
			if (inputRecord.get(ITEM_CONTEXT) != null){
				monitoringSource =  (ItemContext)inputRecord.get(ITEM_CONTEXT).get(0);
				//If taskMonitorHelper configured - log the event. If not - ignore.
				if (monitoringSource.getTaskMonitoringHelper()!=null && monitoringSource.getTaskMonitoringHelper().isMonitoredTask()){
					String sourceName = monitoringSource.getSourceName();
					if (sourceName == null){
						sourceName = "";
					}
					monitoringSource.getTaskMonitoringHelper().countNewFilteredEvents(sourceName,this.errorMessage);
				}
			}



			//Continue to process
			return super.doProcess(inputRecord);

		}
	}



}
