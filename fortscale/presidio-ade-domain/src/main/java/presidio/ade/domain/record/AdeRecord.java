package presidio.ade.domain.record;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * A basic ADE record. All ADE related records (enriched and scored) should extend this one.
 *
 * Created by Lior Govrin on 06/06/2017.
 */
@Document
public abstract class AdeRecord {
	public static final String DATE_TIME_FIELD = "date_time";

	@Id
	private String id;
	@CreatedDate
	private Instant created_date;
	@Indexed @Field(DATE_TIME_FIELD)
	private Instant date_time;

	public AdeRecord(Instant date_time) {
		this.date_time = date_time;
	}

	/**
	 * @return a string representation of this record's type
	 */
	public String getAdeRecordType() {
		return getClass().getSimpleName();
	}

	@Transient
	public abstract String getDataSource();

	public String getId() {
		return id;
	}

	public Instant getCreated_date() {
		return created_date;
	}

	public Instant getDate_time() {
		return date_time;
	}

	public void setDate_time(Instant date_time) {
		this.date_time = date_time;
	}
}
