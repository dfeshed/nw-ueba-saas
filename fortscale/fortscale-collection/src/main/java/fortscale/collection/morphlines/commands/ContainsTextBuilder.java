package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Configs;
import org.kitesdk.morphline.base.FieldExpression;

import java.util.*;

public class ContainsTextBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("containsText");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new ContainsText(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private static final class ContainsText extends AbstractCommand {

		MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		private final Set<Map.Entry<String, Object>> entrySet;
		private final String renderedConfig; // cached value

		public ContainsText(CommandBuilder builder, Config config, Command parent,
				Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.entrySet = new Configs().getEntrySet(config);
			for (Map.Entry<String, Object> entry : entrySet) {
				if (!(entry.getValue() instanceof Collection)) {
					entry.setValue(new FieldExpression(entry.getValue()
							.toString(), getConfig()));
				}
			}
			this.renderedConfig = config.root().render();
		}

		@Override
		protected boolean doProcess(Record record) {
			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getItemContext(record).getMorphlineMetrics();

			for (Map.Entry<String, Object> entry : entrySet) {
				String fieldName = entry.getKey();
				List<?> values = record.get(fieldName);
				Object entryValue = entry.getValue();
				Collection<?> results;
				if (entryValue instanceof Collection) {
					results = (Collection<?>) entryValue;
				} else {
					results = ((FieldExpression) entryValue).evaluate(record);
				}
				boolean found = false;
				for (Object result : results) {
					for (Object value : values) {
						if (value!=null && value.toString().contains(result.toString())) {
							found = true;
							break;
						}
					}
					if (found) break;
				}
				if (!found) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(
								"Contains command failed because it could not find any of {} in values: {} for command: {}",
								new Object[] { results, values, renderedConfig });
					}
					return false;
				}
			}
			return super.doProcess(record);
		}

	}

}
