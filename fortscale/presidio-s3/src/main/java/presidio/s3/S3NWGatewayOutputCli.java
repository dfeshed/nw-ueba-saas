package presidio.s3;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import presidio.s3.services.NWGatewayOutput;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.HOURS;

@Component
public class S3NWGatewayOutputCli implements CommandMarker {
    @Autowired
    private NWGatewayOutput nwGatewayOutput;

    @CliCommand(value = "waitTillHourIsReady", help = "check if hour is ready for reading")
    public void waitTillHourIsReady(
            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "check if all events with (logical) time smaller than specified end time is ready") final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_SCHEMA_FIELD_NAME}, mandatory = true, help = "events schemas") final Schema schema,

            @CliOption(key = {CommonStrings.COMMAND_LINE_TIME_TO_SLEEP}, mandatory = true, help = "time to sleep in seconds between each iteration") final int timeToSleepInSeconds,

            @CliOption(key = {CommonStrings.COMMAND_LINE_TIMEOUT}, mandatory = true, help = "timeout in seconds for execution") final int timeout

    ) throws Exception {
        if (!endTime.truncatedTo(HOURS).equals(endTime))
            throw new IllegalArgumentException("the end time must be hour on the hour");
        nwGatewayOutput.waitTillHourIsReady(endTime, schema.toString(), timeToSleepInSeconds, timeout);
    }

}
