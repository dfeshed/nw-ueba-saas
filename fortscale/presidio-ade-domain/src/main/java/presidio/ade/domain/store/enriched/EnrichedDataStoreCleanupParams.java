package presidio.ade.domain.store.enriched;

import java.time.Instant;

/**
 * Enriched data cleanup in the store will be preformed by these filtering params.
 *
 * Created by barak_schuster on 5/21/17.
 */
public class EnrichedDataStoreCleanupParams {
	private final Instant startDate;
	private final Instant endDate;
	private final String dataSource;

	public EnrichedDataStoreCleanupParams(Instant startDate, Instant endDate, String dataSource) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.dataSource = dataSource;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public String getDataSource() {
		return dataSource;
	}
}
