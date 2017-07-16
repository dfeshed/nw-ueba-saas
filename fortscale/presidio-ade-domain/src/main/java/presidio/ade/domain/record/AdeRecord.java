package presidio.ade.domain.record;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

/**
 * A basic ADE record. All ADE related records (enriched and scored) should extend this one.
 *
 * Created by Lior Govrin on 06/06/2017.
 */
@Document
public abstract class AdeRecord {
	public static final String START_INSTANT_FIELD = "startInstant";

	@Id
	private String id;
	@CreatedDate
	private Instant createdDate;
	@Indexed @Field(START_INSTANT_FIELD)
	private Instant startInstant;

	public AdeRecord(Instant startInstant) {
		this.startInstant = startInstant;
	}

	/**
	 * @return a string representation of the event type
	 */
	public abstract String getAdeEventType();

	public String getId() {
		return id;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public Instant getStartInstant() {
		return startInstant;
	}

	public void setStartInstant(Instant startInstant) {
		this.startInstant = startInstant;
	}

	public abstract List<String> getDataSources();
}
