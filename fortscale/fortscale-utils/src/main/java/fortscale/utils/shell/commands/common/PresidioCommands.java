package fortscale.utils.shell.commands.common;

import fortscale.utils.shell.service.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * Created by efratn on 12/06/2017.
 * Defines all the supported Presidio commands
 */
@Component
public class PresidioCommands implements CommandMarker {

    //TODO change this to some generic intercae to match output\ADE\input ...
    @Autowired
    private PresidioExecutionService executionService;

    //todo fixed_duration??

    public PresidioCommands() {
        System.out.println("TEST");
    }

    @CliAvailabilityIndicator({"process"})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = "process", help = "process events with specified time range and data source")
    public void process(
            @CliOption(key = {"data_source"}, mandatory = true, help = "data source") final String dataSource,

            @CliOption(key = {"start_date"}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed") final String startTime,

            @CliOption(key = {"end_date"}, mandatory = false, help = "events with (logical) time smaller than specified end time will be processed") final String endTime
    ) {
        try {
            System.out.println("starting the process");//todo remove this line
            executionService.process(dataSource, startTime, endTime);
        } catch (Exception e) {
            //todo???
        }
    }
}
