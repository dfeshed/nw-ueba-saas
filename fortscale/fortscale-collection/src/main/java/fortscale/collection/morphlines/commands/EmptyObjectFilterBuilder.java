package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

import fortscale.utils.logging.Logger;

/**
 * Command that succeeds if all field values of the given named fields are not
 * empty string and fails otherwise.
 */
public class EmptyObjectFilterBuilder implements CommandBuilder {
	private static Logger logger = Logger.getLogger(EmptyObjectFilterBuilder.class);

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("EmptyObjectFilter");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new EmptyObjectFilter(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	public static final class EmptyObjectFilter extends AbstractCommand {

		private final List<String> filterFields;
		private final String renderedConfig; // cached value

		public EmptyObjectFilter(CommandBuilder builder, Config config,
				Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			filterFields = getConfigs().getStringList(config, "filterFields");
			this.renderedConfig = config.root().render();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			for (String field : filterFields) {
				Object fieldValue = inputRecord.getFirstValue(field);
				if (fieldValue == null) {
					// drop record
					logger.debug("EmptyObjectFilter command droped record because {} is null. command: {}, record: {}", field, renderedConfig, inputRecord.toString());
					return true;
				}
				if (fieldValue instanceof String) {
					if (StringUtils.isBlank((String)fieldValue)) {
						// drop record
						logger.debug("EmptyObjectFilter command droped record because {} is empty. command: {}, record: {}", field, renderedConfig, inputRecord.toString());
						return true;
					}
				}
			}
			return super.doProcess(inputRecord);
		}

	}

}
