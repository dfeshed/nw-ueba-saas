package fortscale.collection.morphlines.commands;

import static com.google.common.base.Preconditions.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.domain.eventscache.CachedRecord;
import fortscale.domain.eventscache.CachedRecordRepository;

/**
 * Cache for stored record that are used during processing of
 * events in morphline that need to hold them for later use (merge 
 * selected fields into a subsequent morphline record).
 * It is assumed that each morphline configuration file instance 
 * holds a seperate instance of the EventsJoinerCache in memory 
 * and it will be shared by all commands in the same morphline 
 * instance, that use the EventsJoinerCache. 
 *
 */
//@Configurable(preConstruction=true, autowire=Autowire.BY_TYPE)
@Configurable(preConstruction=true)
public class EventsJoinerCache implements Closeable {

	// /////////////////////////////////////////////////////////////////////////////
	// Static accessors: 
	// /////////////////////////////////////////////////////////////////////////////
	
	private static Logger logger = LoggerFactory.getLogger(EventsJoinerCache.class);
	private static Map<String, EventsJoinerCache> instances = new ConcurrentHashMap<String, EventsJoinerCache>();
	
	/**
	 * Get an instance of the EventsJoinerCache that is separate for each 
	 * morphline execution instance
	 */
	public static EventsJoinerCache getInstance(String name) {
		checkNotNull(name);
		
		
		// lookup the hash code in the instances map
		if (!instances.containsKey(name)) {
			EventsJoinerCache instance = new EventsJoinerCache(name);
			instances.put(name, instance);
		}
		return instances.get(name);
	}
	
	/**
	 * Generates a cache key to be used in the EventsJoinerCache according
	 * to the fields values in the given record.
	 */
	public static String buildKey(Record inputRecord, List<String> fields) {
		StringBuilder sb = new StringBuilder();
		for (String key : fields) {
			Object value = inputRecord.getFirstValue(key);
			if (value!=null) {
				sb.append(value.toString());
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * Used by the HashJoinerCache instances to remove instances 
	 * from the global static map once closed
	 */
	private static void removeInstance(String instanceId) {
		if (instances.containsKey(instanceId))
			instances.remove(instanceId);
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// Instance implementation:
	// /////////////////////////////////////////////////////////////////////////////
	
	private boolean isClosed;
	private String instanceId;
	private Map<String,Record> records;
	
	@Autowired
	private CachedRecordRepository repository;
	
	protected EventsJoinerCache(String instanceId) {
		this.instanceId = instanceId;
		this.isClosed = false;
		this.records = new HashMap<String, Record>();
		load();
	}
	
	public void store(String key, Record record) {
		checkNotNull(key);
		checkNotNull(record);
		if (isClosed)
			throw new IllegalStateException("EventsJoinerCache is closed");
		
		records.put(key, record);
	}
	
	public Record fetch(String key) {
		checkNotNull(key);
		if (isClosed)
			throw new IllegalStateException("EventsJoinerCache is closed");
		
		return records.remove(key);
	}
	
	
	@Override
	public void close() throws IOException {
		// mark the instance as closed, clear the records map
		// and remove it from the static instances map
		if (!isClosed) {
			isClosed = true;
			persist();
			removeInstance(instanceId);
		}
	}
	
	/**
	 * Persist stored records into mongodb to allow fail over and restart between 
	 * jobs executions. The method should be called by the close method in the cache.
	 */
	private void persist() {
	
		if (repository!=null) {
			// store all cache records into mongo to a dedicated collection per cache
			// this is to reduce contention on the mongo collection when performing
			// insert or query and delete
			List<CachedRecord> batchToInsert = new LinkedList<CachedRecord>();
			for (String key : records.keySet()) {
				batchToInsert.add(new CachedRecord(instanceId, key, records.get(key)));
			}
			if (!batchToInsert.isEmpty())
				repository.save(batchToInsert);
		} else {
			// should be null when running in unit test context
			logger.error("mongo repository not injected");
		}
			
		
		this.records.clear();
		this.records = new HashMap<String,Record>();
	}
	
	/**
	 * Loads previously stored records from mongodb upon cache creation. The load method 
	 * will load all records that belong to this type of the morphline cache events and 
	 * will delete them from mongodb after the load. 
	 */
	private void load() {
		
		if (repository!=null) {
			// load all records from mongodb
			List<CachedRecord> loadedRecords = repository.findByCacheName(instanceId);
			for (CachedRecord record : loadedRecords)
				this.records.put(record.getKey(), record.getRecord());

			// delete all loaded records from mongodb
			if (!loadedRecords.isEmpty())
				repository.deleteByCacheName(instanceId);
		} else {
			// should be null when running in unit test context
			logger.error("mongoTemplate not initialized");
			return;
		}
		
		
	}

}
