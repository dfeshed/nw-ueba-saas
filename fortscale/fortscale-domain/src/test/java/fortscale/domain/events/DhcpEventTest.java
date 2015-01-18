package fortscale.domain.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.datanucleus.util.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;

public class DhcpEventTest {

    @Test
    public void dhcp_event_should_be_serialized_to_json_using_object_mapper() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        DhcpEvent event = new DhcpEvent();
        event.setAction("START");
        event.setADHostName(true);
        event.setExpiration(12333L);
        event.setCreatedAt(new DateTime());
        event.setIpaddress("1.1.1.1");


        String actual = mapper.writeValueAsString(event);
        Assert.assertTrue(StringUtils.notEmpty(actual));
    }

}