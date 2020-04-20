package presidio.ade.sdk.cleanup;

import presidio.ade.domain.store.AdeDataStoreCleanupParams;

/**
 * Provides the ADE's consumers with APIs related to Cleanup.
 *
 * @author Lior Govrin
 */
public interface CleanupManagerSdk {
	/**
	 * Clean the data that corresponds to the given parameters.
	 *
	 * @param params the cleanup parameters
	 */
	void cleanup(AdeDataStoreCleanupParams params);
}
