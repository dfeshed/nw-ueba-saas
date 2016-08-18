package fortscale.collection.morphlines.commands;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.utils.logging.Logger;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterWhenServiceNameIsNotComputerCmdBuilder implements CommandBuilder {
	private static Logger logger = Logger.getLogger(FilterWhenServiceNameIsNotComputerCmdBuilder.class);
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("FilterWhenServiceNameIsNotComputer");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new FilterWhenServiceNameIsNotComputer(this, config, parent, child,
				context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction = true)
	public static class FilterWhenServiceNameIsNotComputer extends AbstractCommand {

		// Autowired with 'required = false' - this is a workaround due to the wrong
		// implementation of the process method that doesn't filter events when the computerRepository is null
		@Autowired(required = false)
		private ComputerRepository computerRepository;

		MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		private String serviceNameField;
		private String regex;
		private Pattern regexMatcher;
		private String regexReplacement;
		private LoadingCache<String, Boolean> machinesCache;

		protected FilterWhenServiceNameIsNotComputer(CommandBuilder builder,
				Config config, Command parent, Command child,
				MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.serviceNameField = getConfigs().getString(config,
					"serviceName");
			this.machinesCache = CacheBuilder.newBuilder().maximumSize(100000)
					.build(new CacheLoader<String, Boolean>() {
						public Boolean load(String key) {
							return getIsMachine(key);
						}
					});
			this.regex = getConfigs().getString(config, "regex", null);
			if (regex != null) {
				String[] regexArray = regex.split("# #");
				if(regexArray.length != 2){
					throw new IllegalArgumentException("Bad regex format. Regex must be in format: (.*)# #(.*)");
				}
				regexMatcher = Pattern.compile(regexArray[0]);
				regexReplacement = regexArray[1];
			}
		}

		private Boolean getIsMachine(String computerName) {
			Computer computer = computerRepository.findByName(computerName);
			if (computer == null) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			if (computerRepository == null){
				return super.doProcess(inputRecord);
			}
			String serviceName = (String) inputRecord
					.getFirstValue(this.serviceNameField);
			Matcher m = regexMatcher.matcher(serviceName);
			if (m.matches()) {
				serviceName = m.replaceAll(regexReplacement);
			} else {
				if (morphlineMetrics != null) {
					morphlineMetrics.serviceNameNotMatchRegularExpression++;
				}
				commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord,
						CollectionMessages.SERVICE_NAME_NOT_MATCH_REGULAR_EXPRESSION);
				return true;
			}

			serviceName = serviceName.toUpperCase();
			boolean isMachine = machinesCache.getUnchecked(serviceName);
			if (isMachine) {
				if (morphlineMetrics != null) {
					morphlineMetrics.serviceNameIsComputer++;
				}
				return super.doProcess(inputRecord);
			} else {
				logger.debug("Filter event since service name [{}] is not a computer.", serviceName);
				if (morphlineMetrics != null) {
					morphlineMetrics.serviceNameIsNotComputer++;
				}
				commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord,
						CollectionMessages.SERVICE_NAME_IS_NOT_COMPUTER);
				return true;
			}
		}
	}
}
