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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static fortscale.collection.morphlines.RecordExtensions.getLongValue;
import static fortscale.utils.time.TimestampUtils.convertToSeconds;

/**
 * Reduce = Keep the first (or first after cache threshold expired) instance in the cache (compare by 'keys' field) and continue processing otherwise filter the event
 * we 'reduce' events when we have a few similar (by 'keys') events and we only need to process 1 of these events
 */
public class EventsReducerBuilder implements CommandBuilder {

    private static Logger logger = LoggerFactory.getLogger(EventsReducerBuilder.class);

    @Autowired
    private MorphlineConfigService morphlineConfigService;

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("EventsReducer");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new EventsReducerBuilder.EventsReducer(this, config, parent, child, context);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    public final class EventsReducer extends AbstractCommand {
        private List<String> keys;
        private String currentRecordDateField;
        private String cachedRecordDateField;
        private long timeThreshold;
        private boolean dropFromCache;
        private EventsJoinerCache cache;
        private boolean processRecord;
        private Long cacheRecordTtl;
        private final String cacheName;


        private long minimalRecordThreshold = Long.MAX_VALUE;
        private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();


        public EventsReducer(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            keys = getConfigs().getStringList(config, "keys");
            currentRecordDateField = getConfigs().getString(config, "currentRecordDateField", "1");
            cachedRecordDateField = getConfigs().getString(config, "cachedRecordDateField", "2");
            timeThreshold = getConfigs().getLong(config, "timeThreshold", 9999999L);
            cacheRecordTtl = -1L;
            try {
                cacheRecordTtl = new Long(morphlineConfigService.getStringValue(getConfigs(), config, "cacheRecordTtlPropertyName"));
            }
            catch (Exception e){
                logger.warn("Error getting \"cacheRecordTtlPropertyName\" property from configuration, setting value to -1");
            }
            dropFromCache = getConfigs().getBoolean(config, "dropFromCache");
            processRecord =  getConfigs().getBoolean(config, "processRecord",false);
            cacheName = getConfigs().getString(config, "cacheName");
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
            if (cacheRecordTtl != -1  && minimalRecordThreshold > currentTime) {
                minimalRecordThreshold = currentTime;
                cache.setDeprecationTs(minimalRecordThreshold -cacheRecordTtl);
            }

            if (previousEvent==null) {
                // store the record for later merge
                logger.debug("Storing event {} in cache {} since there were no similar events before.", inputRecord, cache);
                cache.store(key, inputRecord);
                commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.SAVED_TO_CACHE);
                if (morphlineMetrics != null) {
                    morphlineMetrics.eventSavedToCache++;
                }

                // mark command as successful, do not pass the record
                // to chained child command to halt execution

                if(processRecord) {
                    return super.doProcess(inputRecord);
                }
                else {
                    return true;
                }
            } else {

                long delta = Math.abs(cachedTime - currentTime);

                if (delta > timeThreshold)  {
                    // replace the cached record as we encountered a new one that should be saved
                    // if the delta is greater than the threshold we assume that the new events belongs
                    // to a new session
                    logger.debug("Storing event {} in cache {} since delta > timeThreshold.", inputRecord, cache);
                    cache.store(key, inputRecord);

                    if (processRecord) {
                        return super.doProcess(inputRecord);
                    }
                    if (morphlineMetrics != null) {
                        morphlineMetrics.deltaGreaterThenThreshold++;
                    }
                    commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.DELTA_GREATER_THEN_THRESHOLD);
                    return true;
                }
                else {
                    logger.info("Filtering event {} since similar event {} is already in the cache.", inputRecord, previousEvent);
                    //Drop record
                    commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.REDUCED_EVENT);
                    if (morphlineMetrics != null) {
                        morphlineMetrics.reducedEvents++;
                    }
                    return true;
                }
            }
        }

        @Override
        protected void doNotify(Record notification) {
            for (Object event : Notifications.getLifecycleEvents(notification)) {
                if (event == Notifications.LifecycleEvent.SHUTDOWN && cache!=null) {
                    try {
                        logger.info("Closing cache {}.", cacheName);
                        cache.close();
                    } catch (IOException e) {
                        logger.error("error closing Events Reducer Cache {}.", cacheName, e);
                    }
                    cache = null;
                }
            }
            super.doNotify(notification);
        }
    }
}
