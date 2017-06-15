package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;

/**
 * A basic ADE enriched record. All ADE enriched records (across all data sources) should extend this one.
 *
 * Created by Lior Govrin on 06/06/2017.
 */
@Document
public abstract class EnrichedRecord extends AdeRecord {
	public EnrichedRecord(Instant date_time) {
		super(date_time);
	}

	@Transient
	public abstract String getDataSource();
}
