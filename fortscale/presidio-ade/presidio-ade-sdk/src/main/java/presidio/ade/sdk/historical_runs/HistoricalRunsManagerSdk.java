package presidio.ade.sdk.historical_runs;

import presidio.ade.sdk.common.PrepareRunResult;
import presidio.ade.sdk.common.RunId;
import presidio.ade.sdk.common.RunStatus;

import java.util.Set;

/**
 * Provides the ADE's consumers with APIs related to Historical Runs.
 *
 * @author Barak Schuster
 * @author Lior Govrin
 */
public interface HistoricalRunsManagerSdk {
	/**
	 * Execute the needed preparation steps before starting to process data.
	 *
	 * @param runId  the execution identifier
	 * @param params that define the historical run (for example, the data is between some start
	 *               instant and some end instant, and it should be processed in chunks of 1 hour)
	 * @return the result of the "prepare run" request
	 */
	PrepareRunResult prepareHistoricalRun(RunId runId, HistoricalRunParams params);

	/**
	 * Change the execution params of a certain historical run.
	 * Usage example: The consumer wants to change the scheduling of an
	 *                execution to run once an hour instead of once a day.
	 *
	 * @param runId  execution identifier
	 * @param params new execution params
	 */
	void changeHistoricalRunParams(RunId runId, HistoricalRunParams params);

	/**
	 * Notify the ADE component that the data needed to process the next time range is ready.
	 *
	 * @param runId execution identifier
	 */
	void processNextHistoricalTimeRange(RunId runId);

	/**
	 * @param runId execution identifier
	 * @return the latest time range of the corresponding historical run processed by the ADE
	 *         (triggered by {@link #processNextHistoricalTimeRange(RunId)})
	 */
	HistoricalRunParams getLatestHistoricalTimeRangeProcessed(RunId runId);

	/**
	 * @param runId execution identifier
	 * @return the time ranges of the corresponding historical run currently processed by the ADE
	 *         (triggered by {@link #processNextHistoricalTimeRange(RunId)})
	 */
	Set<HistoricalRunParams> getHistoricalTimeRangesInProgress(RunId runId);

	/**
	 * @param runId execution identifier
	 * @return the status of the historical run
	 */
	RunStatus getHistoricalRunStatus(RunId runId);

	/**
	 * Pause the relevant execution. The ADE will pause the historical run gracefully as fast
	 * as possible, by finishing the current sub-step and not continuing to the next one.
	 *
	 * @param runId the execution to be paused
	 */
	void pauseHistoricalRun(RunId runId);

	/**
	 * Resume the relevant execution. The ADE will continue to the next sub-step.
	 *
	 * @param runId the execution to be resumed
	 */
	void resumeHistoricalRun(RunId runId);

	/**
	 * Stop the relevant execution once the ADE is done processing its current time range.
	 *
	 * @param runId the execution to be stopped
	 */
	void stopHistoricalRun(RunId runId);

	/**
	 * Interrupt and stop the relevant execution, without waiting for the current time range to be done (i.e. SIGKILL).
	 * NOTICE: You will probably leave the system in a dirty (not fully processed) data state. The dirty data should be
	 *         cleaned, and the execution cursor should be moved to the beginning of the latest time range.
	 *
	 * @param runId the execution to be forcefully stopped
	 */
	void stopHistoricalRunForcefully(RunId runId);
}
