package fortscale.collection.morphlines.commands;


import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.MorphlineConfigService;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static fortscale.collection.morphlines.RecordExtensions.getLongValue;
import static fortscale.utils.time.TimestampUtils.convertToSeconds;

/**
 * This command is responsible for counting events that match certain constraints (example - in dlpmail we count the number of recipients (for a single event_id))
 */
public class EventsCounterBuilder implements CommandBuilder {

    public static final String EVENT_TYPE_FIELD_NAME = "event_type";
    public static final String EVENT_ID_FIELD_NAME = "event_id";
    public static final String NUM_OF_RECIPIENTS_FIELD_NAME = "num_of_recipients";
    public static final String FORTSCALE_CONTROL_EVENT_ID = "Fortscale Control";
    public static final String MESSAGE_BODY_FIELD_NAME = "message body";
    public static final String KEY_DELIMITER = "_";
    private static Logger logger = LoggerFactory.getLogger(EventsReducerBuilder.class);

    @Autowired
    private MorphlineConfigService morphlineConfigService;

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("EventsCounter");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new EventsCounterBuilder.EventsCounter(this, config, parent, child, context);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    public final class EventsCounter extends AbstractCommand {
        private List<String> keys;
        private boolean processRecord;

        private Pair<String, Record> eventsCache = new MutablePair<>();
        private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();


        public EventsCounter(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            keys = getConfigs().getStringList(config, "keys");
            processRecord =  getConfigs().getBoolean(config, "processRecord",true);
        }

        @Override
        protected void doNotify(Record notification) {
            for (Object event : Notifications.getLifecycleEvents(notification)) {
                if (event == Notifications.LifecycleEvent.SHUTDOWN ) {
                    logger.info("Clearing cache.");
                    eventsCache = new MutablePair<>();
                }
            }
            super.doNotify(notification);
        }

        @Override
        protected boolean doProcess(Record inputRecord) {
            final Record previousEvent = eventsCache.getValue();

            if (isControlEvent(inputRecord)) {
                logger.debug("got fortscale control event.");
                eventsCache = new MutablePair<>();
                return continueProcessEvent(previousEvent);
            }

            MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

            final String key = getKey(inputRecord);

            if (isNewEvent(key)) {
                if (previousEvent != null) {
                    return handleFirstEventOfItsKind(inputRecord, previousEvent, morphlineMetrics);
                }
                else {
                    return handleFirstEventInFile(inputRecord, morphlineMetrics);
                }

            }
            else {
                return handleNotFirst(inputRecord, previousEvent, morphlineMetrics);
            }
        }

        private boolean handleFirstEventOfItsKind(Record inputRecord, Record previousEvent, MorphlineMetrics morphlineMetrics) {
            logger.debug("Handling first event {} of its kind (key).", inputRecord);
            saveEventToCache(inputRecord, morphlineMetrics);
            return continueProcessEvent(previousEvent);
        }

        private boolean handleNotFirst(Record inputRecord, Record previousEvent, MorphlineMetrics morphlineMetrics) {
            final String newEventType = (String) inputRecord.getFirstValue(EVENT_TYPE_FIELD_NAME);
            final boolean isNewEventMessageBody = newEventType != null && newEventType.equals(MESSAGE_BODY_FIELD_NAME);

            boolean handledSuccessfully;
            if (isNewEventMessageBody) {
                handledSuccessfully = handleNotFirstMessageBody(inputRecord, previousEvent, morphlineMetrics);
            }
            else {
                handledSuccessfully = handleNotFirstRecipient(inputRecord, previousEvent);
            }

            return handledSuccessfully;
        }

        private boolean handleNotFirstMessageBody(Record inputRecord, Record previousEvent, MorphlineMetrics morphlineMetrics) {
            logger.debug("Handling not-first message-body-event {}.", inputRecord);
            final String previousEventType = (String) previousEvent.getFirstValue(EVENT_TYPE_FIELD_NAME);
            final boolean isPreviousEventMessageBody = previousEventType != null && previousEventType.equals(MESSAGE_BODY_FIELD_NAME);
            if (isPreviousEventMessageBody) {
                logger.error("Error with counting command. there were 2 message bodies for event_id {}", previousEvent.getFields().get(EVENT_ID_FIELD_NAME).get(0));
                return false;
            }
            else { //current is message body & previous is recipient
                String count = (String) previousEvent.getFirstValue(NUM_OF_RECIPIENTS_FIELD_NAME);
                previousEvent.replaceValues("num_of_recipients", ""); //reset the recipient event (all recipient events have empty num_of_recipients)
                inputRecord.replaceValues("num_of_recipients", count);
                saveEventToCache(inputRecord, morphlineMetrics);
            }
            return continueProcessEvent(previousEvent);
        }

        private boolean handleNotFirstRecipient(Record inputRecord, Record previousEvent) {
            logger.debug("Handling not-first recipient-event {}.", inputRecord);
            String countAsString = (String) previousEvent.getFirstValue(NUM_OF_RECIPIENTS_FIELD_NAME);
            Integer count = Integer.parseInt(countAsString);

            previousEvent.replaceValues(NUM_OF_RECIPIENTS_FIELD_NAME, String.valueOf(count+1)); // increase the count by 1

            return continueProcessEvent(inputRecord);
        }

        private boolean handleFirstEventInFile(Record inputRecord, MorphlineMetrics morphlineMetrics) {
            logger.debug("Handling first event {} in file.", inputRecord);

            saveEventToCache(inputRecord, morphlineMetrics);
            return true;
        }

        private boolean isNewEvent(String key) {
            final String currentKeyInCache = eventsCache.getKey();
            return currentKeyInCache == null || !key.equals(currentKeyInCache);
        }

        private boolean isControlEvent(Record inputRecord) {
            final String key = getKey(inputRecord);
            return key.equals(FORTSCALE_CONTROL_EVENT_ID);
        }

        private String getKey(Record inputRecord) {
            StringJoiner stringJoiner = new StringJoiner(KEY_DELIMITER);
            for (String key : keys) {
                final String keyValue = (String) inputRecord.getFirstValue(key);
                stringJoiner.add(keyValue);
            }

            return stringJoiner.toString();
        }

        private void saveEventToCache(Record inputRecord, MorphlineMetrics morphlineMetrics) {
            logger.debug("Storing event {} in cache.", inputRecord);
            final String eventType = (String) inputRecord.getFirstValue(EVENT_TYPE_FIELD_NAME);
            final boolean isInputMessageBody = eventType != null && eventType.equals(MESSAGE_BODY_FIELD_NAME);
            if (isInputMessageBody) {
                if (inputRecord.getFirstValue(NUM_OF_RECIPIENTS_FIELD_NAME).equals("")) {
                    inputRecord.replaceValues(NUM_OF_RECIPIENTS_FIELD_NAME, "0");
                }
            }
            else { //recipient
                inputRecord.replaceValues(NUM_OF_RECIPIENTS_FIELD_NAME, "1");
            }

            final String key = (String) inputRecord.getFirstValue(EVENT_ID_FIELD_NAME);
            eventsCache = new MutablePair<>(key, inputRecord);
            commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.SAVED_TO_CACHE);
            if (morphlineMetrics != null) {
                morphlineMetrics.eventSavedToCache++;
            }
        }

        private boolean continueProcessEvent(Record event) {
            if (processRecord) {
                return super.doProcess(event);
            }
            else {
                return true;
            }
        }
    }




}

