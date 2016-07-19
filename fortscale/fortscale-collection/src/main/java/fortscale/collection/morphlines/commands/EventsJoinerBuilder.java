package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.MorphlineConfigService;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static fortscale.collection.morphlines.RecordExtensions.getLongValue;
import static fortscale.utils.time.TimestampUtils.convertToSeconds;


/**
 * EventsJoiner command is used as a command that can do both join and merge for records
 * based on a key to match the records during morphline ETL process and output a combined 
 * record using fields extracted from both records.
 */
@Configurable(preConstruction=true)
public class EventsJoinerBuilder implements CommandBuilder {
	private static Logger logger = LoggerFactory.getLogger(EventsJoinerBuilder.class);

	@Autowired
	private MorphlineConfigService morphlineConfigService;

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("EventsJoiner");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new EventsJoiner(this, config, parent, child, context);
	}



	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	public final class EventsJoiner extends AbstractCommand {

		
		private List<String> keys;
		private List<String> mergeFields;
		private String currentRecordDateField;
		private String cachedRecordDateField;
		private long timeThreshold;
		private boolean dropFromCache;
		private EventsJoinerCache cache;
        private boolean processRecord;
		private Long cacheRecordTtl;
		private long minimalRecordTs = Long.MAX_VALUE;


		private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();


		public EventsJoiner(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			keys = getConfigs().getStringList(config, "keys");
			currentRecordDateField = getConfigs().getString(config, "currentRecordDateField");
			cachedRecordDateField = getConfigs().getString(config, "cachedRecordDateField");
			timeThreshold = getConfigs().getLong(config, "timeThreshold");
			cacheRecordTtl = -1l;
			try {
				cacheRecordTtl = new Long(morphlineConfigService.getStringValue(getConfigs(), config, "cacheRecordTtlPropertyName"));
			}
			catch (Exception e){
				logger.warn("Error getting \"cacheRecordTtlPropertyName\" property from configuration, setting value to -1");
			}
			mergeFields = getConfigs().getStringList(config, "mergeFields");
			dropFromCache = getConfigs().getBoolean(config, "dropFromCache");
            processRecord =  getConfigs().getBoolean(config, "processRecord",false);
			String cacheName = getConfigs().getString(config, "cacheName");
			cache = EventsJoinerCache.getInstance(cacheName, currentRecordDateField);
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			// get the key fields from the record and look for the record to merge from
			String key = EventsJoinerCache.buildKey(inputRecord, keys);
			Record previousEvent = (dropFromCache)? cache.fetch(key) : cache.peek(key);

			long currentTime = convertToSeconds(getLongValue(inputRecord, currentRecordDateField, 0L));
			long cachedTime = convertToSeconds(getLongValue(previousEvent, cachedRecordDateField, 0L));

			// this ttl is different from the timeThreshold.
			// timeThreshold is used for define the longest join time period possible (in the case a join is possible).
			// cacheRecordTtl is relevant for cases when there was not record to join to an old saved record in the cache.
			// in this case only for the first processed record (in each ETL run) set the cache instance with the relevant time from which will deprecate old cache records.
			// in the case no ttl is desire (save cached records forever) define cacheRecordTtl as -1
			if (cacheRecordTtl != -1  && minimalRecordTs > currentTime) {
				minimalRecordTs = currentTime;
				cache.setDeprecationTs(minimalRecordTs-cacheRecordTtl);
			}

			if (previousEvent==null) {
				// store the record for later merge
				cache.store(key, inputRecord);
				
				// mark command as successful, do not pass the record
				// to chained child command to halt execution

                if(processRecord) {
					return super.doProcess(inputRecord);
				}
				//Drop record
				commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.SAVED_TO_CACHE);
				if (morphlineMetrics != null) {
					morphlineMetrics.eventSavedToCache++;
				}
				return true;
			} else {
				// check if the time delta between the events is within the 
				// tolerated threshold
				long delta = (currentTime < cachedTime)? cachedTime - currentTime : currentTime - cachedTime;
				
				if (delta > timeThreshold) {
					// replace the cached record as we encountered a new one that should be saved
					// if the delta is greater than the threshold we assume that the new events belongs
					// to a new session
					cache.store(key, inputRecord);

                    if(processRecord) {
						return super.doProcess(inputRecord);
					}
					if (morphlineMetrics != null) {
						morphlineMetrics.deltaGreaterThenThreshold++;
					}
					commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.DELTA_GREATER_THEN_THRESHOLD);
					return true;
				} else {
					// get the fields to merge from the previous record and put them 
					// into the current record
					for (String field : mergeFields) {
						@SuppressWarnings("rawtypes")
						List values = previousEvent.get(field);
						// add all values to the input record (only values that already exist at the previous record )
                        if (values.size() > 0) {
                            inputRecord.removeAll(field);
                            for (Object value : values) {
                                inputRecord.put(field, value);
                            }
                        }
					}
	
					// continue processing in the command chain
					return super.doProcess(inputRecord);
				}
			}
		}
		
		@Override
		protected void doNotify(Record notification) {
			for (Object event : Notifications.getLifecycleEvents(notification)) {
				if (event == Notifications.LifecycleEvent.SHUTDOWN && cache!=null) {
					try {
						cache.close();
					} catch (IOException e) {
						LOG.error("error closing EventsJoinerCache", e);
					}
					cache = null;
				}
			}
			super.doNotify(notification);
		}
	}
}
