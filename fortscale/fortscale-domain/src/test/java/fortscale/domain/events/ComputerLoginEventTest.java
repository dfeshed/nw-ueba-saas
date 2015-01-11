package fortscale.domain.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.datanucleus.util.StringUtils;
import org.junit.Test;

public class ComputerLoginEventTest {

    @Test
    public void login_event_should_be_serialized_to_json_using_object_mapper() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ComputerLoginEvent event = new ComputerLoginEvent();
        event.setHostname("ddd");
        event.setIpaddress("1.1.1.1");
        event.setTimestampepoch(113L);

        String actual = mapper.writeValueAsString(event);
        Assert.assertTrue(StringUtils.notEmpty(actual));
    }

}