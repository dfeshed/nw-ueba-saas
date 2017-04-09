package fortscale.collection.morphlines.commands;


import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;
import java.util.Collections;

import static fortscale.collection.jobs.event.process.DlpMailEventProcessJob.FORTSCALE_CONTROL_RECORD_EVENT_ID;
import static fortscale.collection.morphlines.commands.DlpMailEventsCache.EVENT_ID_FIELD_NAME;

@Configurable(preConstruction = true)
public class EventsCacherBuilder implements CommandBuilder {


    private static Logger logger = LoggerFactory.getLogger(EventsReducerBuilder.class);

    @Autowired
    private DlpMailEventsCache dlpMailEventsCache;

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("EventsCacher");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new EventsCacherBuilder.EventsCacher(this, config, parent, child, context);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    // /////////////////////////////////////////////////////////////////////////////
    public final class EventsCacher extends AbstractCommand {


        public EventsCacher(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
        }

        @Override
        protected boolean doProcess(Record inputRecord) {
            try {
                if (!isDummyRecord(inputRecord)) {
                    dlpMailEventsCache.addRecord(inputRecord);
                }
                return super.doProcess(inputRecord);
            } catch (Exception e) {
                logger.error("Failed to process record {}", inputRecord);
                return false;
            }
        }

        private boolean isDummyRecord(Record record) {
            final String eventId = (String) record.getFirstValue(EVENT_ID_FIELD_NAME);
            return eventId.equals(FORTSCALE_CONTROL_RECORD_EVENT_ID);
        }

    }

}
