package fortscale.streaming.task;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class MonitorEventSubscriber3 {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(MonitorEventSubscriber3.class);

    public MonitorEventSubscriber3(EPServiceProvider epService, long minTs, int sessionId) {
        this.epService = epService;
        this.minTs = minTs;
        this.sessionId = sessionId;
        epService.getEPAdministrator().getConfiguration().addVariable("maximalTimestamp"+sessionId, Long.class, Long.MAX_VALUE);
        epService.getEPAdministrator().createEPL("on TimestampEnd(sessionId = " + sessionId +") set maximalTimestamp"+sessionId+" = minimalTimeStamp");
        criticalEventStatement = epService.getEPAdministrator().createEPL(getStatement());
        criticalEventStatement.setSubscriber(this);
    }

    EPServiceProvider epService;
    /**
     * {@inheritDoc}
     */

    int i = 0;
    int counter = 0;
    long minTs;
    int sessionId;
    EPStatement criticalEventStatement;

    public String getStatement() {

        // Example of simple EPL with a Time Window
        return "select count(*) as c, eventId, timeOfReading from TemperatureEvent.win:expr_batch(newest_timestamp-oldest_timestamp > 600000 or (oldest_event.timeOfReading >= " + minTs + " and oldest_event.timeOfReading <= maximalTimestamp"+sessionId+" and newest_event.timeOfReading > maximalTimestamp"+sessionId+"),false) where timeOfReading >= " + minTs + " and timeOfReading <= maximalTimestamp"+sessionId+" order by timeOfReading";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map[] insertStream, Map[] removeStream) {
        criticalEventStatement.stop();
        i++;
        if (insertStream != null) {
            for (Map insertEventMap : insertStream) {
                Long c = (Long) insertEventMap.get("c");
                Long timeOfReading = (Long) insertEventMap.get("timeOfReading");
                int eventId = (Integer) insertEventMap.get("eventId");
                StringBuilder sb = new StringBuilder();
                sb.append("expression - sessionId: " + sessionId + " count: " + c + " received time eventId: " + eventId + " timeOfReading: " + new Date(timeOfReading) + " counter " + ++counter);

                logger.info(sb.toString());
                System.out.println(sb.toString());
            }
        }
        if (insertStream ==  null || insertStream.length == 0){
            System.out.println("expression - sessionId: " + sessionId + " count: 0");
        }
        criticalEventStatement.destroy();
    }
}
