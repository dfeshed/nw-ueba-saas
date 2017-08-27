package presidio.output.processor;

import fortscale.common.general.CommonStrings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import presidio.output.processor.services.OutputExecutionService;

import java.time.Instant;

/**
 * Defines all the supported Presidio commands
 *
 * Created by efratn on 12/06/2017.
 *
 */
@Component
public class OutputShellCommands implements CommandMarker {

    @Autowired
    private OutputExecutionService executionService;

    @CliCommand(value = "run", help = "run events with specified time range ")
    public void run(
            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
            final Instant endTime

    ) throws Exception {
           executionService.run(startTime, endTime);
    }

    @CliCommand(value = "clean", help = "clean application data for specified time range ")
    public void clean(
            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
            final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
            final Instant endTime

    ) throws Exception {
        executionService.clean(startTime, endTime);
    }

    @CliCommand(value = "cleanAll", help = "clean application data for specified data source")
    public void cleanAll() throws Exception {
        executionService.cleanAll();
    }
}
