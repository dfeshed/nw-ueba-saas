package fortscale.streaming.task;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import fortscale.domain.core.Alert;
import fortscale.domain.core.TimestampUpdate;
import org.apache.commons.lang.time.DateUtils;
import org.apache.samza.storage.kv.KeyValueStore;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class MonitorAlertSubscriber {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(MonitorAlertSubscriber.class);

    public MonitorAlertSubscriber(EPServiceProvider epService, KeyValueStore<String, Alert> store) {
        this.epService = epService;
        this.store = store;
        epService.getEPAdministrator().getConfiguration().addVariable("updateTimestamp", Long.class, maxPrevTime);
        epService.getEPAdministrator().createEPL("on TimestampUpdate set updateTimestamp = minimalTimeStamp");
        EPStatement criticalEventStatement = epService.getEPAdministrator().createEPL(getStatement());
        criticalEventStatement.setSubscriber(this);
    }

    EPServiceProvider epService;
    protected KeyValueStore<String, Alert> store;
    /**
     * {@inheritDoc}
     */

    int i = 0;
    Long maxPrevTime = 0l;
    int counter = 0;

    public String getStatement() {

        // Example of simple EPL with a Time Window
//        return "select id, startDate from EvidenceStream.win:ext_timed_batch(startDate, 2 min, 0L) where startDate > updateTimestamp order by startDate";
        return "select * from EvidenceStream.win:ext_timed_batch(startDate, 2 min, 0L) order by startDate";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map[] insertStream, Map[] removeStream) {
        Long maxTime = (Long) insertStream[insertStream.length - 1].get("startDate");
        maxPrevTime = DateUtils.ceiling(new Date(maxTime), Calendar.MINUTE).getTime();
//        epService.getEPRuntime().sendEvent(new TimestampUpdate(maxPrevTime));


        i++;
        if (insertStream != null) {
            List<Map> filterInsertStream = new ArrayList<>();
            for (Map insertEventMap : insertStream) {
                Long startDate = (Long) insertEventMap.get("startDate");
//                int eventId = (Integer) insertEventMap.get("eventId");
                StringBuilder sb = new StringBuilder();
                sb.append("received time frame:" + i +  " startDate: " + new Date(startDate) + " counter " + ++counter);

                logger.info(sb.toString());
                //System.out.println(sb.toString());

                //Store alert in mongoDB
//                store.put(alert.getId(), alert);
            }
        }

    }
}
