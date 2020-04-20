package presidio.ade.domain.store.enriched;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Instant;

/**
 * metadata for enriched records, like what is the data source and what is the time range
 *
 * Created by barak_schuster on 5/18/17.
 */
public class EnrichedRecordsMetadata {
	private final String adeEventType;
	private final Instant startInstant;
	private final Instant endInstant;

	public EnrichedRecordsMetadata(String adeEventType, Instant startInstant, Instant endInstant) {
		this.adeEventType = adeEventType;
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

	public String getAdeEventType() {
		return adeEventType;
	}

	public Instant getStartInstant() {
		return startInstant;
	}

	public Instant getEndInstant() {
		return endInstant;
	}
}
