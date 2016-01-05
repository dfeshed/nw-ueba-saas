package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by shays on 12/29/2015.
   Filter events if account name is computer or not computer depend on parameter.
 *
 * To be able write the event to the monitor you should follow following:
 * 1. The record must have "MONITORING_SOURCE" field which will be the name of the origion where the event come from,
 *    usually file name. If there is not "monitoring_source" the default value for this is empty string.
 * 2.  The morhpline must supply errorMessage- this is the couse from event dropping. I.E.:
 *        FilterAccountNameIsComputer : {
 *            filterIfNotComputer : "true"
 *        }
 */

public class FilterAccountNameIsNotComputerCmdBuilder implements CommandBuilder {


	//getNames is the how the command will be called in the morphline files
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("FilterAccountNameIsNotComputer");
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
		return new FilterAccountNameIsNotComputer(this, config, parent, child, context);
	}


	@Configurable(preConstruction=true)
	public class FilterAccountNameIsNotComputer extends AbstractCommand  {

		public static final String FILTER_IF_NOT_COMPUTER = "filterIfNotComputer";

		MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();



		public FilterAccountNameIsNotComputer(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record record) {

			String account_name = (String)record.get("account_name").get(0);
			Boolean isComputer = account_name.contains("$") ? true : false;
			if (isComputer){
				record.replaceValues("isComputer", isComputer);
				record.replaceValues("account_name", account_name);
				String account_domain = (String)record.get("account_domain").get(0);
				record.replaceValues("account_domain", account_domain);

			} else{
				commandMonitoringHelper.addFilteredEventToMonitoring(record, CollectionMessages.ACCOUNT_NAME_IS_NOT_COMPUTER);
				return true;
			}
			//Continue to process
			return super.doProcess(record);

		}
	}

}
