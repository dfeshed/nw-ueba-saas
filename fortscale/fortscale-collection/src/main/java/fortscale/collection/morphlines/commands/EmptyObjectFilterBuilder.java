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
				@SuppressWarnings("unchecked")
				List<Object> fieldValues = inputRecord.get(field);
				
				if (fieldValues.isEmpty()) {
					// drop record
					logger.debug("EmptyObjectFilter command droped record because {} does not contains any value. command: {}, record: {}", field, renderedConfig, inputRecord.toString());
					return true;
				}
				boolean isAllFieldValueEmpty = true;
				for(Object fieldValue: fieldValues){
					if (fieldValue instanceof String) {
						if (!StringUtils.isBlank((String)fieldValue)) {
							isAllFieldValueEmpty = false;
							break;
						}
					}
				}
				if (isAllFieldValueEmpty) {
					// drop record
					logger.debug("EmptyObjectFilter command droped record because {} contains only empty values. command: {}, record: {}", field, renderedConfig, inputRecord.toString());
					return true;
				}
			}
			return super.doProcess(inputRecord);
		}

	}

}
