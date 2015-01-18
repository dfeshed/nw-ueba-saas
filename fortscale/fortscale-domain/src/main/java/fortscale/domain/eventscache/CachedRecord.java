package fortscale.domain.eventscache;

import org.kitesdk.morphline.api.Record;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.google.common.base.Preconditions.checkNotNull;

@Document(collection="cachedEventsRecord")
@TypeAlias(value="CachedRecord")
public class CachedRecord {

	@Id
	private String id;
	@Indexed(unique=false, dropDups=false)
	private String cacheName;
	private String key;
	private Record record;
	
	public CachedRecord(String cacheName, String key, Record record) {
		checkNotNull(cacheName);
		checkNotNull(key);
		checkNotNull(record);
		this.key = key;
		this.cacheName = cacheName;
		this.record = record;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	public Record getRecord() {
		return record;
	}
	public void setRecord(Record record) {
		this.record = record;
	}
	
}
