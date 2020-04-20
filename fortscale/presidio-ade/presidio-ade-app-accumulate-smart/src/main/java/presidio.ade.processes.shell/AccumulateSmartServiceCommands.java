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
            @CliOption(key = {CommonStrings.COMMAND_LINE_SMART_RECORD_CONF_NAME_FIELD_NAME}, mandatory = true, help = "smart configuration name")
            final String configurationName,

            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
            final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_FIXED_DURATION_FIELD_NAME}, help = "the internal time intervals that the processing will be done by")
            final Double accumulationStrategy

    ) throws Exception {
        accumulateSmartRecordsExecutionService.run(configurationName, startTime, endTime, accumulationStrategy);
    }

    @CliCommand(value = "clean", help = "clean application data for specified time range and data source")
    public void clean(
            @CliOption(key = {CommonStrings.COMMAND_LINE_SMART_RECORD_CONF_NAME_FIELD_NAME}, mandatory = true, help = "smart configuration name")
            final String configurationName,

            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
            final Instant endTime

    ) throws Exception {
        accumulateSmartRecordsExecutionService.clean(configurationName, startTime, endTime);
    }

    @CliCommand(value = "cleanup", help = "cleanup events with specified time range, schema and fixed duration")
    public void cleanup(
            @CliOption(key = {CommonStrings.COMMAND_LINE_SMART_RECORD_CONF_NAME_FIELD_NAME}, help = "smart configuration name")
            final String configurationName,

            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater or equal than specified start time will be processed")
            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
            final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_FIXED_DURATION_FIELD_NAME}, help = "the internal time intervals that the processing will be done by")
            final Double accumulationStrategy

    ) throws Exception {
        accumulateSmartRecordsExecutionService.cleanup(configurationName, startTime, endTime, accumulationStrategy);
    }

}
