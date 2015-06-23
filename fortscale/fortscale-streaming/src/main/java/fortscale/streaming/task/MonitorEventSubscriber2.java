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
public class MonitorEventSubscriber2 {

    /** Logger */
    private static Logger logger = LoggerFactory.getLogger(MonitorEventSubscriber2.class);

    public MonitorEventSubscriber2(EPServiceProvider epService) {
        EPStatement criticalEventStatement = epService.getEPAdministrator().createEPL(getStatement());
        criticalEventStatement.setSubscriber(this);
    }
    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        // Example of simple EPL with a Time Window
        //return "select avg(temperature) as avg_val from TemperatureEvent.win:keepall()";
        return "select eventId, timeOfReading from TemperatureEvent";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, Object> eventMap) {

        // average temp over 10 secs
        int eventId = (Integer) eventMap.get("eventId");
        Long timeOfReading = (Long) eventMap.get("timeOfReading");
        StringBuilder sb = new StringBuilder();
        sb.append("eventId: " + eventId + " timeOfReading: " + new Date(timeOfReading));

        logger.info(sb.toString());
        //System.out.println(sb.toString());
    }
}
