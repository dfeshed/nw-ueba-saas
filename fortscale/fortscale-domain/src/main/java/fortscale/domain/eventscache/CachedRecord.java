package fortscale.domain.eventscache;

import fortscale.domain.core.AbstractDocument;
import org.kitesdk.morphline.api.Record;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.google.common.base.Preconditions.checkNotNull;

@Document(collection="cachedEventsRecord")
@TypeAlias(value="CachedRecord")
public class CachedRecord extends AbstractDocument {

	private static final long serialVersionUID = -2544779887345246880L;


	@Indexed(unique=false, dropDups=false)
	private String cacheName;
	private String key;
	private Record record;


	
/*	public CachedRecord(String cacheName, String key, Record record) {

		this.key = key;
		this.cacheName = cacheName;
		this.record = record;

	}*/

	
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
