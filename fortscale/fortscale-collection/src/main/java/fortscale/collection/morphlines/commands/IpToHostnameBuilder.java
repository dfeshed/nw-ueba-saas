package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.ipresolving.IpToHostnameResolver;

public final class IpToHostnameBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("IpToHostname");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new IpToHostname(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static final class IpToHostname extends AbstractCommand {
		@Autowired
		private IpToHostnameResolver ipToHostnameResolver;
				
		private static final String STRING_EMPTY = "";
		private final String ipAddress;
		private final String timeStamp;
		private final String outputFieldName;
		private final boolean restrictToADName;
			
		public IpToHostname(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.ipAddress = getConfigs().getString(config, "ipAddress");
			this.timeStamp = getConfigs().getString(config, "timeStamp");
			this.outputFieldName = getConfigs().getString(config, "outputFieldName");
			this.restrictToADName = getConfigs().getBoolean(config, "restrictToADName", false);
			
			validateArguments();
		}

        @Override
		protected boolean doProcess(Record inputRecord) {
			// If we weren't able to connect or access the collection,
			// return an empty string
			try {
				String ip = RecordExtensions.getStringValue(inputRecord, this.ipAddress);
				Long ts = RecordExtensions.getLongValue(inputRecord, this.timeStamp);
				
				// Try and get a hostname to the IP
                inputRecord.put(this.outputFieldName, getHostname(ip, ts));
			} catch (IllegalArgumentException e) {
				// did not found ip or ts fields in input record
				inputRecord.put(this.outputFieldName, STRING_EMPTY);
			}

			return super.doProcess(inputRecord);

		}


		private String getHostname(String ip, long ts) {
			if (ip==null || ipToHostnameResolver==null)
				return STRING_EMPTY;
			
			String hostname = ipToHostnameResolver.resolve(ip, ts, restrictToADName);
			return (hostname==null)? STRING_EMPTY : hostname;
		}
	}
}
