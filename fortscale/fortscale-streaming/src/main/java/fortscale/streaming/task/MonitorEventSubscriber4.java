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
public class MonitorEventSubscriber4 {

    /** Logger */
    private static Logger logger = LoggerFactory.getLogger(MonitorEventSubscriber4.class);

    public MonitorEventSubscriber4(EPServiceProvider epService) {
        EPStatement criticalEventStatement = epService.getEPAdministrator().createEPL(getStatement());
        criticalEventStatement.setSubscriber(this);
    }

    /**
     * {@inheritDoc}
     */
    public String getStatement() {

        // Example of simple EPL with a Time Window
        //return "select avg(temperature) as avg_val from TemperatureEvent.win:keepall()";
        return "select * from pattern @DiscardPartialsOnMatch [every a=TemperatureEvent -> b=TemperatureEvent(eventId = a.eventId+5) ]";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, Object> eventMap) {

        // average temp over 10 secs
        int eventId1 = (Integer) eventMap.get("aid");
        Long timeOfReading1 = (Long) eventMap.get("atime");
        int eventId2 = (Integer) eventMap.get("bid");
        Long timeOfReading2 = (Long) eventMap.get("btime");
        StringBuilder sb = new StringBuilder();
        sb.append("a eventId: " + eventId1 + " a timeOfReading: " + new Date(timeOfReading1) + " b eventId: " + eventId2 + " b timeOfReading: " + new Date(timeOfReading2));

        logger.info(sb.toString());
        System.out.println(sb.toString());
    }
}
