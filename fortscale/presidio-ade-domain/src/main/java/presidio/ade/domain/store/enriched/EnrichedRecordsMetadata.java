package presidio.ade.domain.store.enriched;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Instant;

/**
 * metadata for enriched records, like what is the data source and what is the time range
 *
 * Created by barak_schuster on 5/18/17.
 */
public class EnrichedRecordsMetadata {
	private final String dataSource;
	private final Instant startInstant;
	private final Instant endInstant;

	public EnrichedRecordsMetadata(String dataSource, Instant startInstant, Instant endInstant) {
		this.dataSource = dataSource;
		this.startInstant = startInstant;
		this.endInstant = endInstant;
	}

	/**
	 * @return ToString you know...
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getDataSource() {
		return dataSource;
	}

	public Instant getStartInstant() {
		return startInstant;
	}

	public Instant getEndInstant() {
		return endInstant;
	}
}
