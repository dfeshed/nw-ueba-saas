package presidio.ade.manager;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;

import java.time.Instant;

/**
 * The supported CLI commands for the {@link }.
 *
 * @author Lior Govrin
 */
public class ManagerApplicationCommands implements CommandMarker {
	@Autowired
	private ManagerApplicationService managerApplicationService;

	@CliCommand(value = "cleanup", help = "cleanup enriched collections")
	public void run(
			@CliOption(key = {CommonStrings.COMMAND_LINE_UNTIL_DATE_FIELD_NAME}, mandatory = true, help = "until instant")
			final Instant until
	) throws Exception {
		managerApplicationService.cleanupEnrichedCollections(until);
	}


}
