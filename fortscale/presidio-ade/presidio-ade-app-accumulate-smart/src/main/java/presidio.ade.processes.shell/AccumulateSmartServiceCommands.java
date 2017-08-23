package presidio.ade.processes.shell;

import fortscale.common.general.CommonStrings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;

import java.time.Instant;

/**
 * The supported CLI commands for the {@link AccumulateSmartRecordsExecutionService}.
 */
public class AccumulateSmartServiceCommands implements CommandMarker {
    @Autowired
    private AccumulateSmartRecordsExecutionService accumulateSmartRecordsExecutionService;

    @CliCommand(value = "run", help = "run events with specified time range and data source")
    public void run(
            @CliOption(key = {CommonStrings.COMMAND_LINE_SMART_RECORD_CONF_NAME_FIELD_NAME}, mandatory = true, help = "smart configuration name")            final String configurationName,

            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")            final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_ACCUMULATE_DURATION}, help = "the internal time intervals that the processing will be done by")            final Double accumulationStrategy

    ) throws Exception {
        accumulateSmartRecordsExecutionService.run(configurationName, startTime, endTime, accumulationStrategy);
    }

    @CliCommand(value = "clean", help = "clean application data for specified time range and data source")
    public void clean(
            @CliOption(key = {CommonStrings.COMMAND_LINE_SMART_RECORD_CONF_NAME_FIELD_NAME}, mandatory = true, help = "smart configuration name")            final String configurationName,

            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")            final Instant endTime

    ) throws Exception {
        accumulateSmartRecordsExecutionService.clean(configurationName, startTime, endTime);
    }
}
