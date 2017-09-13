package presidio.ade.manager;

import fortscale.common.general.CommonStrings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;

import java.time.Instant;

/**
 * The supported CLI commands for the {@link AdeManagerApplicationService}.
 */
public class AdeManagerApplicationCommands implements CommandMarker {
	@Autowired
	private AdeManagerApplicationService adeManagerApplicationService;

	@CliCommand(value = "enriched_ttl_cleanup", help = "ttl cleanup of enriched data")
	public void enriched_ttl_cleanup(
			@CliOption(key = {CommonStrings.COMMAND_LINE_UNTIL_DATE_FIELD_NAME}, mandatory = true, help = "until instant")
			final Instant until
	) throws Exception {
		adeManagerApplicationService.cleanupEnrichedData(until);
	}


}
