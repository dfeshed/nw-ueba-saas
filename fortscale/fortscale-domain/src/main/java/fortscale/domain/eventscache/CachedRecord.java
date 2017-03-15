package fortscale.domain.eventscache;

import fortscale.domain.core.AbstractDocument;
import org.joda.time.DateTime;
import org.kitesdk.morphline.api.Record;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.domain.core.AbstractAuditableDocument.CREATED_AT_FIELD_NAME;

@Document(collection="cachedEventsRecord")
@TypeAlias(value="CachedRecord")
public class CachedRecord extends AbstractDocument {

	private static final long serialVersionUID = -2544779887345246880L;


	@Indexed(unique=false, dropDups=false)
	private String cacheName;
	private String key;
	private Record record;

	@Indexed(unique = false, expireAfterSeconds=60*60*730) //1 month
	@CreatedDate
	@Field(CREATED_AT_FIELD_NAME)
	private DateTime createdAt;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		checkNotNull(key);
		this.key = key;
	}
	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		checkNotNull(cacheName);
		this.cacheName = cacheName;
	}
	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		checkNotNull(record);
		this.record = record;
	}
	
}
