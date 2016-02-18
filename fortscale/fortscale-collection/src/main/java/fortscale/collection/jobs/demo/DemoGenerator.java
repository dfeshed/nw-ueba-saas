package fortscale.collection.jobs.demo;

import fortscale.utils.kafka.KafkaEventsWriter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Amir Keren on 17/02/2016.
 */
public class DemoGenerator {

    private List<Scenario> scenarios;

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    /**
     *
     * This method generates the demo scenarios
     *
     * @param streamWriter
     */
    public void generateDemo(KafkaEventsWriter streamWriter) throws Exception {
        DateTime anomalyDate = new DateTime().withZone(DateTimeZone.UTC)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        List<JSONObject> records = new ArrayList();
        for (Scenario scenario: scenarios) {
            //TODO - set global params
            //scenario.setGlobalParams();
            records.addAll(scenario.generateScenario());
        }
        //forward events to create buckets
        Collections.sort(records, new JSONComparator());
        for (JSONObject record: records) {
            streamWriter.send(null, record.toJSONString(JSONStyle.NO_COMPRESS));
        }
        long endTime = anomalyDate.plusDays(1).plusSeconds(1).getMillis() / 1000;
        String dummyEvent = "{\"date_time_unix\":" + endTime + ",\"data_source\":\"dummy\"}";
        streamWriter.send(null, dummyEvent);
    }

}