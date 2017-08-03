package presidio.ade.processes.shell;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
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
public class AccumulateServiceCommands implements CommandMarker {
	@Autowired
	private AccumulateAggregationsExecutionService accumulateAggregationsExecutionService;

	@CliCommand(value = "run", help = "run events with specified time range and data source")
	public void run(
			@CliOption(key = {CommonStrings.COMMAND_LINE_SCHEMA_FIELD_NAME}, mandatory = true, help = "events schema")
			final Schema schema,

			@CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
			final Instant startTime,

			@CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
			final Instant endTime,

			@CliOption(key = {CommonStrings.COMMAND_LINE_FIXED_DURATION_FIELD_NAME}, help = "the internal time intervals that the processing will be done by")
			final Double fixedDuration,

			@CliOption(key = {CommonStrings.COMMAND_LINE_FEATURE_BUCKET_STRATEGY_FIELD_NAME}, help = "the internal time intervals that the processing will be done by")
			final Double featureBucketStrategy

	) throws Exception {
		accumulateAggregationsExecutionService.run(schema, startTime, endTime, fixedDuration, featureBucketStrategy);
	}

	@CliCommand(value = "clean", help = "clean application data for specified time range and data source")
	public void clean(
			@CliOption(key = {CommonStrings.COMMAND_LINE_SCHEMA_FIELD_NAME}, mandatory = true, help = "events schema")
			final Schema schema,

			@CliOption(key = {CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time greater than specified start time will be processed")
			final Instant startTime,

			@CliOption(key = {CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME}, mandatory = true, help = "events with (logical) time smaller than specified end time will be processed")
			final Instant endTime

	) throws Exception {
		accumulateAggregationsExecutionService.clean(schema, startTime, endTime);
	}
}
