package fortscale.streaming.exceptions;

import fortscale.utils.logging.Logger;

/**
 * The {@link EnhancedExceptionHandler} keeps track of exceptions thrown during the execution of a certain operation,
 * and helps the user to decide whether to ignore the exception and continue, sleep for a while and then re-execute the
 * action, or simply re-throw the exception back to the user.
 *
 * @author Lior Govrin
 */
public class EnhancedExceptionHandler {
	private static final String NAME = EnhancedExceptionHandler.class.getSimpleName();
	private static final Logger logger = Logger.getLogger(EnhancedExceptionHandler.class);

	private boolean enabled;
	private int numOfExceptionsToIgnore;
	private long sleepMillisBeforeRetry;
	private int numOfAllowedExceptions;
	private int numOfExceptionsCaught;

	/**
	 * C'tor.
	 *
	 * @param enabled                 indicates whether the handler is turned on or off
	 * @param numOfExceptionsToIgnore the first {@code numOfExceptionsToIgnore} exceptions are ignored without a retry
	 * @param sleepMillisBeforeRetry  number of milliseconds to sleep before trying to re-execute the action
	 * @param numOfAllowedExceptions  any exception after the first {@code numOfAllowedExceptions} exceptions is thrown
	 *                                back to the user (the exception is not ignored and the action is not re-executed)
	 */
	public EnhancedExceptionHandler(
			boolean enabled, int numOfExceptionsToIgnore, long sleepMillisBeforeRetry, int numOfAllowedExceptions) {

		this.enabled = enabled;
		this.numOfExceptionsToIgnore = numOfExceptionsToIgnore;
		this.sleepMillisBeforeRetry = sleepMillisBeforeRetry;
		this.numOfAllowedExceptions = numOfAllowedExceptions;
		reset();
	}

	/**
	 * Handle an exception caught and decide whether to re-throw it,
	 * sleep for a while and then order a retry, or simply ignore it.
	 *
	 * @param eCaught the exception that was caught
	 * @return true if the user should re-execute the action, false otherwise
	 * @throws Exception the exception caught if the handler decided to re-throw it
	 */
	public boolean handleException(Exception eCaught) throws Exception {
		numOfExceptionsCaught++;
		logger.warn("{} caught an exception of type {}.", NAME, eCaught.getClass().getSimpleName());
		logger.warn("No. of exceptions to ignore {}, no. of allowed exceptions {}, no. of exceptions caught {}.",
				numOfExceptionsToIgnore, numOfAllowedExceptions, numOfExceptionsCaught);

		// If the handler is disabled, throw back the exception
		if (!enabled) {
			logger.error("{} is disabled - throwing back the exception.", NAME);
			throw eCaught;
		}

		// If the number of exceptions caught exceeds the number of allowed exceptions, throw back the exception
		if (numOfExceptionsCaught > numOfAllowedExceptions) {
			logger.error("No. of exceptions caught exceeds no. of allowed exceptions - throwing back the exception.");
			throw eCaught;
		}

		if (numOfExceptionsCaught > numOfExceptionsToIgnore) {
			// If the number of exceptions caught exceeds the number of exceptions to ignore, start retries
			logger.warn("{} is going to sleep for {} milliseconds before retry.", NAME, sleepMillisBeforeRetry);

			try {
				Thread.sleep(sleepMillisBeforeRetry);
			} catch (InterruptedException eSleep) {
				logger.error("Exception thrown while sleeping - continuing to retry: {}.", eSleep.getMessage());
			}

			// Retry to execute the action that led to the exception
			return true;
		} else {
			// Ignore the first {@code numOfExceptionsToIgnore} exceptions and do not retry to execute the action
			logger.warn("Ignoring the exception.");
			return false;
		}
	}

	/**
	 * Reset this handler's exceptions counter.
	 * Should be called by the user after a successful execution (without a retry).
	 */
	public void reset() {
		numOfExceptionsCaught = 0;
	}
}
