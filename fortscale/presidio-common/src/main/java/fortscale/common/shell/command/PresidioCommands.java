package fortscale.common.shell.command;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.PresidioSchemas;
import fortscale.common.shell.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Defines all the supported Presidio commands
 *
 * Created by efratn on 12/06/2017.
 *
 */
@Component
public class PresidioCommands implements CommandMarker {

    @Autowired
    private PresidioExecutionService executionService;

    @CliCommand(value = "run", help = "run events with specified time range and data source")
    public void run(
            @CliOption(key = {CommonStrings.COMMAND_LINE_DATA_SOURCE_FIELD_NAME}, mandatory = true, help = "data source")
            final PresidioSchemas presidioSchemas,

            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
            final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_FIXED_DURATION_FIELD_NAME}, help = "the internal time intervals that the processing will be done by")
            final Double fixedDuration

    ) throws Exception {
           executionService.run(presidioSchemas, startTime, endTime, fixedDuration);
    }

    @CliCommand(value = "clean", help = "clean application data for specified time range and data source")
    public void clean(
            @CliOption(key = {CommonStrings.COMMAND_LINE_DATA_SOURCE_FIELD_NAME}, mandatory = true, help = "data source")
            final PresidioSchemas presidioSchemas,

            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
            final Instant endTime

    ) throws Exception {
        executionService.clean(presidioSchemas, startTime, endTime);
    }

    @CliCommand(value = "cleanAll", help = "clean application data for specified data source")
    public void cleanAll(
            @CliOption(key = {CommonStrings.COMMAND_LINE_DATA_SOURCE_FIELD_NAME}, mandatory = true, help = "data source")
            final PresidioSchemas presidioSchemas

    ) throws Exception {
        executionService.cleanAll(presidioSchemas);
    }
}
