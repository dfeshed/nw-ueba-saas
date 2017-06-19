package fortscale.common.shell.command;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.general.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
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

    //todo fixed_duration??

    @CliAvailabilityIndicator({"process"})
    public boolean isCommandAvailable() {
        return true;
    }

    public PresidioCommands() {
        System.out.println();
    }

    @CliCommand(value = "process", help = "process events with specified time range and data source")
    public void process(
            @CliOption(key = {"data_source"}, mandatory = true, help = "data source") final DataSource dataSource,

            @CliOption(key = {"start_date"}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed") final Instant startTime,

            @CliOption(key = {"end_date"}, mandatory = false, help = "events with (logical) time smaller than specified end time will be processed") final Instant endTime

    ) throws Exception {
            executionService.process(fortscale.common.general.DataSource.DLPFILE, startTime, endTime);
    }

    public enum DataSource {

        DLPFILE("dlpfile"), DLPMAIL("dlpmail"), PRNLOG("prnlog");

        private String name;

        DataSource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static fortscale.common.general.DataSource createDataSource(String dataSourceName) throws Exception {
            return fortscale.common.general.DataSource.valueOf(dataSourceName.toUpperCase());
        }


    }
}
