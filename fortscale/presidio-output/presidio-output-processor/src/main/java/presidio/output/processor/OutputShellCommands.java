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
 * <p>
 * Created by efratn on 12/06/2017.
 */
@Component
public class OutputShellCommands implements CommandMarker {

    private final String HOURLY_OUTPUT_PROCESSOR_RUN = "hourlyOutputProcessorRun";
    private final String DAILY_OUTPUT_PROCESSOR_RUN = "dailyOutputProcessorRun";

    @Autowired
    private OutputExecutionService executionService;

    @CliCommand(value = "run", help = "run events with specified time range ")
    public void run(
            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed") final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed") final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_FIXED_DURATION_FIELD_NAME}, help = "the internal time intervals that the processing will be done by") final Double fixedDuration

    ) throws Exception {
        Thread.currentThread().setName(HOURLY_OUTPUT_PROCESSOR_RUN + startTime.toString());
        executionService.run(startTime, endTime);
    }

    @CliCommand(value = "recalculate-user-score", help = "run daily calculation for output")
    public void runDaily(
    ) throws Exception {
        Thread.currentThread().setName(DAILY_OUTPUT_PROCESSOR_RUN + Instant.now().toString());
        executionService.recalculateUserScore();
    }

    @CliCommand(value = "cleanup", help = "clean application data for specified time range ")
    public void cleanup(
            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed") final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed") final Instant endTime

    ) throws Exception {
        executionService.clean(startTime, endTime);
    }

    @CliCommand(value = "applyRetentionPolicy", help = "clean application data from start of time to specified endTime minus configured time  ")
    public void applyRetentionPolicy(
            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed") final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed") final Instant endTime,
            //TODO: Remove the COMMAND_LINE_FIXED_DURATION_FIELD_NAME  when fixing the JarOpertaor (Currently this is mandatory in the JarOperator)
            @CliOption(key = {CommonStrings.COMMAND_LINE_FIXED_DURATION_FIELD_NAME}, help = "the internal time intervals that the processing will be done by") final Double fixedDuration
    ) throws Exception {
        executionService.applyRetentionPolicy(startTime);
    }

    @CliCommand(value = "cleanAll", help = "clean application data for specified data source")
    public void cleanAll() throws Exception {
        executionService.cleanAll();
    }
}
