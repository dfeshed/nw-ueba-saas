package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;


/**
 * Command that converts the Java objects in a given field via <code>Object.toString()</code> to
 * their string representation, and performs substring manipulation on it
 */
public class SubstringBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("SubString");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new SubString(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	public static final class SubString extends AbstractCommand {

		private final String fieldName;
		private final int startIndex;
		private final int endIndex;
		private final String endCharacter;

		public SubString(CommandBuilder builder, Config config, Command parent,
				Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.fieldName = getConfigs().getString(config, "field");
			this.startIndex = getConfigs().getInt(config, "startIndex", 0);
			this.endIndex = getConfigs().getInt(config, "endIndex", 0);
			this.endCharacter = getConfigs().getString(config, "endCharacter", "");
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected boolean doProcess(Record record) {
			ListIterator iter = record.get(fieldName).listIterator();
			while (iter.hasNext()) {
				Object next = iter.next();
				if (next!=null) {
					String str = next.toString();
					
					// ensure we are not out of bounds
					int start = Math.min(startIndex, str.length()-1);
					int end = Math.min(endIndex, str.length());
					
					if (StringUtils.isNotEmpty(endCharacter)) {
						if (str.contains(endCharacter))
							str = str.substring(start, str.indexOf(endCharacter));
					} else if (end!=0) {
						str = str.substring(start, end);
					} else if (start!=0 && endIndex==0 && StringUtils.isEmpty(endCharacter)) {
						str = str.substring(start);
					}
					iter.set(str);
				}
			}

			// pass record to next command in chain:
			return super.doProcess(record);
		}

	}
	
}
