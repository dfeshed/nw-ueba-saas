package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

/**
 * Command that converts the Java objects in a given field via <code>Object.toString()</code> to
 * their string representation, and lower case them.
 */

public final class ToLowerBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("toLower");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new ToLower(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private static final class ToLower extends AbstractCommand {

		private final String fieldName;

		public ToLower(CommandBuilder builder, Config config, Command parent,
				Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.fieldName = getConfigs().getString(config, "field");
			validateArguments();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected boolean doProcess(Record record) {
			ListIterator iter = record.get(fieldName).listIterator();
			while (iter.hasNext()) {
				String str = iter.next().toString();
				iter.set(str.toLowerCase());
			}

			// pass record to next command in chain:
			return super.doProcess(record);
		}

	}

	


}
