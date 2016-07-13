package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;

import com.typesafe.config.Config;

public class ParseKerberosTicketOptionsBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("ParseKerberosTicketOptions");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new ParseKerberosTicketOptions(this, config, parent, child, context);
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	///////////////////////////////////////////////////////////////////////////////
	public static final class ParseKerberosTicketOptions extends AbstractCommand {

		private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		private static final Logger logger = LoggerFactory.getLogger(ParseKerberosTicketOptions.class);
		
		private String ticketOptionsField;
		private String forwardableField;
		private String forwardedField;
		private String proxiedField;
		private String postdatedField;
		private String renewRequestField;
		private String constraintDelegationField;
		
		public ParseKerberosTicketOptions(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			
			// get mandatory ticket options field parameter
			this.ticketOptionsField = getConfigs().getString(config, "ticketOptionsField");
			// get optional output fields parameters
			this.forwardableField = getConfigs().getString(config, "forwardableField", "");
			this.forwardedField = getConfigs().getString(config, "forwardedField", "");
			this.proxiedField = getConfigs().getString(config, "proxiedField", "");
			this.postdatedField = getConfigs().getString(config, "postdatedField", "");
			this.renewRequestField = getConfigs().getString(config, "renewRequestField", "");
			this.constraintDelegationField = getConfigs().getString(config, "constraintDelegationField", "");
		}
		
		@Override
		public boolean doProcess(Record inputRecord)  {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			// get the ticket field
			String ticket = (String)inputRecord.getFirstValue(ticketOptionsField);
			if (ticket!=null) {
				try {
					// strip 0x from the start of the string
					if (ticket.startsWith("0x"))
						ticket = ticket.substring(2);
					// pad the ticket with zero to have it in length of 8
					ticket = StringUtils.leftPad(ticket, 8, '0');
					
					byte[] ticketBytes = DatatypeConverter.parseHexBinary(ticket);
					
					checkFieldMask(inputRecord, ticketBytes, 7, forwardableField);
					checkFieldMask(inputRecord, ticketBytes, 6, forwardedField);
					checkFieldMask(inputRecord, ticketBytes, 4, proxiedField);
					checkFieldMask(inputRecord, ticketBytes, 2, postdatedField);
					checkFieldMask(inputRecord, ticketBytes, 26, renewRequestField);
					checkFieldMask(inputRecord, ticketBytes, 10, constraintDelegationField);

					morphlineMetrics.kerberosTicketConverted++;
				} catch (IllegalArgumentException e) {
					morphlineMetrics.errorConvertingKerberosTicket++;
					logger.warn("error converting ticket option '{}' to byte array for record '{}'", ticket, inputRecord);
				}
			}		
			
			return super.doProcess(inputRecord);
		}
		
		private void checkFieldMask(Record record, byte[] ticket, int bit, String field) {
			// check field bit only if the output field is set
			if (!field.isEmpty()) {
				// check if the bit is set for the field
				if (isSet(ticket, bit-1)) {
					record.put(field, "True");
				} else {
					record.put(field, "False");
				}
			}
		}
		
		private boolean isSet(byte[] arr, int bit) {
			int index = bit / 8;  // Get the index of the array for the byte with this bit
		    int bitPosition = bit % 8;  // Position of this bit in a byte

		    if (index<arr.length)
		    	return (arr[index] >> bitPosition & 1) == 1;
		    else
		    	return false;
		}
	}
	
}
