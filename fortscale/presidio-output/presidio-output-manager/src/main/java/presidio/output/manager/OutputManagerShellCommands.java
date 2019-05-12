package presidio.output.manager;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OutputManagerShellCommands {
    @Autowired
    private OutputManagerService outputManagerService;

    @CliCommand(value = "clean-documents", help = "clean output documents for specified time range ")
    public void cleanDocuments(
            @CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed") final Instant endTime,

            @CliOption(key = {CommonStrings.COMMAND_LINE_SCHEMA_FIELD_NAME}, mandatory = true, help = "events schema") final Schema schema

            ) throws Exception {
        outputManagerService.cleanDocuments(endTime, schema);
    }
}
