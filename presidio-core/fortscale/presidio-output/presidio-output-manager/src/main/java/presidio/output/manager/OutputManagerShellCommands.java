package presidio.output.manager;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import presidio.output.manager.services.OutputManagerService;

import java.time.Instant;
import java.util.List;

@Component
public class OutputManagerShellCommands implements CommandMarker {
    @Autowired
    private OutputManagerService outputManagerService;

    @CliCommand(value = "applyRetentionPolicy", help = "clean output documents up to specified date")
    public void cleanDocuments(
            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed") final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_SCHEMA_FIELD_NAME}, help = "events schemas") final Schema[] schemas
            ) throws Exception {
        outputManagerService.cleanDocuments(endTime, schemas);
    }
}
