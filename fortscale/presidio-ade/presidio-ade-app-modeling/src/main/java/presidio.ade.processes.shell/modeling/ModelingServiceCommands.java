package presidio.ade.processes.shell.modeling;

import fortscale.common.general.CommonStrings;
import fortscale.ml.model.ModelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;

import java.time.Instant;

/**
 * The supported CLI commands for the {@link ModelingService}.
 *
 * @author Lior Govrin
 */
public class ModelingServiceCommands implements CommandMarker {
	@Autowired
	private ModelingService modelingService;

	@CliCommand(value = "process", help = "Create models for the given group, with the given session ID and end instant.")
	public void process(
			@CliOption(
					key = CommonStrings.COMMAND_LINE_GROUP_NAME_FIELD_NAME,
					mandatory = true,
					help = "The name of the group of model configurations."
			) final String groupName,

			@CliOption(
					key = CommonStrings.COMMAND_LINE_SESSION_ID_FIELD_NAME,
					mandatory = true,
					help = "The session ID of the created models."
			) final String sessionId,

			@CliOption(
					key = CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME,
					mandatory = true,
					help = "The end date of the created models."
			) final Instant endDate
	) throws Exception {
		modelingService.process(groupName, sessionId, endDate);
	}

	@CliCommand(value = "clean", help = "Clean models of the given group (optional), with the given session ID (optional).")
	public void clean(
			@CliOption(
					key = CommonStrings.COMMAND_LINE_GROUP_NAME_FIELD_NAME,
					help = "The name of the group of model configurations."
			) final String groupName,

			@CliOption(
					key = CommonStrings.COMMAND_LINE_SESSION_ID_FIELD_NAME,
					help = "The session ID of the models to clean."
			) final String sessionId
	) throws Exception {
		modelingService.clean(groupName, sessionId);
	}
}
