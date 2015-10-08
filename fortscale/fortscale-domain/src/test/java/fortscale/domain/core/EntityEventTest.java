package fortscale.domain.core;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EntityEventTest {
    private final static String ENTITY_EVENT_JSON = "{\"start_time_unix\":1435176000,\"contextId\":\"normalized_username_normalized_username_14060866\",\"baseScore\":0.0,\"entity_event_value\":0.0,\"event_type\":\"entity_event\",\"score\":0.0,\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"end_time_unix\":1435179599,\"creation_epochtime\":1444297230,\"entity_event_type\":\"normalized_username_hourly\",\"date_time_unix\":1435179599,\"aggregated_feature_events\":[{\"creation_date_time\":\"2015-10-08 09:33:53\",\"aggregated_feature_value\":0.0,\"event_type\":\"aggr_event\",\"data_source\":\"aggr_event.normalized_username_vpn_session_hourly.number_of_high_score_rate_vpnsession_hourly\",\"score\":null,\"aggregated_feature_type\":\"P\",\"data_sources\":[\"vpn_session\"],\"creation_epochtime\":1444296833,\"date_time_unix\":1435179599,\"start_time_unix\":1435176000,\"end_time\":\"2015-06-24 20:59:59\",\"bucket_conf_name\":\"normalized_username_vpn_session_hourly\",\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"start_time\":\"2015-06-24 20:00:00\",\"aggregated_feature_name\":\"number_of_high_score_rate_vpnsession_hourly\",\"end_time_unix\":1435179599,\"aggregated_feature_info\":{\"total\":0}}]}";

    @Test
    public void test_deserialization() throws Exception{
        JSONObject entityEventJsonObj = (JSONObject)JSONValue.parse(ENTITY_EVENT_JSON);
        EntityEvent entityEvent = EntityEvent.buildEntityEvent(entityEventJsonObj);
        Assert.assertEquals(1435176000, entityEvent.getStart_time_unix());
        Assert.assertEquals("normalized_username_normalized_username_14060866", entityEvent.getContextId());
        Assert.assertEquals(0, Double.compare(0.0, entityEvent.getBase_score()));
        Assert.assertEquals(0, Double.compare(0.0, entityEvent.getEntity_event_value()));
        Assert.assertEquals(0, Double.compare(0.0, entityEvent.getScore() ));
        Date endTime = new Date(1435179599000L);
        Assert.assertEquals(endTime, entityEvent.getEnd_time());
        Map<String, String> context = new HashMap<>();
        context.put("normalized_username", "normalized_username_14060866");
        Assert.assertEquals(context, entityEvent.getContext());
        Assert.assertNotNull(entityEvent.getAggregated_feature_events());

    }
}
