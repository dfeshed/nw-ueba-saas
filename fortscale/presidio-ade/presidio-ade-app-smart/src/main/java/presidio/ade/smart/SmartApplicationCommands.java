package presidio.ade.smart;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * The supported CLI commands for the {@link SmartService}.
 *
 * @author Lior Govrin
 */
@Component
public class SmartApplicationCommands implements CommandMarker {
	@Autowired
	private SmartService smartService;

	@CliCommand(value = "process", help = "Create smart records from the given configuration and time range.")
	public void process(
			@CliOption(
					key = CommonStrings.COMMAND_LINE_SMART_RECORD_CONF_NAME_FIELD_NAME,
					mandatory = true,
					help = "The name of the smart record configuration."
			) final String smartRecordConfName,

			@CliOption(
					key = CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME,
					mandatory = true,
					help = "The start date of the smart records created."
			) final Instant startDate,

			@CliOption(
					key = CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME,
					mandatory = true,
					help = "The end date of the smart records created."
			) final Instant endDate
	) throws Exception {
		smartService.process(smartRecordConfName, new TimeRange(startDate, endDate));
	}

	@CliCommand(value = "cleanup", help = "cleanup events with specified time range, schema and fixed duration")
	public void cleanup(
			@CliOption(
					key = CommonStrings.COMMAND_LINE_SMART_RECORD_CONF_NAME_FIELD_NAME,
					help = "The name of the smart record configuration."
			) final String smartRecordConfName,

			@CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME},
					mandatory = true,
					help = "events with (logical) time greater than or equal specified start time will be deleted")
			final Instant startDate,

			@CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME},
					mandatory = true,
					help = "events with (logical) time smaller than specified end time will be deleted")
			final Instant endDate

	) throws Exception {
		smartService.cleanup(smartRecordConfName, new TimeRange(startDate, endDate));
	}
}
