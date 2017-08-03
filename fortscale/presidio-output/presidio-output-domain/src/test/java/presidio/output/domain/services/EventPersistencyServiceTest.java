package presidio.output.domain.services;

import fortscale.common.general.EventResult;
import fortscale.common.general.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest()
//@ContextConfiguration
public class EventPersistencyServiceTest {

    @Autowired
    private EventPersistencyService eventPersistencyService;

//
//    @Before
//    public void before() {
//        esTemplate.deleteIndex(Alert.class);
//        esTemplate.createIndex(Alert.class);
//        esTemplate.putMapping(Alert.class);
//        esTemplate.refresh(Alert.class);
//    }

    @Test
    public void testSave() {
        Instant startDate = Instant.now();
        Instant endDate = Instant.now();
        EnrichedEvent event = new EnrichedEvent("eventId", Schema.FILE.toString(),
                "userId", "username", "userDisplayName", "dataSource", "oppType", new ArrayList<>(),
                EventResult.FAILURE, "resultCode", new HashMap<>());

        List<EnrichedEvent> events = new ArrayList<>();
        events.add(event);

        eventPersistencyService.store(Schema.FILE, events);
        //TODO- add asserts
    }

    @Configuration
    @Import({EventPersistencyServiceConfig.class})
    private class EventPersistencyServiceTestConfig  {

    }
}