package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.net.util.SubnetUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

/**
 * Morphline command that recieves any ip and CIDR format than checks for match. 
 * If a match between the ip and the cidr format exist than it sets True value 
 * for the output record field, otherwise set False in the output field.
 */
public class MatchIPBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("MatchIP");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new MatchIP(this, config, parent, child, context);
	}

	///////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	///////////////////////////////////////////////////////////////////////////////
	public static final class MatchIP extends AbstractCommand {
	
		private final String ipAddress;
		private final String output;
		private final String cidr;
		
		public MatchIP(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			
			this.ipAddress = getConfigs().getString(config, "ipAddress");
			this.output = getConfigs().getString(config, "output");
			this.cidr = getConfigs().getString(config, "cidr");
		}
	
		
		@Override
		protected boolean doProcess(Record inputRecord)  {
			
			try {
				// try and get the ip address from the input record
				String address = (String)inputRecord.getFirstValue(ipAddress);
				
				// calculate match between cidr and the ip address
				if (cidr.contains("/")) {
					SubnetUtils utils = new SubnetUtils(cidr);
					boolean match = utils.getInfo().isInRange(address);
	 
					inputRecord.put(output, match);
				} else {
					boolean match = address.equals(cidr);
					inputRecord.put(output, match);
				}

			} catch (Exception e) {
				// put false in output field
				inputRecord.put(output, Boolean.FALSE);
			}
			return super.doProcess(inputRecord);
		}
		
	}
	
}
