package fortscale.streaming.task;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import fortscale.streaming.task.messages.TimestampUpdate;
import org.apache.commons.lang.time.DateUtils;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class MonitorEventSubscriber {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(MonitorEventSubscriber.class);

    public MonitorEventSubscriber(EPServiceProvider epService) {
        this.epService = epService;
        epService.getEPAdministrator().getConfiguration().addVariable("updateTimestamp", Long.class, maxPrevTime);
        epService.getEPAdministrator().createEPL("on TimestampUpdate set updateTimestamp = minimalTimeStamp");
        EPStatement criticalEventStatement = epService.getEPAdministrator().createEPL(getStatement());
        criticalEventStatement.setSubscriber(this);
    }

    EPServiceProvider epService;
    /**
     * {@inheritDoc}
     */

    int i = 0;
    Long maxPrevTime = 0l;
    int counter = 0;

    public String getStatement() {

        // Example of simple EPL with a Time Window
        return "select eventId, timeOfReading from OrderTemperatureEvent.win:ext_timed_batch(timeOfReading, 2 min, 0L) where timeOfReading > updateTimestamp order by timeOfReading";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map[] insertStream, Map[] removeStream) {
        Long maxTime = (Long) insertStream[insertStream.length - 1].get("timeOfReading");
        maxPrevTime = DateUtils.ceiling(new Date(maxTime), Calendar.MINUTE).getTime();
        epService.getEPRuntime().sendEvent(new TimestampUpdate(maxPrevTime));

                /*if (insertStream != null) {
            List<Map> filterInsertStream = new ArrayList<>();
            for (Map insertEventMap : insertStream) {
                Long timeOfReading = (Long) insertEventMap.get("timeOfReading");
                if (timeOfReading > maxPrevTime) {
                    filterInsertStream.add(insertEventMap);
                    if (timeOfReading > maxCurrentTime){
                        maxCurrentTime = timeOfReading;
                    }
                }
                else{
                    int eventId = (Integer) insertEventMap.get("eventId");
                    StringBuilder sb = new StringBuilder();
                    sb.append("deleted time frame:" + i + "eventId: " + eventId + " timeOfReading: " + new Date(timeOfReading) + " counter " + ++counter);

                    logger.info(sb.toString());
                    System.out.println(sb.toString());
                }
            }*/
            i++;
            if (insertStream != null) {
            List<Map> filterInsertStream = new ArrayList<>();
            for (Map insertEventMap : insertStream) {
                Long timeOfReading = (Long) insertEventMap.get("timeOfReading");
                    int eventId = (Integer) insertEventMap.get("eventId");
                    StringBuilder sb = new StringBuilder();
                    sb.append("received time frame:" + i + "eventId: " + eventId + " timeOfReading: " + new Date(timeOfReading) + " counter " + ++counter);

                    logger.info(sb.toString());
                    //System.out.println(sb.toString());
                }
            }

    }
}
