package presidio.ade.sdk.online_run;

import presidio.ade.sdk.common.PrepareRunResult;
import presidio.ade.sdk.common.RunId;
import presidio.ade.sdk.common.RunStatus;

import java.util.Set;

/**
 * Provides the ADE's consumers with APIs related to the Online Run.
 *
 * @author Barak Schuster
 * @author Lior Govrin
 */
public interface OnlineRunManagerSdk {
	/**
	 * Execute the needed preparation steps before starting to process data.
	 *
	 * @param params that define the online run (for example, the data is from some
	 *               start instant, and it should be processed in chunks of 1 hour)
	 * @return the result of the "prepare run" request
	 */
	PrepareRunResult prepareOnlineRun(OnlineRunParams params);

	/**
	 * @return the ID of the online run
	 */
	RunId getOnlineRunId();

	/**
	 * Change the execution params of the online run.
	 * Usage example: The consumer wants to change the scheduling of the
	 *                execution to run once an hour instead of once a day.
	 *
	 * @param params new execution params
	 */
	void changeOnlineRunParams(OnlineRunParams params);

	/**
	 * Notify the ADE component that the data needed to process the next time range is ready.
	 */
	void processNextOnlineTimeRange();

	/**
	 * @return the latest time range of the online run processed by the ADE
	 *         (triggered by {@link #processNextOnlineTimeRange()})
	 */
	OnlineRunParams getLatestOnlineTimeRangeProcessed();

	/**
	 * @return the time ranges of the online run currently processed by the ADE
	 *         (triggered by {@link #processNextOnlineTimeRange()})
	 */
	Set<OnlineRunParams> getOnlineTimeRangesInProgress();

	/**
	 * @return the status of the online run
	 */
	RunStatus getOnlineRunStatus();

	/**
	 * Pause the online execution. The ADE will pause the online run gracefully as fast
	 * as possible, by finishing the current sub-step and not continuing to the next one.
	 */
	void pauseOnlineRun();

	/**
	 * Resume the online execution. The ADE will continue to the next sub-step.
	 */
	void resumeOnlineRun();

	/**
	 * Stop the online execution once the ADE is done processing its current time range.
	 */
	void stopOnlineRun();

	/**
	 * Interrupt and stop the online execution, without waiting for the current time range to be done (i.e. SIGKILL).
	 * NOTICE: You will probably leave the system in a dirty (not fully processed) data state. The dirty data should be
	 *         cleaned, and the execution cursor should be moved to the beginning of the latest time range.
	 */
	void stopOnlineRunForcefully();
}
