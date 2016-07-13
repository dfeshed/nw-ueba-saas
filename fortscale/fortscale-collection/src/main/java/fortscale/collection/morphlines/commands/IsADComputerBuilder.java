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

import fortscale.services.ComputerService;

public class IsADComputerBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IsADComputer");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new IsADComputer(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction = true)
	public static class IsADComputer extends AbstractCommand {

		private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		@Autowired
		private ComputerService computerService;
		
		private String hostnameField;
		private String outputField;
		
		protected IsADComputer(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			
			hostnameField = getConfigs().getString(config, "hostnameField");
			outputField = getConfigs().getString(config, "outputField");
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			if (computerService != null) {
				String hostname = (String) inputRecord.getFirstValue(hostnameField);
				boolean isInAD = computerService.isHostnameInAD(hostname);

				if (isInAD){
					morphlineMetrics.computerServiceFoundInAD++;
				}else{
					morphlineMetrics.computerServiceNotInAD++;
				}
				inputRecord.put(outputField, isInAD);
			}

			else {
				inputRecord.put(outputField, false);
				morphlineMetrics.computerServiceNotInAD++;
			}
			return super.doProcess(inputRecord);
		}
	}
}
