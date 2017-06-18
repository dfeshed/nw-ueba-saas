package fortscale.utils.shell.commands.common;

import fortscale.utils.shell.service.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

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

    //todo fixed_duration??

    @CliAvailabilityIndicator({"process"})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = "process", help = "process events with specified time range and data source")
    public void process(
            @CliOption(key = {"data_source"}, mandatory = true, help = "data source") final String dataSource,

            @CliOption(key = {"start_date"}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed") final String startTime,

            @CliOption(key = {"end_date"}, mandatory = false, help = "events with (logical) time smaller than specified end time will be processed") final String endTime

    ) throws Exception {
            executionService.process(dataSource, startTime, endTime);
    }
}
