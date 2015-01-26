package fortscale.domain.events;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.datanucleus.util.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class DhcpEventTest {

    @Test
    public void dhcp_event_should_be_serialized_to_json_using_object_mapper() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        DhcpEvent event = new DhcpEvent();
        event.setAction("START");
        event.setADHostName(true);
        event.setExpiration(12333L);
        event.setCreatedAt(new DateTime(1422189771865L));
        event.setIpaddress("1.1.1.1");
        event.setTimestampepoch(1420963260000L);


        String actual = mapper.writeValueAsString(event);
        Assert.assertTrue(StringUtils.notEmpty(actual));
        Assert.assertEquals("{\"id\":null,\"createdAt\":1422189771865,\"timestampepoch\":1420963260000,\"ipaddress\":\"1.1.1.1\",\"hostname\":null,\"macAddress\":null,\"expiration\":12333000,\"action\":\"START\",\"adhostName\":true}", actual);
    }

    @Test
    public void dhcp_event_should_be_deserialized_from_json_using_object_mapper() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String message = "{\"id\":null,\"createdAt\":1422189771865,\"timestampepoch\":1420963260000,\"ipaddress\":\"1.1.1.1\",\"hostname\":null,\"macAddress\":null,\"expiration\":12333000,\"action\":\"START\",\"adhostName\":true}";
        DhcpEvent actual = mapper.readValue(message, DhcpEvent.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(new DateTime(1422189771865L), actual.getCreatedAt());
    }

}