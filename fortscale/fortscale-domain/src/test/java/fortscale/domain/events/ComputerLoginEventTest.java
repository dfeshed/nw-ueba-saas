package fortscale.domain.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ComputerLoginEventTest {

    @Test
    public void login_event_should_be_serialized_to_json_using_object_mapper() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ComputerLoginEvent event = new ComputerLoginEvent();
        event.setHostname("ddd");
        event.setIpaddress("1.1.1.1");
        event.setTimestampepoch(113L);

        String actual = mapper.writeValueAsString(event);
        assertEquals("{\"id\":null,\"createdAt\":null,\"timestampepoch\":113000,\"ipaddress\":\"1.1.1.1\",\"hostname\":\"ddd\",\"eventPriority\":2,\"partOfVpn\":false,\"expirationVpnSessiondt\":0}", actual);
    }

    @Test
    public void login_event_should_be_deserialized_from_json_using_object_mapper() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String message = "{\"id\":null,\"createdAt\":null,\"timestampepoch\":113000,\"ipaddress\":\"1.1.1.1\",\"hostname\":\"ddd\"}";
        ComputerLoginEvent actual = mapper.readValue(message, ComputerLoginEvent.class);

        assertNull(actual.getCreatedAt());
        assertEquals(new Long(113000), actual.getTimestampepoch());
        assertEquals("1.1.1.1", actual.getIpaddress());
        assertEquals("ddd", actual.getHostname());

    }

}
