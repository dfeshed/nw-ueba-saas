package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;

public class EntityEventIDFixedPrefixGeneratorTest {

    /***
     * EventID has fixed format:
     *      EV-<event seq number>-<username>
     *
     * event seq number - sequence,  auto increment from generator creation
     * username - must be provided in constructor
     */
    @Test
    public void EventIDGenerator1Test() {
        String expected_EventID = "EV-1-dlpuser23";

        EntityEventIDFixedPrefixGenerator EIDG = new EntityEventIDFixedPrefixGenerator("dlpuser23");
        String actual_EventID = EIDG.getNext();

        Assert.assertEquals(actual_EventID, expected_EventID);
    }
}
