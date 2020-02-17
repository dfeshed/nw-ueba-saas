package presidio.s3;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import presidio.s3.services.NWGatewayService;

import java.time.Instant;

@Component
public class S3Shellcommands implements CommandMarker {
    @Autowired
    private NWGatewayService nwGatewayService;

    @CliCommand(value = "hourIsReady", help = "check if hour is ready for reading")
    public void hourIsReady(
            @CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time bigger than specified start time will be processed") final Instant startTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed") final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_SCHEMA_FIELD_NAME}, help = "events schemas") final Schema schema,

            @CliOption(key = {CommonStrings.COMMAND_LINE_FIXED_DURATION_FIELD_NAME}, help = "the internal time intervals that the processing will be done by") final Double fixedDuration
    ) throws Exception {
        nwGatewayService.hourIsReady(startTime, endTime, schema.toString());
    }

}
