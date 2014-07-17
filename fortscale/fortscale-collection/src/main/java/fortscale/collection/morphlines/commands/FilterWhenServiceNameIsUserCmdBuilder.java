package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.typesafe.config.Config;

import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;

public class FilterWhenServiceNameIsUserCmdBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("FilterWhenServiceNameIsUser");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new FilterWhenServiceNameIsUser(this, config, parent, child,
				context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction = true)
	public static class FilterWhenServiceNameIsUser extends AbstractCommand {

		@Autowired
		private ComputerRepository computerRepository;
		private String serviceNameField;
		private String regex;
		private Pattern regexMatcher;
		private String regexReplacement;
		private LoadingCache<String, Boolean> machinesCache;

		protected FilterWhenServiceNameIsUser(CommandBuilder builder,
				Config config, Command parent, Command child,
				MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.serviceNameField = getConfigs().getString(config,
					"serviceName");
			this.machinesCache = CacheBuilder.newBuilder().maximumSize(100000)
					.expireAfterWrite(1, TimeUnit.HOURS)
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
			String serviceName = (String) inputRecord
					.getFirstValue(this.serviceNameField);
			Matcher m = regexMatcher.matcher(serviceName);
			if (m.matches()) {
				serviceName = m.replaceAll(regexReplacement);
			} else {
				return true;
			}

			serviceName = serviceName.toUpperCase();
			boolean isMachine = machinesCache.getUnchecked(serviceName);
			if (isMachine) {
				return super.doProcess(inputRecord);
			} else {
				return true;
			}
		}
	}
}
