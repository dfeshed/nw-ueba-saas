package fortscale.collection.morphlines.commands;

import static com.google.common.base.Preconditions.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;

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
public class EventsJoinerCache implements Closeable {

	// /////////////////////////////////////////////////////////////////////////////
	// Static accessors: 
	// /////////////////////////////////////////////////////////////////////////////
	
	private static Map<Integer, EventsJoinerCache> instances = new HashMap<Integer, EventsJoinerCache>();
	
	public static EventsJoinerCache getInstance(Command command) {
		checkNotNull(command);
		
		// get the root command's hash code and use it to construct 
		// a shared EventsJoiner instance for each morphline instance
		while (command.getParent()!=null)
			command = command.getParent();
		int hashCode = command.hashCode();
		
		// loopup the hash code in the instances map
		if (!instances.containsKey(hashCode)) {
			EventsJoinerCache instance = new EventsJoinerCache(hashCode);
			instances.put(hashCode, instance);
		}
		return instances.get(hashCode);
	}
	
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
	
	private static void removeInstance(int instanceId) {
		if (instances.containsKey(instanceId))
			instances.remove(instanceId);
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// Instance implementation:
	// /////////////////////////////////////////////////////////////////////////////
	
	private boolean isClosed;
	private int instanceId;
	private Map<String,Record> records;
	
	private EventsJoinerCache(int instanceId) {
		this.instanceId = instanceId;
		this.isClosed = false;
		this.records = new HashMap<String, Record>();
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
		
		// TODO: do we need to check for records of the same time sequence? (after the timestamp of the stored record)?
		return records.remove(key);
	}
	
	
	@Override
	public void close() throws IOException {
		// mark the instance as closed, clear the records map
		// and remove it from the static instances map
		if (!isClosed) {
			isClosed = true;
			this.records.clear();
			this.records = new HashMap<String,Record>();
			removeInstance(instanceId);
		}
	}

}
